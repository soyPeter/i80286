package io.bitnomio.shared.infra.config;

import org.jdbi.v3.cache.caffeine.CaffeineCachePlugin;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.jackson2.Jackson2Plugin;
import org.jdbi.v3.opentelemetry.JdbiOpenTelemetryPlugin;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.stringtemplate4.StringTemplateEngine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

;

@Configuration
public class JdbiConfig {

  // --- 1. JDBI Instance for Write Operations (Command Side) ---
  @Bean
  public Jdbi jdbiCommand(@Qualifier("commandDataSource") DataSource commandDataSource) {

    return Jdbi.create(commandDataSource)
        // Core plugins
        .installPlugin(new SqlObjectPlugin())
        .installPlugin(new PostgresPlugin()) // For PostgreSQL types like JSONB
        // Optional/Extension plugins
        .installPlugin(new CaffeineCachePlugin())
        .installPlugin(new JdbiOpenTelemetryPlugin())
        .installPlugin(new Jackson2Plugin())
        .setTemplateEngine(new StringTemplateEngine())
        .addCustomizer(new JdbiAuditConfig());
  }

  // --- 2. JDBI Instance for Read Operations (Query Side) ---
  @Bean
  public Jdbi jdbiQuery(@Qualifier("queryDataSource") DataSource queryDataSource) {
    return Jdbi.create(queryDataSource)
        // Core plugins
        .installPlugin(new SqlObjectPlugin())
        .installPlugin(new PostgresPlugin())
        // Optional/Extension plugins
        .installPlugin(new CaffeineCachePlugin())
        .installPlugin(new JdbiOpenTelemetryPlugin())
        .setTemplateEngine(new StringTemplateEngine())
        .addCustomizer(new JdbiAuditConfig());
  }
}
