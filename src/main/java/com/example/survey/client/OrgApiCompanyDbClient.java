package com.example.survey.client;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.aisolutions.shared.tenancy.CompanyDbResponse;
import com.example.survey.tenancy.CompanyDbLookupException;

/**
 * Calls org-api's company-to-database lookup, the same endpoint the Quarkus
 * services resolve tenants through.
 */
@Component
public class OrgApiCompanyDbClient {

  private static final String COMPANY_DB_PATH = "/api/organizations/companies/{companyId}/db";

  private final RestTemplate orgApiRestTemplate;

  public OrgApiCompanyDbClient(RestTemplate orgApiRestTemplate) {
    this.orgApiRestTemplate = orgApiRestTemplate;
  }

  /**
   * The database {@code companyId} owns, or empty when it has none of its own.
   *
   * @param companyId the company to resolve
   * @return the company's database name, or empty when it has none
   * @throws CompanyDbLookupException when org-api did not answer
   */
  public Optional<String> fetchDatabaseName(String companyId) {
    try {
      CompanyDbResponse response =
          orgApiRestTemplate.getForObject(COMPANY_DB_PATH, CompanyDbResponse.class, companyId);
      return Optional.ofNullable(response)
          .map(CompanyDbResponse::dbName)
          .filter(databaseName -> !databaseName.isBlank());
    } catch (RestClientException e) {
      throw new CompanyDbLookupException(
          "org-api company database lookup failed for company " + companyId, e);
    }
  }
}
