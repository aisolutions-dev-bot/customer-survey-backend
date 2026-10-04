package com.example.survey.service.notification;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

import com.aisolutions.shared.notification.NotificationEnvelope;
import com.aisolutions.shared.notification.NotificationOutboxEvent;
import com.aisolutions.shared.notification.jdbc.JdbcNotificationOutboxRepository;
import com.example.survey.testsupport.NotificationKafkaProbe;
import com.example.survey.testsupport.SurveyIntegrationTestBase;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

/** Exercises the native Spring relay against the shared database and Kafka test stack. */
class NativeNotificationOutboxE2E extends SurveyIntegrationTestBase {

    private static final String EMAIL_TOPIC = "notifications.email.v1";
    private static final String OUTBOX_TABLE = "customer_survey_notification_outbox";
    private static final String NATIVE_APPLICATION_IMAGE = "eclipse-temurin:25-jre-jammy";

    /** Starts the native application, stages an outbox row, and verifies its Kafka delivery. */
    @Test
    @Tag("native-e2e")
    void publishesCommittedOutboxEventFromNativeApplication() throws Exception {
        String recipient = "native-outbox-" + UUID.randomUUID() + "@example.com";
        try (NotificationKafkaProbe kafkaProbe = new NotificationKafkaProbe(kafkaBootstrapServers(), EMAIL_TOPIC);
                GenericContainer<?> nativeApplication = createNativeApplication()) {
            nativeApplication.start();
            stageEmailOutboxEvent(recipient);
            ConsumerRecord<String, String> publishedRecord = kafkaProbe.awaitRecipient(recipient);

            assertThat(publishedRecord.key()).isEqualTo("db_test2");
            assertThat(publishedRecord.value())
                    .contains(
                            recipient,
                            "notificationId",
                            "evaluation_completed_v1",
                            "languageCode",
                            "templateParameters");
        }
    }

    /** Resolves and validates the native executable supplied by the build workflow. */
    private String nativeExecutablePath() {
        String configuredPath = System.getProperty("customer-survey.native-runner");
        if (configuredPath == null || configuredPath.isBlank()) {
            throw new IllegalStateException("Set CUSTOMER_SURVEY_NATIVE_RUNNER to the built native executable");
        }
        Path executable = Path.of(configuredPath).toAbsolutePath();
        if (!Files.isExecutable(executable)) {
            throw new IllegalArgumentException("Native executable is missing or not executable: " + executable);
        }
        return executable.toString();
    }

    /** Configures the native service with its isolated datasource and Kafka broker. */
    private GenericContainer<?> createNativeApplication() {
        String configuredImage = System.getProperty("customer-survey.native-image");
        GenericContainer<?> nativeApplication = configuredImage == null || configuredImage.isBlank()
                ? createContainerFromNativeExecutable()
                : new GenericContainer<>(DockerImageName.parse(configuredImage));
        return configureNativeApplication(nativeApplication);
    }

    /** Creates a runtime container around the executable compiled by the local native task. */
    private GenericContainer<?> createContainerFromNativeExecutable() {
        return new GenericContainer<>(DockerImageName.parse(NATIVE_APPLICATION_IMAGE))
                .withFileSystemBind(nativeExecutablePath(), "/app/application", BindMode.READ_ONLY)
                .withCommand("/app/application");
    }

    /** Adds the shared ephemeral services and startup check to the native application container. */
    private GenericContainer<?> configureNativeApplication(GenericContainer<?> nativeApplication) {
        return nativeApplication
                .withNetwork(TEST_NETWORK)
                .withExposedPorts(8090)
                .withEnv("DB_URL", "jdbc:mysql://survey-mysql:3306/customer_survey_test")
                .withEnv("DB_USERNAME", "test")
                .withEnv("DB_PASSWORD", "test")
                .withEnv("KAFKA_BOOTSTRAP_SERVERS", "survey-kafka:9092")
                .withEnv("ORG_SERVICE_URL", "http://127.0.0.1:1")
                .waitingFor(Wait.forLogMessage(".*Started CustomerSurveyBackendApplication.*\\n", 1));
    }

    /** Stages the unique test email in a committed JDBC transaction. */
    private void stageEmailOutboxEvent(String recipient) throws Exception {
        MysqlDataSource dataSource = buildTestDataSource();
        JdbcNotificationOutboxRepository repository = new JdbcNotificationOutboxRepository(dataSource, OUTBOX_TABLE);
        NotificationEnvelope envelope = new NotificationEnvelope(
                UUID.randomUUID().toString(),
                "db_test2",
                recipient,
                null,
                null,
                "evaluation_completed_v1",
                "en_US",
                null,
                Map.of(
                        "evaluator_name", "Evaluator",
                        "staff_id", "STAFF-001",
                        "evaluatee_name", "Evaluatee",
                        "project_id", "PROJECT-001",
                        "project_name", "Native e2e project",
                        "department_id", "Research",
                        "skillset", "General",
                        "form_type", "CARPENTER",
                        "evaluation_score", "88"));
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            repository.enqueue(connection, new NotificationOutboxEvent("email", envelope));
            connection.commit();
        }
    }

    /** Creates a test datasource using the ephemeral MySQL container credentials. */
    private MysqlDataSource buildTestDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(MYSQL.getJdbcUrl());
        dataSource.setUser(MYSQL.getUsername());
        dataSource.setPassword(MYSQL.getPassword());
        return dataSource;
    }

    /** Returns the host-accessible broker address used by the Kafka assertion probe. */
    private String kafkaBootstrapServers() {
        return KAFKA.getBootstrapServers();
    }
}
