/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:myinvestor-backend@MyInvestor.es
 *
 * fraud-detection-engine - Created by pedro.almendro@MyInvestor
 * Date: 4/8/25 Time: 18:57
 *
 */
package io.bitnomio.fde.infra;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

// Define your PostgreSQL container images
// Ensure these match the versions you use in docker-compose.yml
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE) // No need for web server for DB tests
@ActiveProfiles("test") // Use a 'test' profile for specific test configs
@ContextConfiguration(initializers = IntegrationTestBase.Initializer.class)
// Custom initializer for Testcontainers properties
public abstract class IntegrationTestBase {

  // Command DB Container
  @Container
  public static PostgreSQLContainer<?> postgresCommandContainer =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
          .withDatabaseName("fraud_command_test_db")
          .withUsername("test_writer")
          .withPassword("test_password");

  // Query DB Container
  @Container
  public static PostgreSQLContainer<?> postgresQueryContainer =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
          .withDatabaseName("fraud_query_test_db")
          .withUsername("test_reader")
          .withPassword("test_password");

  // Initializer to dynamically set datasource properties based on Testcontainers ports
  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of(
          "spring.datasource.command.url=" + postgresCommandContainer.getJdbcUrl(),
          "spring.datasource.command.username=" + postgresCommandContainer.getUsername(),
          "spring.datasource.command.password=" + postgresCommandContainer.getPassword(),
          "spring.datasource.query.url=" + postgresQueryContainer.getJdbcUrl(),
          "spring.datasource.query.username=" + postgresQueryContainer.getUsername(),
          "spring.datasource.query.password=" + postgresQueryContainer.getPassword(),
          // Flyway runs on command DB
          "spring.flyway.url=" + postgresCommandContainer.getJdbcUrl(),
          "spring.flyway.user=" + postgresCommandContainer.getUsername(),
          "spring.flyway.password=" + postgresCommandContainer.getPassword()
          // You might also need to disable Spring Cloud Config import if it's enabled by default
          // "spring.cloud.config.enabled=false"
      ).applyTo(applicationContext.getEnvironment());
    }
  }


}
