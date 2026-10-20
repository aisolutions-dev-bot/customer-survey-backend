package com.example.survey.config;

import com.aisolutions.shared.notification.NotificationEnvelope;
import com.aisolutions.shared.notification.spring.SpringNotificationOutboxDataSourceProvider;
import com.example.survey.tenancy.CompanyDataSourceRegistry;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Connects the shared Spring relay to Customer Survey's tenant datasource registry.
 *
 * <p>The outbox stores the envelope as Jackson JSON; the native image needs its binding hints.
 */
@Configuration(proxyBeanMethods = false)
@RegisterReflectionForBinding(NotificationEnvelope.class)
public class SurveyNotificationOutboxConfiguration {

    /** Supplies every datasource that can contain this service's transactional outbox rows. */
    @Bean
    SpringNotificationOutboxDataSourceProvider surveyNotificationOutboxDataSourceProvider(
            CompanyDataSourceRegistry companyDataSourceRegistry) {
        return companyDataSourceRegistry::dataSourcesForNotificationRelay;
    }
}
