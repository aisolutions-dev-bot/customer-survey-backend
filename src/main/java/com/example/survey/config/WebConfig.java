package com.example.survey.config;

import com.example.survey.tenancy.TenantResolutionFilter;
import com.example.survey.tenancy.TenantResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    private static final String[] ALLOWED_ORIGINS = {
        "http://localhost:4200",
        "https://customer-survey-production.up.railway.app",
        "https://customer-survey-staging.up.railway.app",
        "https://staging.aisolutionspl.com",
        "https://staging-org-api.aisolutionspl.com",
        "https://staging-jobtasks-api.aisolutionspl.com",
        "https://staging-ui.aisolutionspl.com",
        "https://staging-jobtasks.aisolutionspl.com",
        "https://staging-contact-api.aisolutionspl.com",
        "https://staging-contact.aisolutionspl.com",
        "https://staging-hr-administration-api.aisolutionspl.com",
        "https://staging-hr-administration.aisolutionspl.com",
        "https://staging-hr-management-api.aisolutionspl.com",
        "https://staging-hr-management.aisolutionspl.com",
        "https://staging-evaluation-api.aisolutionspl.com",
        "https://staging-evaluation.aisolutionspl.com",
        "https://staging-project-api.aisolutionspl.com",
        "https://staging-project.aisolutionspl.com",
        "https://staging-system-api.aisolutionspl.com",
        "https://staging-system.aisolutionspl.com",
        "https://staging-account-api.aisolutionspl.com",
        "https://staging-account.aisolutionspl.com",
        "https://staging-general-settings-api.aisolutionspl.com",
        "https://staging-general-settings.aisolutionspl.com",
        "https://staging-vendor-registration-api.aisolutionspl.com",
        "https://staging-vendor-registration.aisolutionspl.com",
        "https://staging-vendor-management-api.aisolutionspl.com",
        "https://staging-vendor-management.aisolutionspl.com",
        "https://staging-vendor-admin-api.aisolutionspl.com",
        "https://staging-vendor-admin.aisolutionspl.com",
        "https://staging-survey-api.aisolutionspl.com",
        "https://staging-survey.aisolutionspl.com",
        "https://staging-payroll-api.aisolutionspl.com",
        "https://staging-payroll.aisolutionspl.com"
    };

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
                        .allowedOrigins(ALLOWED_ORIGINS)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowCredentials(true);
            }
        };
    }
}
