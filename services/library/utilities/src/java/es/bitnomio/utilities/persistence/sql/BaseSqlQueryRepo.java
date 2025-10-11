/*
 * COPYRIGHT 2020 -2024 original authors
1 *
 * bitnomio-spectrum - Created by backend@bitnomio
 * Date: 5/11/24 Time: 12:25
 *
 */
package es.bitnomio.utilities.persistence.sql;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.GenericRepository;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The `BaseSqlQueryRepo` is an abstract class that provides foundational SQL query operations for subclasses via the
 * `JdbcOperations` class. This class provides generic methods for executing SQL queries and returning results as an
 * entity of entity `T`.
 *
 * @param <T> the entity of entity this repository operates on
 */
//@JdbcRepository(value = "default", dialect = Dialect.POSTGRES)
//@Transactional(readOnly = true, rollbackFor = Exception.class)
public abstract class BaseSqlQueryRepo<T> {

  public final static Logger LOG = LoggerFactory.getLogger(BaseSqlQueryRepo.class);
  public final JdbcOperations jdbcOperations;

  protected BaseSqlQueryRepo(JdbcOperations jdbcOperations) {
    this.jdbcOperations = jdbcOperations;



  }

  /**
   * Find the first matching entity based on the provided SQL query and parameters.
   *
   * @param sql    the SQL query to be executed
   * @param params the map of parameters to be set in the PreparedStatement
   * @param entity the class entity of the entity to be returned
   * @return an optional containing the first matching entity, or empty if no match found
   */

  protected Optional<T> findFirstByQuery(String sql, Map<Integer, Object> params, Class<T> entity) {
    return jdbcOperations.prepareStatement(sql, stmt -> {
      setParams(stmt, params);
      ResultSet resultSet = stmt.executeQuery();
      return jdbcOperations.entityStream(resultSet, entity).findFirst();
    });
  }

  /**
   * Find all matching entities based on the provided SQL query and parameters.
   *
   * @param sql    the SQL query to be executed
   * @param params the map of parameters to be set in the PreparedStatement
   * @param entity the class entity of the entities to be returned
   * @return a list of matching entities
   */

  protected List<T> findAllByQuery(String sql, Map<Integer, Object> params, Class<T> entity) {
    return jdbcOperations.prepareStatement(sql, stmt -> {
      setParams(stmt, params);
      ResultSet resultSet = stmt.executeQuery();
      return jdbcOperations.entityStream(resultSet, entity).toList();
    });
  }

  /**
   * Check if any records exist that match the provided SQL query and parameters.
   *
   * @param sql    the SQL query to be executed
   * @param params the map of parameters to be set in the PreparedStatement
   * @return true if a matching record is found, otherwise false
   */

  protected boolean existsByQuery(String sql, Map<Integer, Object> params) {
    return jdbcOperations.prepareStatement(sql, stmt -> {
      setParams(stmt, params);
      ResultSet resultSet = stmt.executeQuery();
      return resultSet.next();
    });
  }

  /**
   * Count the number of records that match the provided SQL query and parameters.
   *
   * @param sql    the SQL query to be executed
   * @param params the map of parameters to be set in the PreparedStatement
   * @return the count of matching records
   */

  protected long countByQuery(String sql, Map<Integer, Object> params) {
    return jdbcOperations.prepareStatement(sql, stmt -> {
      setParams(stmt, params);
      ResultSet resultSet = stmt.executeQuery();
      long count = 0;
      if (resultSet.next()) {
        count = resultSet.getLong(1);
      }
      return count;
    });
  }

  /**
   * Find a unique matching entity based on the provided SQL query and parameters.
   *
   * @param sql    the SQL query to be executed
   * @param params the map of parameters to be set in the PreparedStatement
   * @param entity the class entity of the entity to be returned
   * @return an optional containing the unique matching entity, or empty if no match found
   */

  protected Optional<T> findUniqueByQuery(String sql, Map<Integer, Object> params, Class<T> entity) {
    return jdbcOperations.prepareStatement(sql, stmt -> {
      setParams(stmt, params);
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return jdbcOperations.entityStream(resultSet, entity).findFirst();
      }
      else {
        return Optional.empty();
      }
    });
  }

  /**
   * Retrieve the last record, potentially including or excluding deleted records based on the given flag.
   *
   * @param shouldIncludeDeletedRecords whether or not to include deleted records
   * @return an optional containing the last matching entity, or empty if no match found
   */

  public Optional<T> getLast(Boolean shouldIncludeDeletedRecords, Map<Integer, Object> params, Class<T> entity) {

    String queryBase = " SELECT e.* FROM " + entity.getSimpleName();
    String orderByLimit = " e ORDER BY e.id DESC LIMIT 1 ";
    String includeDeleteRecords = shouldIncludeDeletedRecords ? "" : " WHERE e.deleted_at = false ";

    String query = queryBase + includeDeleteRecords + orderByLimit;

    return jdbcOperations.prepareStatement(query, stmt -> {
      ResultSet resultSet = stmt.executeQuery();
      if (resultSet.next()) {
        return jdbcOperations.entityStream(resultSet, entity).findFirst();
      }
      else {
        return Optional.empty();
      }
    });
  }


  /**
   * Set parameters on the PreparedStatement from the provided map.
   *
   * @param stmt   the PreparedStatement where the parameters are to be set
   * @param params the map of parameters to be set in the PreparedStatement
   * @throws SQLException if a SQL error occurs while setting parameters
   */
  private void setParams(PreparedStatement stmt, Map<Integer, Object> params) throws SQLException {
    for (Map.Entry<Integer, Object> entry : params.entrySet()) {
      stmt.setObject(entry.getKey(), entry.getValue());
    }
  }

}
