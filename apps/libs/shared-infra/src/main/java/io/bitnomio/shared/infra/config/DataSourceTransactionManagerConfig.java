/*
 * COPYRIGHT 2020 -2025 original authors
 * mailto:bitnomio-backend@bitnomio.io
 *
 * fraud-detection-engine - Created by pedro.almendro@bitnomio
 * Date: 4/8/25 Time: 19:48
 *
 */
package io.bitnomio.shared.infra.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Configuration for Spring's transaction management.
 * Defines separate PlatformTransactionManager beans for command (write) and query (read) datasources.
 */
@Configuration
@EnableTransactionManagement // Enables Spring's annotation-driven transaction management (e.g., @Transactional)
public class DataSourceTransactionManagerConfig {

  /**
   * Defines the transaction manager for the command (write) datasource.
   * This will be the default transaction manager for @Transactional if no qualifier is specified,
   * due to its common usage for DML operations.
   *
   * @param commandDataSource The HikariDataSource bean for command operations.
   * @return A DataSourceTransactionManager for command operations.
   */
  @Bean(name = "commandTransactionManager") // Give it a specific name for @Transactional qualifier
  public PlatformTransactionManager commandTransactionManager(@Qualifier("commandDataSource") DataSource commandDataSource) {
    return new DataSourceTransactionManager(commandDataSource);
  }

  /**
   * Defines the transaction manager for the query (read) datasource.
   * This can be used for explicit read-only transactions, though often not strictly necessary
   * as read operations usually don't require transactional boundaries, or can use
   * the command manager with readOnly=true.
   *
   * @param queryDataSource The HikariDataSource bean for query operations.
   * @return A DataSourceTransactionManager for query operations.
   */
  @Bean(name = "queryTransactionManager") // Give it a specific name
  public PlatformTransactionManager queryTransactionManager(@Qualifier("queryDataSource") DataSource queryDataSource) {
    // For read-only operations, we can set it to read-only.
    // This can sometimes allow optimizations at the driver/database level.
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(queryDataSource);
    transactionManager.setEnforceReadOnly(true);
    return transactionManager;
  }
}
