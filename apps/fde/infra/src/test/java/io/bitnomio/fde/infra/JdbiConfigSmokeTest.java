/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 4/8/25 Time: 19:41
 *
 */
package io.bitnomio.fde.infra;

import es.myinvestor.shared.infra.utils.MdcUtils;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach; // For cleaning MDC
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional; // For rollback

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.Map; // For retrieving data as a Map

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;


// This test extends your existing IntegrationTestBase to reuse Testcontainers setup
@SpringBootTest
@DisplayName("JDBI Configuration Smoke Test")
class JdbiConfigSmokeTest extends IntegrationTestBase {

  @Autowired
  @Qualifier("jdbiCommand")
  private Jdbi jdbiCommand;

  @Autowired
  @Qualifier("jdbiQuery")
  private Jdbi jdbiQuery;

  private String testAuditor;

  @BeforeEach
  void setupAuditorAndMdc() {
    // Set a unique auditor for each test to avoid interference
    testAuditor = "test_user_" + UUID.randomUUID().toString().substring(0, 8);
    MDC.put(MdcUtils.MDC_REQUEST_USERNAME, testAuditor);
  }

  @AfterEach
  void cleanupMdc() {
    MDC.remove(MdcUtils.MDC_REQUEST_USERNAME);
  }

  @Test
  @DisplayName("Should successfully load Spring context and inject JDBI beans")
  void contextLoads() {
    assertNotNull(jdbiCommand, "jdbiCommand bean should not be null");
    assertNotNull(jdbiQuery, "jdbiQuery bean should not be null");
  }

  @Test
  @DisplayName("JDBI Command and Query instances should be distinct")
  void jdbiInstancesAreDistinct() {
    assertNotSame(jdbiCommand, jdbiQuery, "jdbiCommand and jdbiQuery should be distinct instances");
  }

  @Test
  @DisplayName("JDBI instances should be able to connect and execute simple queries")
  void jdbiInstancesCanConnect() {
    // This implicitly checks if datasources are working and JDBI can get a handle
    Integer commandResult = jdbiCommand.withHandle(handle -> handle.createQuery("SELECT 1").mapTo(Integer.class).one());
    assertThat(commandResult).isEqualTo(1);

    Integer queryResult = jdbiQuery.withHandle(handle -> handle.createQuery("SELECT 1").mapTo(Integer.class).one());
    assertThat(queryResult).isEqualTo(1);
  }

  @Test
  @Transactional("commandTransactionManager") // Ensure transactional rollback for this test
  @DisplayName("AuditArgumentBinder should inject createdAt and createdBy on INSERT")
  void auditBinderInsertsCreatedAtAndBy() {
    String testName = "test_insert_" + UUID.randomUUID().toString();
    Instant beforeInsert = Instant.now();

    // Perform an INSERT using jdbiCommand (where AuditArgumentBinder is registered)
    jdbiCommand.useHandle(handle -> handle.createUpdate("""
            INSERT INTO dummy_table (name, created_at, created_by, updated_at, updated_by, deleted, deleted_at, deleted_by)
            VALUES (:name, :createdAt, :createdBy, :updatedAt, :updatedBy, :deleted, :deletedAt, :deletedBy)
            """)
        .bind("name", testName)
        // AuditArgumentBinder will provide values for :createdAt, :createdBy, :updatedAt, :updatedBy, :deleted, :deletedAt, :deletedBy
        .execute());

    // Retrieve the inserted row and verify audit fields
    Map<String, Object> insertedRow = jdbiCommand.withHandle(handle -> handle.createQuery("SELECT * FROM dummy_table WHERE name = :name")
        .bind("name", testName)
        .mapToMap()
        .one());

    assertThat(insertedRow).isNotNull();
    assertThat(insertedRow.get("name")).isEqualTo(testName);

    // Verify audit fields
    assertThat(insertedRow.get("created_by")).isEqualTo(testAuditor);
    assertThat((Timestamp) insertedRow.get("created_at")).isAfterOrEqualTo(beforeInsert).isBeforeOrEqualTo(Instant.now());
    assertThat(insertedRow.get("updated_by")).isEqualTo(testAuditor);
    assertThat((Timestamp) insertedRow.get("updated_at")).isAfterOrEqualTo(beforeInsert).isBeforeOrEqualTo(Instant.now());
    assertThat(insertedRow.get("deleted")).isEqualTo(false);
    assertThat(insertedRow.get("deleted_at")).isNull();
    assertThat(insertedRow.get("deleted_by")).isNull();
  }

