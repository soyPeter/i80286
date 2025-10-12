package es.myinvestor.common.infrastructure.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

// This class will house the DataSource beans and related configuration
@Configuration
public class DataSourceConfig {

  // --- 1. Property Binding for Command DataSource ---
  @Bean
  @Primary // Mark this as the primary datasource for auto-wiring (e.g., Flyway)
  @ConfigurationProperties("spring.datasource.command") // Binds properties from application.yml
  public DataSourceProperties commandDataSourceProperties() {
    return new DataSourceProperties();
  }

  // --- 2. HikariDataSource Bean for Command Operations ---
  @Bean
  @Qualifier("commandDataSource")
  @Primary
  public HikariDataSource commandDataSource() {
    return commandDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  // --- 3. Property Binding for Query DataSource ---
  @Bean
  @ConfigurationProperties("spring.datasource.query") // Binds properties from application.yml
  public DataSourceProperties queryDataSourceProperties() {
    return new DataSourceProperties();
  }

  // --- 4. HikariDataSource Bean for Query Operations ---
  @Bean
  @Qualifier("queryDataSource")
  public HikariDataSource queryDataSource() {
    return queryDataSourceProperties()
        .initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }
}
