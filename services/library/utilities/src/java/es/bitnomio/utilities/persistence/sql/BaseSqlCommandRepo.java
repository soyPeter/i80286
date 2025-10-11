package es.bitnomio.utilities.persistence.sql;

import es.bitnomio.utilities.utils.MDCUtils;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The `BaseSqlCommandRepo` is an abstract class that provides foundational SQL command operations for subclasses via the
 * `JdbcOperations` class. This class provides generic methods for performing SQL commands like insert, update, delete,
 * and upsert and returning results as an entity of type `T`.
 *
 * @param <T> the type of entity this repository operates on
 */
public abstract class BaseSqlCommandRepo<T, ID> implements CrudRepository<T, ID> {

  protected final JdbcOperations jdbcOperations;
  protected final Class<T> modelClass;
  protected final String tableName;

  private static String BASE_UPDATE_STATEMENT;
  private static String BASE_SOFT_DELETE_STATEMENT;
  private static String BASE_DELETE_STATEMENT;

  private static final Logger LOGGER = LoggerFactory.getLogger(BaseSqlCommandRepo.class);

  /**
   * Constructor to initialize the JDBC operations and model class.
   *
   * @param jdbcOperations the JDBC operations to be used for query executions
   * @param modelClass     the class of the model
   */
  protected BaseSqlCommandRepo(JdbcOperations jdbcOperations, Class<T> modelClass) {
    this.jdbcOperations = jdbcOperations;
    this.modelClass = modelClass;
    this.tableName = modelClass.getSimpleName().toLowerCase(); // Assumes table name is the simple class name in lowercase

    BASE_UPDATE_STATEMENT = "UPDATE " + tableName + " ";
    BASE_SOFT_DELETE_STATEMENT = "UPDATE " + tableName + " SET deletedAt = ?, deletedBy = ? ";
    BASE_DELETE_STATEMENT = "DELETE FROM " + tableName;
  }

  /**
   * Saves an entity using the default repository save mechanism.
   *
   * This method provides a simple wrapper around the default save operation, ensuring that the entity is saved to the
   * database. The method is annotated with {@code @Transactional}, which means that the operation is executed within a
   * transaction context.
   *
   * @param model the entity to be saved
   */
  @Transactional
  public T saveModel(T model) {
    LOGGER.info("Saving entity");
    return save(model);
  }

  /**
   * Updates an existing entity using the default repository update mechanism.
   *
   * This method wraps the default update operation, ensuring that the provided entity is updated in the database. It
   * logs the update process and is annotated with {@code @Transactional}, meaning that the operation is executed within
   * a transaction context.
   *
   * @param model the entity to be updated
   */
  public T updateModel(T model) {
    LOGGER.info("Updating entity");
    return update(model);
  }

  /**
   * Saves an entity using the provided insert statement.
   *
   * @param model           the entity to be saved
   * @param insertStatement the SQL insert statement
   */
  @Transactional
  public void saveRaw(T model, String insertStatement) {
    LOGGER.info("Saving entity using statement: {}", insertStatement);

    jdbcOperations.prepareStatement(insertStatement, (PreparedStatement stmt) -> {
      bindModelToStatement(stmt, model);
      int result = stmt.executeUpdate();
      LOGGER.info("Entity saved successfully.");
      return result;
    });
  }

  /**
   * Saves an entity and returns the generated ID.
   *
   * @param model           the entity to be saved
   * @param insertStatement the SQL insert statement
   * @return the generated ID of the saved entity
   */
  @Transactional
  public Long saveRawAndReturnId(T model, String insertStatement) {
    LOGGER.info("Saving entity and returning generated ID using statement: {}", insertStatement);

    return jdbcOperations.prepareStatement(insertStatement, (PreparedStatement stmt) -> {
      bindModelToStatement(stmt, model);
      stmt.executeUpdate();
      ResultSet generatedKeys = stmt.getGeneratedKeys();
      if (generatedKeys.next()) {
        Long id = generatedKeys.getLong(1);
        LOGGER.info("Entity saved successfully with generated ID: {}", id);
        return id;
      }
      LOGGER.warn("No ID generated during save.");
      return Long.MIN_VALUE;
    });
  }

