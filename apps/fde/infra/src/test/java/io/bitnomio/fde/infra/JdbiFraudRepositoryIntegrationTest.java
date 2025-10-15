/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 4/8/25 Time: 19:03
 *
 */
package io.bitnomio.fde.infra;

import es.myinvestor.shared.infra.utils.MdcUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.UUID;


public class JdbiFraudRepositoryIntegrationTest extends IntegrationTestBase {

//  @Autowired
//  private FraudRuleDefinitionSqlObject fraudRuleDefinitionSqlObject; // Inject the JDBI SqlObject directly for testing its behavior
//
//  @Autowired
//  private JdbiFraudRuleDefinitionQueryRepositoryAdapter fraudRuleDefinitionQueryRepositoryAdapter; // Inject the adapter as well

  // You would also inject FraudDecisionSqlObject and JdbiFraudDecisionCommandRepositoryAdapter

  @BeforeEach
  void setupMdc() {
    MDC.put(MdcUtils.MDC_REQUEST_USERNAME, "test_user_" + UUID.randomUUID().toString().substring(0, 8));
  }

  @Test
  void testAuditArgumentBinderOnInsert() {
    // Given
    String testUser = MDC.get(MdcUtils.MDC_REQUEST_USERNAME);
    Instant beforeInsert = Instant.now();
  }

}
