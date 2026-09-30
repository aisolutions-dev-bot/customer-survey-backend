package com.example.survey.tenancy;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.survey.service.CompanyDbLookupService;

/**
 * Turns the company a request names into the database it must be served from.
 */
@Service
public class TenantResolver {

  private static final Logger LOG = LoggerFactory.getLogger(TenantResolver.class);

  private final CompanyDbLookupService companyDbLookupService;

  public TenantResolver(CompanyDbLookupService companyDbLookupService) {
    this.companyDbLookupService = companyDbLookupService;
  }

  /**
   * Resolves the tenant {@code companyId} names, binding no database when the
   * company has none of its own.
   *
   * @param companyId the company from the {@code c} query parameter or the {@code X-Company-Id} header
   * @return the tenant to bind to the request
   * @throws CompanyDbLookupException when org-api did not answer
   */
  public TenantContext.TenantIdentity resolveFromCompanyId(String companyId) {
    Optional<String> databaseName = companyDbLookupService.lookupDatabaseName(companyId);
    LOG.info("tenant-resolution: company {} resolved to database {}",
        companyId, databaseName.orElse("(default)"));
    return new TenantContext.TenantIdentity(companyId, databaseName.orElse(null));
  }
}