  /**
   * Updates an entity by its ID.
   *
   * @param model the parameters to be updated
   * @param id    the ID of the entity to be updated
   * @return the number of rows affected
   */
  @Transactional
  public Integer updateById(Map<String, Object> model, Long id) {
    String finalUpdateStatement = BASE_UPDATE_STATEMENT + buildUpdateStatement(model) + " WHERE id = ?";
    LOGGER.info("Updating entity by ID using statement: {}", finalUpdateStatement);

    model.put("updated_by", MDCUtils.getUser());

    return jdbcOperations.prepareStatement(finalUpdateStatement, (PreparedStatement stmt) -> {
      bindMapToStatement(stmt, model);
      stmt.setLong(model.size() + 1, id);
      int rowsAffected = stmt.executeUpdate();
      LOGGER.info("Updated {} rows.", rowsAffected);
      return rowsAffected;
    });
  }

  /**
   * Updates an entity by its UUID.
   *
   * @param model the parameters to be updated
   * @param id    the UUID of the entity to be updated
   * @return the number of rows affected
   */
  @Transactional
  public Integer updateByUuid(Map<String, Object> model, UUID id) {
    String finalUpdateStatement = BASE_UPDATE_STATEMENT + buildUpdateStatement(model) + " WHERE uuid = ?";
    LOGGER.info("Updating entity by UUID using statement: {}", finalUpdateStatement);

    model.put("updated_by", MDCUtils.getUser());

    return jdbcOperations.prepareStatement(finalUpdateStatement, (PreparedStatement stmt) -> {
      bindMapToStatement(stmt, model);
      stmt.setObject(model.size() + 1, id);
      int rowsAffected = stmt.executeUpdate();
      LOGGER.info("Updated {} rows.", rowsAffected);
      return rowsAffected;
    });
  }

  /**
   * Performs an upsert operation using Micronaut Data JDBC standards. Tries to update first; if no rows are affected,
   * performs an insert.
   *
   * @param sqlStatements     a map containing the SQL statements (update and insert) with their respective WHERE clauses
   * @param model             the entity to be upserted
   * @param whereClauseParams the parameters for the WHERE clauses
   */
  @Transactional
  public void upsert(Map<String, String> sqlStatements, T model, Map<String, Object> whereClauseParams) {
    String updateStatement = sqlStatements.get("update");
    String insertStatement = sqlStatements.get("insert");

    LOGGER.info("Attempting to update entity using statement: {}", updateStatement);

    int rowsAffected = jdbcOperations.prepareStatement(updateStatement, (PreparedStatement stmt) -> {
      bindModelToStatement(stmt, model);
      bindMapToStatement(stmt, whereClauseParams, modelClass.getDeclaredFields().length + 1);
      return stmt.executeUpdate();
    });

    if (rowsAffected == 0) {
      LOGGER.info("No rows affected by update; performing insert using statement: {}", insertStatement);
      jdbcOperations.prepareStatement(insertStatement, (PreparedStatement stmt) -> {
        bindModelToStatement(stmt, model);
        stmt.executeUpdate();
        LOGGER.info("Entity inserted successfully.");
        return null;
      });
    } else {
      LOGGER.info("Entity updated successfully.");
    }
  }

  /**
   * Deletes an entity by its ID.
   *
   * @param id        the ID of the entity to be deleted
   */
  @Transactional
  public void deleteById(Long id) {
    String deleteStatement = BASE_SOFT_DELETE_STATEMENT + " WHERE id = ?";
    LOGGER.info("Deleting entity by ID using statement: {}", deleteStatement);

    jdbcOperations.prepareStatement(deleteStatement, (PreparedStatement stmt) -> {
      stmt.setLong(1, id);
      stmt.executeUpdate();
      LOGGER.info("Entity deleted successfully.");
      return null;
    });
  }

  /**
   * Deletes an entity by its UUID.
   *
   * @param id        the UUID of the entity to be deleted
   */
  @Transactional
  public void deleteByUuid(UUID id) {
    String deleteStatement = BASE_SOFT_DELETE_STATEMENT + " WHERE uuid = ?";
    LOGGER.info("Deleting entity by UUID using statement: {}", deleteStatement);

    jdbcOperations.prepareStatement(deleteStatement, (PreparedStatement stmt) -> {
      stmt.setObject(1, id);
      stmt.executeUpdate();
      LOGGER.info("Entity deleted successfully.");
      return null;
    });
  }

