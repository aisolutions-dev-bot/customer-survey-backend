package com.example.survey.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.survey.dto.StaffDTO;
import com.example.survey.model.EvaluationDistributionNonProj;
import com.example.survey.model.EvaluationRating;
import com.example.survey.repository.StaffRepository;

/**
 * Notifies an evaluator by email/sms/whatsapp right after they submit a
 * non-project evaluation form, gated by the org-wide notification flags.
 * Mirrors evaluation-management-backend's EvaluationFormNotificationService,
 * built here so non-project submissions don't depend on a second frontend
 * call to another backend succeeding.
 */
@Service
public class EvaluationCompletedNotificationService {

  private static final Logger LOG = LoggerFactory.getLogger(EvaluationCompletedNotificationService.class);
  private static final String EMAIL_SUBJECT_TEMPLATE = "Evaluation Submitted Successfully - %s";

  private final StaffRepository staffRepository;
  private final NotificationConfigCacheService notificationConfigCacheService;
  private final EmailNotificationService emailNotificationService;
  private final SmsNotificationService smsNotificationService;
  private final WhatsappNotificationService whatsappNotificationService;

  public EvaluationCompletedNotificationService(
      StaffRepository staffRepository,
      NotificationConfigCacheService notificationConfigCacheService,
      EmailNotificationService emailNotificationService,
      SmsNotificationService smsNotificationService,
      WhatsappNotificationService whatsappNotificationService) {
    this.staffRepository = staffRepository;
    this.notificationConfigCacheService = notificationConfigCacheService;
    this.emailNotificationService = emailNotificationService;
    this.smsNotificationService = smsNotificationService;
    this.whatsappNotificationService = whatsappNotificationService;
  }

  /**
   * Sends the "evaluation completed" notification for a non-project
   * submission. Never throws — a notification failure must not roll back
   * the rating that was already saved.
   */
  public void notifyEvaluatorOfCompletion(EvaluationDistributionNonProj distribution, EvaluationRating rating) {
    try {
      doNotifyEvaluatorOfCompletion(distribution, rating);
    } catch (Exception e) {
      LOG.error("[EvalCompletedNotify] Failed for non-project distribution uniqId={}", distribution.getUniqId(), e);
    }
  }

  private void doNotifyEvaluatorOfCompletion(EvaluationDistributionNonProj distribution, EvaluationRating rating) {
    StaffDTO evaluator = resolveStaff(distribution.getEvaluatorId());
    boolean hasNoContactInfo = evaluator != null
        && evaluator.getTelMobile() == null
        && evaluator.getEmailCompany() == null;
    if (evaluator == null || hasNoContactInfo) {
      LOG.warn("[EvalCompletedNotify] No contact info for evaluatorId={}, skipping", distribution.getEvaluatorId());
      return;
    }

    String evaluateeName = resolveEvaluateeName(rating.getEvaluateeId());
    Long score = rating.getWeightedScore() != null ? Math.round(rating.getWeightedScore()) : null;
    String projectName = distribution.getProjectName() != null
        ? distribution.getProjectName() : distribution.getProjectId();

    dispatchWhatsapp(evaluator, evaluateeName, projectName, rating.getFormType(), score);
    dispatchSms(evaluator, evaluateeName, projectName, rating.getFormType(), score);
    dispatchEmail(evaluator, evaluateeName, projectName, rating.getFormType(), score);
  }

  private StaffDTO resolveStaff(String staffId) {
    if (staffId == null) {
      return null;
    }
    return staffRepository.findByStaffId(staffId).orElse(null);
  }

  private String resolveEvaluateeName(String evaluateeId) {
    StaffDTO evaluatee = resolveStaff(evaluateeId);
    return evaluatee != null && evaluatee.getName() != null ? evaluatee.getName() : evaluateeId;
  }

  private void dispatchWhatsapp(StaffDTO evaluator, String evaluateeName, String projectName, String formType, Long score) {
    if (!notificationConfigCacheService.isWhatsappEnabled() || evaluator.getTelMobile() == null) {
      return;
    }
    whatsappNotificationService.sendEvaluationCompletedNotification(
        evaluator.getTelMobile(), evaluator.getName(), evaluateeName, projectName, formType, score);
  }

  private void dispatchSms(StaffDTO evaluator, String evaluateeName, String projectName, String formType, Long score) {
    if (!notificationConfigCacheService.isSmsEnabled() || evaluator.getTelMobile() == null) {
      return;
    }
    String smsText = buildSmsText(evaluateeName, projectName, formType, score);
    smsNotificationService.send(evaluator.getTelMobile(), smsText);
  }

  private void dispatchEmail(StaffDTO evaluator, String evaluateeName, String projectName, String formType, Long score) {
    if (!notificationConfigCacheService.isEmailEnabled() || evaluator.getEmailCompany() == null) {
      return;
    }
    String subject = String.format(EMAIL_SUBJECT_TEMPLATE, evaluateeName);
    String body = buildEmailBody(evaluator.getName(), evaluateeName, projectName, formType, score);
    emailNotificationService.send(evaluator.getEmailCompany(), subject, body);
  }

  private String buildSmsText(String evaluateeName, String projectName, String formType, Long score) {
    return "Your evaluation has been submitted successfully.\r\n" +
        "Staff: " + evaluateeName + "\r\n" +
        "Reference: " + projectName + "\r\n" +
        "Form Type: " + formType + "\r\n" +
        "Score: " + score + "\r\n" +
        "Thank you.";
  }

  private String buildEmailBody(String evaluatorName, String evaluateeName, String projectName, String formType, Long score) {
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
        """.formatted(evaluatorName, evaluateeName, projectName, formType, score != null ? score : "-");
  }
}
