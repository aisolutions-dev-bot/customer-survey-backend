package com.example.survey.service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.survey.client.OrgApiCompanyDbClient;
import com.example.survey.tenancy.CompanyDbLookupException;

/**
 * Resolves a company's database name, caching answers briefly because
 * company-to-database mappings change very rarely while evaluation traffic
 * arrives constantly. Only answers from org-api are cached; a lookup that
 * failed is never remembered.
 */
@Service
public class CompanyDbLookupService {

  private final OrgApiCompanyDbClient companyDbClient;
  private final long cacheTtlMillis;
  private final ConcurrentHashMap<String, CachedDatabaseName> databaseNameByCompanyId = new ConcurrentHashMap<>();

  public CompanyDbLookupService(
      OrgApiCompanyDbClient companyDbClient,
      @Value("${aisolutions.tenancy.company-db-cache.ttl-seconds:120}") long cacheTtlSeconds) {
    this.companyDbClient = companyDbClient;
    this.cacheTtlMillis = cacheTtlSeconds * 1000L;
  }

  /**
   * The database {@code companyId} owns, or empty when it has no database of
   * its own and should therefore use the default datasource.
   *
   * @param companyId the company to resolve
   * @return the company's database name, or empty when it has none
   * @throws CompanyDbLookupException when org-api could not answer
   */
  public Optional<String> lookupDatabaseName(String companyId) {
    if (companyId == null || companyId.isBlank()) {
      throw new CompanyDbLookupException("Cannot resolve a company database for a blank companyId");
    }
    CachedDatabaseName cached = databaseNameByCompanyId.get(companyId);
    if (cached != null && !cached.hasExpired()) {
      return cached.databaseName();
    }
    Optional<String> resolvedDatabaseName = companyDbClient.fetchDatabaseName(companyId);
    cacheDatabaseName(companyId, resolvedDatabaseName);
    return resolvedDatabaseName;
  }

  private void cacheDatabaseName(String companyId, Optional<String> databaseName) {
    databaseNameByCompanyId.put(
        companyId, new CachedDatabaseName(databaseName, System.currentTimeMillis() + cacheTtlMillis));
    dropExpiredEntries();
  }

  /** Keeps the cache to the companies looked up within the cache lifetime. */
  private void dropExpiredEntries() {
    databaseNameByCompanyId.entrySet().removeIf(entry -> entry.getValue().hasExpired());
  }

  /** A resolved database name together with the time it stops being served. */
  private record CachedDatabaseName(Optional<String> databaseName, long expiresAtMillis) {

    private boolean hasExpired() {
      return System.currentTimeMillis() >= expiresAtMillis;
    }
  }
}