  /**
   * Deletes an entity using the provided parameters to compose the WHERE clause.
   *
   * @param deleteStatement the SQL delete statement
   * @param whereParams     the parameters for the WHERE clause
   */
  @Transactional
  public void deleteByComposedKey(String deleteStatement, Map<String, Object> whereParams) {
    LOGGER.info("Deleting entity using composed key with statement: {}", deleteStatement);

    jdbcOperations.prepareStatement(deleteStatement, (PreparedStatement stmt) -> {
      int index = 1;
      for (Map.Entry<String, Object> entry : whereParams.entrySet()) {
        stmt.setObject(index++, entry.getValue());
      }
      stmt.executeUpdate();
      LOGGER.info("Entity deleted successfully.");
      return null;
    });
  }

  /**
   * Restores (un-deletes) an entity by its ID.
   *
   * @param id        the ID of the entity to be restored
   */
  @Transactional
  public void restoreById(Long id) {
    String restoreStatement = BASE_UPDATE_STATEMENT + " SET deletedAt = null, deletedBy = null, updatedBy = ? WHERE id = ?";
    LOGGER.info("Restoring entity by ID using statement: {}", restoreStatement);

    jdbcOperations.prepareStatement(restoreStatement, (PreparedStatement stmt) -> {
      stmt.setObject(1, MDCUtils.getUser());
      stmt.setObject(2, id);
      stmt.executeUpdate();
      LOGGER.info("Entity restored successfully.");
      return null;
    });
  }

  /**
   * Restores (un-deletes) an entity by its UUID.
   *
   * @param id        the UUID of the entity to be restored
   */
  @Transactional
  public void restoreByUuid(UUID id) {
    String restoreStatement = BASE_UPDATE_STATEMENT + " SET deletedAt = null, deletedBy = null, updatedBy = ? WHERE uuid = ?";
    LOGGER.info("Restoring entity by UUID using statement: {}", restoreStatement);

    jdbcOperations.prepareStatement(restoreStatement, (PreparedStatement stmt) -> {
      stmt.setObject(1, MDCUtils.getUser());
      stmt.setObject(2, id);
      stmt.executeUpdate();
      LOGGER.info("Entity restored successfully.");
      return null;
    });
  }

  /**
   * Permanently deletes an entity by its ID.
   *
   * @param id        the ID of the entity to be permanently deleted
   */
  @Transactional
  public void destroyById(Long id) {
    String destroyStatement = BASE_DELETE_STATEMENT + " WHERE id = ?";
    LOGGER.info("Permanently deleting entity by ID using statement: {} by user: {}", destroyStatement, MDCUtils.getUser());

    jdbcOperations.prepareStatement(destroyStatement, (PreparedStatement stmt) -> {
      stmt.setLong(1, id);
      stmt.executeUpdate();
      LOGGER.info("Entity permanently deleted.");
      return null;
    });
  }

  /**
   * Permanently deletes an entity by its UUID.
   *
   * @param id        the UUID of the entity to be permanently deleted
   */
  @Transactional
  public void destroyByUuid(UUID id) {
    String destroyStatement = BASE_DELETE_STATEMENT + " WHERE uuid = ?";
    LOGGER.info("Permanently deleting entity by UUID using statement: {}, by user: {}", destroyStatement, MDCUtils.getUser());

    jdbcOperations.prepareStatement(destroyStatement, (PreparedStatement stmt) -> {
      stmt.setObject(1, id);
      stmt.executeUpdate();
      LOGGER.info("Entity permanently deleted.");
      return null;
    });
  }

  /**
   * Saves a batch of entities using the provided insert statement.
   *
   * @param modelList       the list of entities to be saved
   * @param insertStatement the SQL insert statement
   * @return array of update counts as returned by `executeBatch()`
   */
  @Transactional
  public int[] saveBatch(List<T> modelList, String insertStatement) {
    LOGGER.info("Saving a batch of entities using statement: {}", insertStatement);

    return jdbcOperations.prepareStatement(insertStatement, (PreparedStatement stmt) -> {
      for (T model : modelList) {
        bindModelToStatement(stmt, model);
        stmt.addBatch();
      }
      int[] result = stmt.executeBatch();
      LOGGER.info("Batch save completed.");
      return result;
    });
  }

