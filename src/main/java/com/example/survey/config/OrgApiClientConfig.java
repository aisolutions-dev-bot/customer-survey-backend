package com.example.survey.config;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * Produces the RestTemplate used to call ai-solutions-organization-api
 * (system parameter flags, company database lookups, service-to-service calls)
 * with a service-account Basic Auth header, matching the pattern already used
 * by evaluation-management-backend's ServiceAuthHeaderFactory.
 */
@Configuration
public class OrgApiClientConfig {

  @Value("${org.api.base-url}")
  private String orgApiBaseUrl;

  @Value("${app.service.username}")
  private String serviceUsername;

  @Value("${app.service.password}")
  private String servicePassword;

  // Tenant routing cannot distinguish a slow org-api from an unreachable one,
  // so a hung lookup has to end in a failed request. Without a read timeout the
  // call waits forever and the survey request it belongs to never completes.
  @Value("${org.api.connect-timeout:PT2S}")
  private Duration connectTimeout;

  @Value("${org.api.read-timeout:PT5S}")
  private Duration readTimeout;

  @Bean
  public RestTemplate orgApiRestTemplate(RestTemplateBuilder builder) {
    return builder
        .rootUri(orgApiBaseUrl)
        .connectTimeout(connectTimeout)
        .readTimeout(readTimeout)
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
