package com.example.survey.tenancy;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Routes every {@code getConnection()} to the datasource of the tenant bound to
 * the calling thread, and to the default datasource when no tenant is bound.
 *
 * <p>Tenant datasources are not registered up front: they are built on first
 * use by {@link CompanyDataSourceRegistry} and evicted again when idle, so
 * {@link #determineTargetDataSource()} resolves them itself rather than
 * looking them up in the fixed map {@code AbstractRoutingDataSource} would
 * otherwise index.
 */
public class TenantRoutingDataSource extends AbstractRoutingDataSource {

  private static final String DEFAULT_LOOKUP_KEY = "DEFAULT";

  private final DataSource defaultDataSource;
  private final CompanyDataSourceRegistry dataSourceRegistry;

  public TenantRoutingDataSource(DataSource defaultDataSource, CompanyDataSourceRegistry dataSourceRegistry) {
    this.defaultDataSource = defaultDataSource;
    this.dataSourceRegistry = dataSourceRegistry;
    setDefaultTargetDataSource(defaultDataSource);
    setTargetDataSources(Map.<Object, Object>of(DEFAULT_LOOKUP_KEY, defaultDataSource));
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return TenantContext.currentDatabaseName();
  }

  @Override
  protected DataSource determineTargetDataSource() {
    String databaseName = TenantContext.currentDatabaseName();
    if (databaseName == null || databaseName.isBlank()) {
      return defaultDataSource;
    }
    return dataSourceRegistry.dataSourceFor(databaseName);
  }
}
