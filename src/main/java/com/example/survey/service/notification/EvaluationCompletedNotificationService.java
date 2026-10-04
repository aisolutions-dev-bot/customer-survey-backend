package com.example.survey.service.notification;

import java.util.LinkedHashMap;
import java.util.Map;

import com.aisolutions.shared.notification.spring.SpringNotificationPublisher;
import com.example.survey.dto.StaffDTO;
import com.example.survey.model.EvaluationDistributionNonProj;
import com.example.survey.model.EvaluationRating;
import com.example.survey.repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Renders and publishes the "evaluation completed" notification after a
 * non-project submission.
 *
 * <p>Delegates recipient resolution to {@link #resolveEvaluator} and
 * {@link #resolveEvaluateeName}, then publishes registry template parameters through
 * {@link SpringNotificationPublisher}. The delivery request
 * is staged on the rating transaction so either both records commit or neither does.
 */
@Service
public class EvaluationCompletedNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationCompletedNotificationService.class);

    private static final String WHATSAPP_TEMPLATE_NAME = "evaluation_completed_v1";
    private static final String WHATSAPP_LANGUAGE_CODE = "en_US";
    private static final String TEMPLATE_LANGUAGE_CODE = "en_US";

    private final StaffRepository staffRepository;
    private final NotificationConfigCacheService notificationConfigCacheService;
    private final NotificationCompanyResolver notificationCompanyResolver;
    private final SpringNotificationPublisher springNotificationPublisher;

    /** Keeps recipient lookup, tenant resolution, channel flags and the shared Spring publisher. */
    public EvaluationCompletedNotificationService(
            StaffRepository staffRepository,
            NotificationConfigCacheService notificationConfigCacheService,
            NotificationCompanyResolver notificationCompanyResolver,
            SpringNotificationPublisher springNotificationPublisher) {
        this.staffRepository = staffRepository;
        this.notificationConfigCacheService = notificationConfigCacheService;
        this.notificationCompanyResolver = notificationCompanyResolver;
        this.springNotificationPublisher = springNotificationPublisher;
    }

    /** Delegates recipient resolution to buildNotice, then publishes each enabled channel. */
    public void notifyEvaluatorOfCompletion(EvaluationDistributionNonProj distribution, EvaluationRating rating) {
        StaffDTO evaluator = resolveEvaluator(distribution.getEvaluatorId());
        if (!hasContactInformation(evaluator)) {
            LOG.warn(
                    "[EvalCompletedNotify] No contact info for evaluatorId={}, skipping",
                    distribution.getEvaluatorId());
            return;
        }
        EvaluationCompletionNotice notice = buildNotice(distribution, rating, evaluator);
        dispatchWhatsapp(notice);
        dispatchSms(notice);
        dispatchEmail(notice);
    }

    /** Requires a mobile number or company address before any channel is published. */
    private boolean hasContactInformation(StaffDTO evaluator) {
        if (evaluator == null) {
            return false;
        }
        return !isAbsent(evaluator.getTelMobile()) || !isAbsent(evaluator.getEmailCompany());
    }

    /** Builds the recipient and evaluation facts shared by every channel helper. */
    private EvaluationCompletionNotice buildNotice(
            EvaluationDistributionNonProj distribution, EvaluationRating rating, StaffDTO evaluator) {
        return new EvaluationCompletionNotice(
                evaluator.getName(),
                rating.getEvaluateeId(),
                evaluator.getEmailCompany(),
                evaluator.getTelMobile(),
                resolveEvaluateeName(rating.getEvaluateeId()),
                distribution.getProjectId(),
                resolveProjectName(distribution),
                rating.getFormType(),
                resolveScore(rating),
                notificationCompanyResolver.resolveRequiredCompanyId());
    }

    /** Stages the approved template when WhatsApp is enabled and a mobile number exists. */
    private void dispatchWhatsapp(EvaluationCompletionNotice notice) {
        if (!shouldStageChannel(
                NotificationChannel.WHATSAPP,
                notificationConfigCacheService.isWhatsappEnabled(),
                notice.evaluatorMobile())) {
            return;
        }
        springNotificationPublisher.enqueueWhatsappTemplate(
                notice.companyId(),
                notice.evaluatorMobile(),
                WHATSAPP_TEMPLATE_NAME,
                WHATSAPP_LANGUAGE_CODE,
                templateParameters(notice));
    }

    /** Stages the SMS template when SMS is enabled and a mobile number exists. */
    private void dispatchSms(EvaluationCompletionNotice notice) {
        if (!shouldStageChannel(
                NotificationChannel.SMS, notificationConfigCacheService.isSmsEnabled(), notice.evaluatorMobile())) {
            return;
        }
        springNotificationPublisher.enqueueSmsTemplate(
                notice.companyId(),
                notice.evaluatorMobile(),
                WHATSAPP_TEMPLATE_NAME,
                TEMPLATE_LANGUAGE_CODE,
                templateParameters(notice));
    }

    /** Stages the email template when email is enabled and a company address exists. */
    private void dispatchEmail(EvaluationCompletionNotice notice) {
        if (!shouldStageChannel(
                NotificationChannel.EMAIL, notificationConfigCacheService.isEmailEnabled(), notice.evaluatorEmail())) {
            return;
        }
        springNotificationPublisher.enqueueEmailTemplate(
                notice.companyId(),
                notice.evaluatorEmail(),
                WHATSAPP_TEMPLATE_NAME,
                TEMPLATE_LANGUAGE_CODE,
                templateParameters(notice));
    }

    /** Records a disabled or incomplete channel before deciding whether to stage it. */
    private boolean shouldStageChannel(NotificationChannel channel, boolean channelEnabled, String recipient) {
        if (!channelEnabled) {
            LOG.info("[EvalCompletedNotify] Skipped channel={} reason=disabled", channel.outboxChannelToken());
            return false;
        }
        if (isAbsent(recipient)) {
            LOG.info("[EvalCompletedNotify] Skipped channel={} reason=recipient_missing", channel.outboxChannelToken());
            return false;
        }
        return true;
    }

    /** Looks up the evaluator, returning null when no staff id is present. */
    private StaffDTO resolveEvaluator(String staffId) {
        if (staffId == null) {
            return null;
        }
        return staffRepository.findByStaffId(staffId).orElse(null);
    }

    /** Falls back to the raw evaluatee id when the staff record has no name. */
    private String resolveEvaluateeName(String evaluateeId) {
        StaffDTO evaluatee = resolveEvaluator(evaluateeId);
        return evaluatee != null && !isAbsent(evaluatee.getName()) ? evaluatee.getName() : evaluateeId;
    }

    /** Prefers the distribution's project name and falls back to its project id. */
    private String resolveProjectName(EvaluationDistributionNonProj distribution) {
        return !isAbsent(distribution.getProjectName()) ? distribution.getProjectName() : distribution.getProjectId();
    }

    /** Rounds the stored weighted score, leaving it absent when not scored. */
    private Long resolveScore(EvaluationRating rating) {
        return rating.getWeightedScore() != null ? Math.round(rating.getWeightedScore()) : null;
    }

    /** Builds registry data for evaluation-completion templates without rendering channel content. */
    private Map<String, Object> templateParameters(EvaluationCompletionNotice notice) {
        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("evaluator_name", emptyIfNull(notice.evaluatorName()));
        parameters.put("staff_id", emptyIfNull(notice.evaluateeId()));
        parameters.put("evaluatee_name", emptyIfNull(notice.evaluateeName()));
        parameters.put("project_id", emptyIfNull(notice.projectId()));
        parameters.put("project_name", emptyIfNull(notice.projectName()));
        parameters.put("department_id", "");
        parameters.put("skillset", "");
        parameters.put("form_type", emptyIfNull(notice.formType()));
        parameters.put(
                "evaluation_score", notice.score() == null ? "" : notice.score().toString());
        return Map.copyOf(parameters);
    }

    /** Reports whether a contact detail is absent or whitespace-only. */
    private boolean isAbsent(String value) {
        return value == null || value.isBlank();
    }

    /** Carries the recipient and evaluation facts rendered by the notification registry. */
    record EvaluationCompletionNotice(
            String evaluatorName,
            String evaluateeId,
            String evaluatorEmail,
            String evaluatorMobile,
            String evaluateeName,
            String projectId,
            String projectName,
            String formType,
            Long score,
            String companyId) {}

    /** Replaces null values so the shared envelope can copy the parameter map. */
    private String emptyIfNull(String value) {
        return value == null ? "" : value;
    }
}
