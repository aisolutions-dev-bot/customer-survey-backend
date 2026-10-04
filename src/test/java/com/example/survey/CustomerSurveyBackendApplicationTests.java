package com.example.survey;

import com.example.survey.testsupport.SurveyIntegrationTestBase;
import org.junit.jupiter.api.Test;

/** Verifies the application context boots against ephemeral MySQL and Kafka. */
class CustomerSurveyBackendApplicationTests extends SurveyIntegrationTestBase {

    /** Loads the full context, including the notification publisher. */
    @Test
    void contextLoads() {}
}
