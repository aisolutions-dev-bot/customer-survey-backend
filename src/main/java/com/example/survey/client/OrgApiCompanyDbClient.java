package com.example.survey.client;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
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

  private static final Logger LOG = LoggerFactory.getLogger(OrgApiCompanyDbClient.class);

  private static final String COMPANY_DB_PATH = "/api/organizations/companies/{companyId}/db";

  private final RestTemplate orgApiRestTemplate;

  public OrgApiCompanyDbClient(RestTemplate orgApiRestTemplate) {
    this.orgApiRestTemplate = orgApiRestTemplate;
  }

  /**
   * The database {@code companyId} owns.
   *
   * <p>A 404 is org-api's answer that the company has no database of its own,
   * which is an empty result rather than an error. Every other failure —
   * 5xx, connection refused, timeout, rejected credentials — propagates,
   * because treating it as "no database" would route the company's request to
   * the default database and serve it another company's rows.
   *
   * @param companyId the company to resolve
   * @return the company's database name, or empty when it has none
   * @throws CompanyDbLookupException when org-api could not be reached or did not answer 200 or 404
   */
  public Optional<String> fetchDatabaseName(String companyId) {
    try {
      CompanyDbResponse response =
          orgApiRestTemplate.getForObject(COMPANY_DB_PATH, CompanyDbResponse.class, companyId);
      return Optional.ofNullable(response)
          .map(CompanyDbResponse::dbName)
          .filter(databaseName -> !databaseName.isBlank());
    } catch (HttpClientErrorException.NotFound notFound) {
      LOG.info("company-db-lookup: company {} has no database of its own", companyId);
      return Optional.empty();
    } catch (RestClientException e) {
      throw new CompanyDbLookupException(
          "org-api company database lookup failed for company " + companyId, e);
    }
  }
}
