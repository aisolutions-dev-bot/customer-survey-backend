package com.example.survey.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.survey.tenancy.CompanyDataSourceRegistry;
import com.example.survey.tenancy.TenantRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Wires tenant routing into the service's datasource configuration.
 *
 * <p>Hibernate's second-level and query caches stay off. They would be keyed
 * by entity id alone, so one tenant's cached entity could be served to a
 * request routed to a different tenant.
 */
@Configuration
public class TenantDataSourceConfig {

  /**
   * The service's own database, built from the existing {@code spring.datasource.*}
   * settings. Named separately so the routing datasource, the link token
   * repository and {@link CompanyDataSourceRegistry} can depend on the default
   * database without going through routing.
   */
  @Bean
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public HikariDataSource defaultDataSource(DataSourceProperties dataSourceProperties) {
    return dataSourceProperties.initializeDataSourceBuilder()
        .type(HikariDataSource.class)
        .build();
  }

  /**
   * The datasource every JPA repository and JdbcTemplate resolves: the tenant's
   * database for a request that resolved one, the default database otherwise.
   */
  @Bean
  @Primary
  public DataSource tenantRoutingDataSource(
      @Qualifier("defaultDataSource") HikariDataSource defaultDataSource,
      CompanyDataSourceRegistry dataSourceRegistry) {
    return new TenantRoutingDataSource(defaultDataSource, dataSourceRegistry);
  }
}