  /**
   * Saves a batch of entities in chunks using the provided insert statement.
   *
   * @param chunkSize       the size of each chunk in the batch
   * @param modelList       the list of entities to be saved
   * @param insertStatement the SQL insert statement
   * @return combined array of update counts as returned by `executeBatch()`
   */
  @Transactional
  public int[] saveBatch(int chunkSize, List<T> modelList, String insertStatement) {
    LOGGER.info("Saving a batch of entities in chunks of {} using statement: {}", chunkSize, insertStatement);

    return jdbcOperations.prepareStatement(insertStatement, (PreparedStatement stmt) -> {
      int count = 0;
      int[] results = new int[0]; // Placeholder to collect batch results
      for (T model : modelList) {
        bindModelToStatement(stmt, model);
        stmt.addBatch();
        if (++count % chunkSize == 0) {
          int[] batchResult = stmt.executeBatch();
          results = combineArrays(results, batchResult);
        }
      }
      int[] finalBatchResult = stmt.executeBatch();
      LOGGER.info("Batch save in chunks completed.");
      return combineArrays(results, finalBatchResult);
    });
  }

  /**
   * Deletes a batch of entities using the provided delete statement.
   *
   * @param modelList       the list of entities to be deleted
   * @param deleteStatement the SQL delete statement
   * @return array of update counts as returned by `executeBatch()`
   */
  @Transactional
  public int[] deleteBatchById(List<T> modelList, String deleteStatement) {
    LOGGER.info("Deleting a batch of entities by ID using statement: {}", deleteStatement);

    return jdbcOperations.prepareStatement(deleteStatement, (PreparedStatement stmt) -> {
      for (T model : modelList) {
        bindModelToStatement(stmt, model);
        stmt.addBatch();
      }
      int[] result = stmt.executeBatch();
      LOGGER.info("Batch delete completed.");
      return result;
    });
  }

  /**
   * Binds a model to a PreparedStatement.
   *
   * @param stmt  the prepared statement
   * @param model the entity model
   * @throws SQLException if there's an error while binding the parameters
   */
  private void bindModelToStatement(PreparedStatement stmt, T model) throws SQLException {
    var components = model.getClass().getRecordComponents();

    try {
      int index = 1;
      for (var component : components) {
        stmt.setObject(index++, component.getAccessor().invoke(model));
      }
    }
    catch (ReflectiveOperationException e) {
      throw new SQLException("Error binding model to statement", e);
    }
  }

  /**
   * Binds the elements of the model map to a PreparedStatement.
   *
   * @param stmt  the prepared statement
   * @param model the map of parameters to be bound
   * @throws SQLException if there's an error while binding the parameters
   */
  private void bindMapToStatement(PreparedStatement stmt, Map<String, Object> model) throws SQLException {
    int index = 1;
    for (Map.Entry<String, Object> entry : model.entrySet()) {
      stmt.setObject(index++, entry.getValue());
    }
  }

  /**
   * Overloaded method to bindMapToStatement with offset.
   *
   * @param stmt   the prepared statement
   * @param model  the map of parameters to be bound
   * @param offset the starting index for binding parameters
   * @throws SQLException if there's an error while binding the parameters
   */
  private void bindMapToStatement(PreparedStatement stmt, Map<String, Object> model, int offset) throws SQLException {
    int index = offset;
    for (Map.Entry<String, Object> entry : model.entrySet()) {
      stmt.setObject(index++, entry.getValue());
    }
  }

  /**
   * Combines two integer arrays into one.
   *
   * @param first  the first array
   * @param second the second array
   * @return the combined array
   */
  private int[] combineArrays(int[] first, int[] second) {
    int[] result = new int[first.length + second.length];
    System.arraycopy(first, 0, result, 0, first.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  /**
   * Constructs the SQL update statement by iterating over the given set of fields.
   * Each field will be included in the update clause of the statement.
   *
   * @param model the map of fields to be updated in the SQL update statement
   * @return the constructed SQL update statement as a string
   */
  private String buildUpdateStatement(Map<String, Object> model) {
    StringBuilder statementBuilder = new StringBuilder(BASE_UPDATE_STATEMENT);
    boolean first = true;

    for (String field : model.keySet()) {
      if (!first) {
        statementBuilder.append(", ");
      } else {
        first = false;
      }
      statementBuilder.append(field).append(" = ?");
    }

    statementBuilder.append(" ");
    return statementBuilder.toString();
  }
}
