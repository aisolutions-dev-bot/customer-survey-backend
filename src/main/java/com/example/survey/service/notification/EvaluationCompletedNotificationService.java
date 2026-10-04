package com.example.survey.service.notification;

import java.util.List;
import java.util.Map;

import com.aisolutions.shared.notification.spring.SpringNotificationPublisher;
import com.aisolutions.shared.service.whatsapp.TemplateComponent;
import com.aisolutions.shared.service.whatsapp.TemplateComponent.NamedParameter;
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
 * {@link #resolveEvaluateeName}, renders each channel in {@link #buildSmsText},
 * {@link #buildEmailBody} and {@link #buildWhatsappComponents}, then publishes the
 * rendered content through {@link SpringNotificationPublisher}. The delivery request
 * is staged on the rating transaction so either both records commit or neither does.
 */
@Service
public class EvaluationCompletedNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(EvaluationCompletedNotificationService.class);

    private static final String EMAIL_SUBJECT_TEMPLATE = "Evaluation Submitted Successfully - %s";
    private static final String WHATSAPP_TEMPLATE_NAME = "evaluation_completed_v1";
    private static final String WHATSAPP_LANGUAGE_CODE = "en_US";
    private static final String MISSING_SCORE_TEXT = "-";
    private static final String SMS_LINE_BREAK = "\r\n";

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
                evaluator.getEmailCompany(),
                evaluator.getTelMobile(),
                resolveEvaluateeName(rating.getEvaluateeId()),
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
                buildWhatsappComponents(notice));
    }

    /** Stages the rendered SMS when SMS is enabled and a mobile number exists. */
    private void dispatchSms(EvaluationCompletionNotice notice) {
        if (!shouldStageChannel(
                NotificationChannel.SMS, notificationConfigCacheService.isSmsEnabled(), notice.evaluatorMobile())) {
            return;
        }
        springNotificationPublisher.enqueueSms(notice.companyId(), notice.evaluatorMobile(), buildSmsText(notice));
    }

    /** Stages the rendered email when email is enabled and a company address exists. */
    private void dispatchEmail(EvaluationCompletionNotice notice) {
        if (!shouldStageChannel(
                NotificationChannel.EMAIL, notificationConfigCacheService.isEmailEnabled(), notice.evaluatorEmail())) {
            return;
        }
        springNotificationPublisher.enqueueEmail(
                notice.companyId(), notice.evaluatorEmail(), buildEmailSubject(notice), buildEmailBody(notice));
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

    /** Builds the subject line naming the evaluatee. */
    private String buildEmailSubject(EvaluationCompletionNotice notice) {
        return String.format(EMAIL_SUBJECT_TEMPLATE, notice.evaluateeName());
    }

    /** Renders the plain-text SMS confirmation. */
    private String buildSmsText(EvaluationCompletionNotice notice) {
        return String.join(
                SMS_LINE_BREAK,
                "Your evaluation has been submitted successfully.",
                "Staff: " + notice.evaluateeName(),
                "Reference: " + notice.projectName(),
                "Form Type: " + notice.formType(),
                "Score: " + notice.score(),
                "Thank you.");
    }

    /** Renders the HTML email confirmation. */
    private String buildEmailBody(EvaluationCompletionNotice notice) {
        String scoreText = notice.score() != null ? String.valueOf(notice.score()) : MISSING_SCORE_TEXT;
        return """
        Dear %s,<br>
        <br>
        This is to confirm that your evaluation has been submitted successfully.<br>
        Below are the details for your reference:<br>
        <br>
        Evaluatee : %s<br>
        Reference : %s<br>
        Form Type : %s<br>
        Evaluation Score : %s/100<br>
        <br>
        Thank you for completing the evaluation promptly.<br>
        <br>
        Best regards,<br>
        Evaluation Management System
        """.formatted(
                notice.evaluatorName(), notice.evaluateeName(), notice.projectName(), notice.formType(), scoreText);
    }

    /** Builds the approved template's named body parameters for the WhatsApp payload. */
    private List<Map<String, Object>> buildWhatsappComponents(EvaluationCompletionNotice notice) {
        String scoreText = notice.score() != null ? String.valueOf(notice.score()) : "";
        return List.of(TemplateComponent.bodyNamed(
                        new NamedParameter("evaluator_name", notice.evaluatorName()),
                        new NamedParameter("evaluatee_name", notice.evaluateeName()),
                        new NamedParameter("project_name", notice.projectName()),
                        new NamedParameter("form_type", notice.formType()),
                        new NamedParameter("evaluation_score", scoreText))
                .toMap());
    }

    /** Reports whether a contact detail is absent or whitespace-only. */
    private boolean isAbsent(String value) {
        return value == null || value.isBlank();
    }

    /** Carries the recipient and evaluation facts rendered into every channel. */
    record EvaluationCompletionNotice(
            String evaluatorName,
            String evaluatorEmail,
            String evaluatorMobile,
            String evaluateeName,
            String projectName,
            String formType,
            Long score,
            String companyId) {}
}
