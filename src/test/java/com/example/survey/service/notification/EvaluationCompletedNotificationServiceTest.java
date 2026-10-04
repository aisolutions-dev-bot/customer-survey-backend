package com.example.survey.service.notification;

import java.util.Optional;

import com.aisolutions.shared.notification.spring.SpringNotificationPublisher;
import com.example.survey.dto.StaffDTO;
import com.example.survey.model.EvaluationDistributionNonProj;
import com.example.survey.model.EvaluationRating;
import com.example.survey.repository.StaffRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/** Verifies the completion notifier renders content and hands every enabled channel to the publisher. */
@ExtendWith(MockitoExtension.class)
class EvaluationCompletedNotificationServiceTest {

    private static final String COMPANY_DATABASE = "db_test2";
    private static final String EVALUATOR_ID = "E1";
    private static final String EVALUATEE_ID = "M1";
    private static final String EVALUATOR_NAME = "Eve";
    private static final String EVALUATEE_NAME = "Mona";
    private static final String EVALUATOR_EMAIL = "eve@example.com";
    private static final String EVALUATOR_MOBILE = "+60100";

    @Mock
    StaffRepository staffRepository;

    @Mock
    NotificationConfigCacheService notificationConfigCacheService;

    @Mock
    NotificationCompanyResolver notificationCompanyResolver;

    @Mock
    SpringNotificationPublisher springNotificationPublisher;

    /** Delegates recipient lookups and channel publishing, then verifies every channel. */
    @Test
    void publishesEveryEnabledChannelThroughThePublisher() {
        enableEveryChannel();
        stubStaff();

        newService().notifyEvaluatorOfCompletion(buildDistribution(), buildRating());

        verify(springNotificationPublisher)
                .enqueueEmailTemplate(
                        eq(COMPANY_DATABASE),
                        eq(EVALUATOR_EMAIL),
                        eq("evaluation_completed_v1"),
                        eq("en_US"),
                        eq(expectedTemplateParameters()));
        verify(springNotificationPublisher)
                .enqueueSmsTemplate(
                        eq(COMPANY_DATABASE),
                        eq(EVALUATOR_MOBILE),
                        eq("evaluation_completed_v1"),
                        eq("en_US"),
                        eq(expectedTemplateParameters()));
        verify(springNotificationPublisher)
                .enqueueWhatsappTemplate(
                        eq(COMPANY_DATABASE),
                        eq(EVALUATOR_MOBILE),
                        eq("evaluation_completed_v1"),
                        eq("en_US"),
                        eq(expectedTemplateParameters()));
    }

    /** Verifies the org-wide flags suppress publishing without touching the publisher. */
    @Test
    void stagesNothingWhenEveryChannelIsDisabled() {
        when(notificationConfigCacheService.isEmailEnabled()).thenReturn(false);
        when(notificationConfigCacheService.isSmsEnabled()).thenReturn(false);
        when(notificationConfigCacheService.isWhatsappEnabled()).thenReturn(false);
        stubStaff();

        newService().notifyEvaluatorOfCompletion(buildDistribution(), buildRating());

        verifyNoInteractions(springNotificationPublisher);
    }

    /** Turns on all three org-wide channel flags. */
    private void enableEveryChannel() {
        when(notificationConfigCacheService.isEmailEnabled()).thenReturn(true);
        when(notificationConfigCacheService.isSmsEnabled()).thenReturn(true);
        when(notificationConfigCacheService.isWhatsappEnabled()).thenReturn(true);
    }

    /** Stubs the evaluator and evaluatee lookups used by the notice builders. */
    private void stubStaff() {
        when(staffRepository.findByStaffId(EVALUATOR_ID))
                .thenReturn(
                        Optional.of(new TestStaff(EVALUATOR_ID, EVALUATOR_NAME, EVALUATOR_MOBILE, EVALUATOR_EMAIL)));
        when(staffRepository.findByStaffId(EVALUATEE_ID))
                .thenReturn(Optional.of(new TestStaff(EVALUATEE_ID, EVALUATEE_NAME, null, null)));
    }

    /** Builds the service under test with its mocked collaborators. */
    private EvaluationCompletedNotificationService newService() {
        when(notificationCompanyResolver.resolveRequiredCompanyId()).thenReturn(COMPANY_DATABASE);
        return new EvaluationCompletedNotificationService(
                staffRepository,
                notificationConfigCacheService,
                notificationCompanyResolver,
                springNotificationPublisher);
    }

    /** Builds a non-project distribution that points at the evaluator. */
    private EvaluationDistributionNonProj buildDistribution() {
        return EvaluationDistributionNonProj.builder()
                .evaluatorId(EVALUATOR_ID)
                .projectName("Project X")
                .build();
    }

    /** Builds a scored rating that points at the evaluatee. */
    private EvaluationRating buildRating() {
        EvaluationRating rating = new EvaluationRating();
        rating.setEvaluateeId(EVALUATEE_ID);
        rating.setFormType("CARPENTER");
        rating.setWeightedScore(88.0);
        return rating;
    }

    /** Returns the union of declared channel parameters sent to the registry. */
    private java.util.Map<String, Object> expectedTemplateParameters() {
        return java.util.Map.of(
                "evaluator_name", EVALUATOR_NAME,
                "staff_id", EVALUATEE_ID,
                "evaluatee_name", EVALUATEE_NAME,
                "project_id", "",
                "project_name", "Project X",
                "department_id", "",
                "skillset", "",
                "form_type", "CARPENTER",
                "evaluation_score", "88");
    }

    /** Minimal staff projection implementation for the notifier unit test. */
    private record TestStaff(String staffId, String name, String telMobile, String emailCompany) implements StaffDTO {

        @Override
        public String getStaffId() {
            return staffId;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getFormType() {
            return null;
        }

        @Override
        public String getTelMobile() {
            return telMobile;
        }

        @Override
        public String getEmailCompany() {
            return emailCompany;
        }
    }
}
