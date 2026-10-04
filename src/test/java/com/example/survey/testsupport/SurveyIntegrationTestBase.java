package com.example.survey.testsupport;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Starts ephemeral MySQL and Kafka containers for the integration tests.
 *
 * <p>The shared Spring integration is pinned to the container broker through
 * {@code notification.outbox.bootstrap-servers}.
 */
@SpringBootTest
public abstract class SurveyIntegrationTestBase {

    protected static final Network TEST_NETWORK = Network.newNetwork();

    protected static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("customer_survey_test")
            .withUsername("test")
            .withPassword("test")
            .withNetwork(TEST_NETWORK)
            .withNetworkAliases("survey-mysql");

    protected static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("apache/kafka:4.3.1"))
            .withNetwork(TEST_NETWORK)
            .withNetworkAliases("survey-kafka");

    static {
        startSharedContainers();
    }

    /** Starts singleton containers so Spring's cached context never retains expired ports. */
    private static void startSharedContainers() {
        MYSQL.start();
        KAFKA.start();
    }

    /** Delegates datasource and broker pinning to their helpers. */
    @DynamicPropertySource
    static void registerContainerProperties(DynamicPropertyRegistry registry) {
        registerDatasourceProperties(registry);
        registerKafkaProperties(registry);
    }

    /** Points the default datasource at the ephemeral MySQL container. */
    private static void registerDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("tenant.default-company-id", () -> "db_test2");
    }

    /** Pins the shared broker and every notification channel to the container broker. */
    private static void registerKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("notification.outbox.bootstrap-servers", KAFKA::getBootstrapServers);
    }
}
