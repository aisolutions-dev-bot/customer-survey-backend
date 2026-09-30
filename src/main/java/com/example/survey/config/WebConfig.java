package com.example.survey.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.survey.tenancy.TenantResolutionFilter;
import com.example.survey.tenancy.TenantResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class WebConfig {

  /**
   * Runs tenant resolution ahead of every other filter and on every path, so
   * the tenant is bound before any handler can reach a datasource. The filter
   * is built here rather than component-scanned so its URL patterns and order
   * are explicit; registering it as a bean as well would run it twice.
   */
  @Bean
  public FilterRegistrationBean<TenantResolutionFilter> tenantResolutionFilter(
      TenantResolver tenantResolver, ObjectMapper objectMapper) {
    FilterRegistrationBean<TenantResolutionFilter> registration =
        new FilterRegistrationBean<>(new TenantResolutionFilter(tenantResolver, objectMapper));
    registration.addUrlPatterns("/*");
    registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
    return registration;
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:4200",
                "https://customer-survey-production.up.railway.app",
                "https://customer-survey-staging.up.railway.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowCredentials(true);
      }
    };
  }
}