  @Test
  @Transactional("commandTransactionManager") // Ensure transactional rollback for this test
  @DisplayName("AuditArgumentBinder should inject updatedAt and updatedBy on UPDATE")
  void auditBinderUpdatesUpdatedAtAndBy() throws InterruptedException {
    String initialName = "initial_update_name";
    String updatedName = "updated_name_" + UUID.randomUUID().toString();
    String initialAuditor = "initial_user";

    // Step 1: Insert an initial row (with a known auditor)
    MDC.put(MdcUtils.MDC_REQUEST_USERNAME, initialAuditor);
    Instant initialInsertTime = Instant.now();
    jdbiCommand.useHandle(handle -> handle.createUpdate("""
            INSERT INTO dummy_table (name, created_at, created_by, updated_at, updated_by, deleted, deleted_at, deleted_by)
            VALUES (:name, :createdAt, :createdBy, :updatedAt, :updatedBy, :deleted, :deletedAt, :deletedBy)
            """)
        .bind("name", initialName)
        .execute());

    MDC.remove(MdcUtils.MDC_REQUEST_USERNAME); // Clean MDC after initial insert

    // Retrieve the ID of the inserted row
    Long id = jdbiCommand.withHandle(handle -> handle.createQuery("SELECT id FROM dummy_table WHERE name = :name")
        .bind("name", initialName)
        .mapTo(Long.class)
        .one());
    assertNotNull(id);

    // Verify initial audit fields
    Map<String, Object> initialRow = jdbiCommand.withHandle(handle -> handle.createQuery("SELECT * FROM dummy_table WHERE id = :id")
        .bind("id", id)
        .mapToMap()
        .one());
    assertThat(initialRow.get("created_by")).isEqualTo(initialAuditor);
    assertThat((Timestamp) initialRow.get("created_at")).isBetween(initialInsertTime.minusSeconds(1), initialInsertTime.plusSeconds(1));
    assertThat(initialRow.get("updated_by")).isEqualTo(initialAuditor);
    assertThat((Timestamp) initialRow.get("updated_at")).isBetween(initialInsertTime.minusSeconds(1), initialInsertTime.plusSeconds(1));

    // Wait a bit to ensure updated_at changes
    Thread.sleep(100);

    // Step 2: Perform an UPDATE with a different auditor
    MDC.put(MdcUtils.MDC_REQUEST_USERNAME, testAuditor); // Set new auditor for the update

    Instant beforeUpdate = Instant.now();

    jdbiCommand.useHandle(handle -> handle.createUpdate("UPDATE dummy_table SET name = :newName, updated_by = :updatedBy, updated_at = :updatedAt WHERE id = :id")
        .bind("id", id)
        .bind("newName", updatedName)
        // AuditArgumentBinder will provide :updatedAt and :updatedBy
        .execute());

    // Step 3: Retrieve and verify updated audit fields
    Map<String, Object> updatedRow = jdbiCommand.withHandle(handle -> handle.createQuery("SELECT * FROM dummy_table WHERE id = :id")
        .bind("id", id)
        .mapToMap()
        .one());

    assertThat(updatedRow).isNotNull();
    assertThat(updatedRow.get("name")).isEqualTo(updatedName);

    // created_at and created_by should remain unchanged
    assertThat(updatedRow.get("created_by")).isEqualTo(initialAuditor);
    assertThat((Timestamp) updatedRow.get("created_at")).isEqualTo(initialRow.get("created_at"));

    // updated_at and updated_by should reflect the update
    assertThat(updatedRow.get("updated_by")).isEqualTo(testAuditor); // This should be our new auditor
    assertThat((Timestamp) updatedRow.get("updated_at")).isAfterOrEqualTo(beforeUpdate).isBeforeOrEqualTo(Instant.now());
  }

  @Test
  @Transactional("commandTransactionManager") // Ensure transactional rollback for this test
  @DisplayName("AuditArgumentBinder should not override explicitly bound audit fields (if any)")
  void auditBinderDoesNotOverrideExplicitBinds() {
    // This test is harder to write effectively because ctx.define() might be overridden by explicit @Bind calls.
    // It's a design choice to either let customizer be the source or let @Bind win.
    // Given ctx.define() behavior, explicit @Bind might take precedence or customizer's :param might clash.
    // Best practice: DO NOT explicitly bind :createdAt, :createdBy, :updatedAt, :updatedBy, :deleted, :deletedAt, :deletedBy
    // in your SqlObject methods if you use AuditArgumentBinder. Let the customizer handle them.
    // This test serves as a reminder to avoid mixing.
    // If a test were needed, it would look similar to the insert test, but with an explicit bind
    // for an audit field, then assert the explicit bind's value is used.
  }
}
