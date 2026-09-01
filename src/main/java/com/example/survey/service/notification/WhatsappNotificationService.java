package com.example.survey.service.notification;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aisolutions.shared.service.whatsapp.MetaWhatsappService;
import com.aisolutions.shared.service.whatsapp.TemplateComponent;
import com.aisolutions.shared.service.whatsapp.TemplateComponent.NamedParameter;

/**
 * Sends the "evaluation completed" WhatsApp template to an evaluator,
 * using the same Meta template (evaluation_completed_v1) that
 * evaluation-management-backend already sends for project evaluations.
 */
@Service
public class WhatsappNotificationService {

  private static final Logger LOG = LoggerFactory.getLogger(WhatsappNotificationService.class);

  private static final String EVALUATION_COMPLETED_TEMPLATE_NAME = "evaluation_completed_v1";
  private static final String EVALUATION_COMPLETED_LANGUAGE_CODE = "en_US";

  private final MetaWhatsappService metaWhatsappService;

  public WhatsappNotificationService(MetaWhatsappService metaWhatsappService) {
    this.metaWhatsappService = metaWhatsappService;
  }

  public boolean sendEvaluationCompletedNotification(
      String recipientMobileNumber,
      String evaluatorName,
      String evaluateeName,
      String projectName,
      String formType,
      Long evaluationScore) {

    String normalizedMobile = normalizeMobileNumber(recipientMobileNumber);
    String scoreText = evaluationScore != null ? String.valueOf(evaluationScore) : "";

    try {
      metaWhatsappService.sendTemplate(
          normalizedMobile,
          EVALUATION_COMPLETED_TEMPLATE_NAME,
          EVALUATION_COMPLETED_LANGUAGE_CODE,
          List.of(TemplateComponent.bodyNamed(
              new NamedParameter("evaluator_name", evaluatorName),
              new NamedParameter("evaluatee_name", evaluateeName),
              new NamedParameter("project_name", projectName),
              new NamedParameter("form_type", formType),
              new NamedParameter("evaluation_score", scoreText))));
      return true;
    } catch (Exception e) {
      LOG.error("[WhatsappNotification] Failed to send evaluation_completed_v1 to={}", normalizedMobile, e);
      return false;
    }
  }

  /** Meta's Cloud API expects E.164 without the leading '+'. */
  private String normalizeMobileNumber(String mobileNumber) {
    if (mobileNumber == null) {
      return null;
    }
    return mobileNumber.startsWith("+") ? mobileNumber.substring(1) : mobileNumber;
  }
}
