/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@bnext.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 4/8/25 Time: 18:41
 *
 */
package io.bitnomio.shared.infra.config;

import io.bitnomio.shared.infra.utils.MdcUtils;
import org.jdbi.v3.core.statement.Binding;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.core.statement.StatementCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class JdbiAuditConfig implements StatementCustomizer {

  private static final Logger log = LoggerFactory.getLogger(JdbiAuditConfig.class);
  // Mapa con los valores para binding

  @Override
  public void beforeBinding(PreparedStatement stmt, StatementContext ctx) throws SQLException {
    // Get the SQL query from the context
    //ctx.setTraceId(MdcUtils.getCurrentAuditor());
    String sql = ctx.getRenderedSql().trim().toUpperCase();

    boolean isInsert = sql.startsWith("INSERT");
    boolean isUpdate = sql.startsWith("UPDATE");


    if (isInsert || isUpdate) {
      final Timestamp now = Timestamp.from(Instant.now());
      final String auditor = MdcUtils.getCurrentAuditor();
      final Binding binding = ctx.getBinding();

      Map<String, Object> auditParameters = new HashMap<>();

      // JDBI automatically handles binding of defined attributes.
      // We define these attributes on the context, and JDBI will try to bind them if the SQL contains them.
      if (isInsert) {
        auditParameters.put("createdAt", now);
        auditParameters.put("createdBy", auditor);
        auditParameters.put("updatedAt", now);
        auditParameters.put("updatedBy", auditor);
        auditParameters.put("deleted", false);
        auditParameters.put("deletedAt", null);
        auditParameters.put("deletedBy", null);

        log.warn("defining properties for insert");

      } else if (isUpdate) {
        // For updates, we do NOT touch createdAt, createdBy, deleted, deletedAt, deletedBy here.
        // These should only be set on INSERT or explicit softDelete operations.

        auditParameters.put("updatedAt", now);
        auditParameters.put("updatedBy", auditor);

        log.warn("defining properties for update, updatedBy: {}", auditor);
      }

      // Parallel stream para bind condicional
      ctx.getParsedSql().getParameters().getParameterNames().parallelStream()
          .filter(auditParameters::containsKey)
          .forEach(paramName -> {
            log.warn("Binding audit parameter: {}", paramName);
            binding.addNamed(paramName, auditParameters.get(paramName));
          });
    }
  }

  @Override
  public void beforeTemplating(PreparedStatement stmt, StatementContext ctx) throws SQLException {

  }

  @Override
  public void beforeExecution(PreparedStatement stmt, StatementContext ctx) throws SQLException {
  }

  @Override
  public void afterExecution(PreparedStatement stmt, StatementContext ctx) throws SQLException {
  }
}