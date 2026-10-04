package com.example.survey.config;

import com.aisolutions.shared.notification.spring.SpringNotificationOutboxDataSourceProvider;
import com.example.survey.tenancy.CompanyDataSourceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Connects the shared Spring relay to Customer Survey's tenant datasource registry. */
@Configuration(proxyBeanMethods = false)
public class SurveyNotificationOutboxConfiguration {

    /** Supplies every datasource that can contain this service's transactional outbox rows. */
    @Bean
    SpringNotificationOutboxDataSourceProvider surveyNotificationOutboxDataSourceProvider(
            CompanyDataSourceRegistry companyDataSourceRegistry) {
        return companyDataSourceRegistry::dataSourcesForNotificationRelay;
    }
}
