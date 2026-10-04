package com.example.survey.service.notification;

import java.util.UUID;

import com.aisolutions.shared.notification.spring.SpringNotificationOutboxRelay;
import com.aisolutions.shared.notification.spring.SpringNotificationPublisher;
import com.example.survey.testsupport.NotificationKafkaProbe;
import com.example.survey.testsupport.SurveyIntegrationTestBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the shared Spring publisher relays its outbox envelope to the platform topic.
 */
class NotificationChannelPublisherIntegrationTest extends SurveyIntegrationTestBase {

    private static final String EMAIL_TOPIC = "notifications.email.v1";
    private static final String DEFAULT_COMPANY_ID = "db_test2";

    @Autowired
    SpringNotificationPublisher springNotificationPublisher;

    @Autowired
    SpringNotificationOutboxRelay notificationOutboxRelay;

    @Autowired
    PlatformTransactionManager transactionManager;

    /** Delegates publishing, then observes the broker through {@link NotificationKafkaProbe}. */
    @Test
    void publishesEmailEnvelopeToThePlatformTopic() {
        String recipient = "channel-probe-" + UUID.randomUUID() + "@example.com";
        try (NotificationKafkaProbe probe = new NotificationKafkaProbe(kafkaBootstrapServers(), EMAIL_TOPIC)) {
            stageEmailNotification(recipient);
            notificationOutboxRelay.publishPendingBatches();
            ConsumerRecord<String, String> record = probe.awaitRecipient(recipient);
            assertThat(record.key()).isEqualTo(DEFAULT_COMPANY_ID);
            assertThat(record.value())
                    .contains(
                            recipient,
                            "notificationId",
                            "evaluation_completed_v1",
                            "templateParameters",
                            "evaluator_name",
                            "evaluation_score");
        }
    }

    /** Returns the shared container broker address. */
    private String kafkaBootstrapServers() {
        return KAFKA.getBootstrapServers();
    }

    /** Stages an email on a Spring-managed transaction before the relay sees it. */
    private void stageEmailNotification(String recipient) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.executeWithoutResult(status -> springNotificationPublisher.enqueueEmailTemplate(
                DEFAULT_COMPANY_ID,
                recipient,
                "evaluation_completed_v1",
                "en_US",
                java.util.Map.of(
                        "evaluator_name", "Evaluator",
                        "staff_id", "STAFF-001",
                        "evaluatee_name", "Evaluatee",
                        "project_id", "PROJECT-001",
                        "project_name", "Project",
                        "department_id", "",
                        "skillset", "",
                        "form_type", "CARPENTER",
                        "evaluation_score", "88")));
    }
}
