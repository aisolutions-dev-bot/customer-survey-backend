package com.example.survey.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * Produces the RestTemplate used to call ai-solutions-organization-api
 * (system parameter flags, service-to-service calls) with a service-account
 * Basic Auth header, matching the pattern already used by
 * evaluation-management-backend's ServiceAuthHeaderFactory.
 */
@Configuration
public class OrgApiClientConfig {

  @Value("${org.api.base-url}")
  private String orgApiBaseUrl;

  @Value("${app.service.username}")
  private String serviceUsername;

  @Value("${app.service.password}")
  private String servicePassword;

  @Bean
  public RestTemplate orgApiRestTemplate(RestTemplateBuilder builder) {
    return builder
        .rootUri(orgApiBaseUrl)
        .additionalInterceptors(buildServiceAccountAuthInterceptor())
        .build();
  }

  /**
   * Builds a request interceptor that stamps every outgoing call with a
   * Basic Auth header for the dedicated service account, so this backend
   * can call org-api without forwarding an end-user's token.
   */
  private ClientHttpRequestInterceptor buildServiceAccountAuthInterceptor() {
    String rawCredentials = serviceUsername + ":" + servicePassword;
    String encodedCredentials = Base64.getEncoder()
        .encodeToString(rawCredentials.getBytes(StandardCharsets.UTF_8));
    String authHeaderValue = "Basic " + encodedCredentials;

    return (request, body, execution) -> {
      request.getHeaders().add("Authorization", authHeaderValue);
      return execution.execute(request, body);
    };
  }
}
