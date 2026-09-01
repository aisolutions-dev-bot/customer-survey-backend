package com.example.survey.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.survey.dto.NotificationConfigDTO;

/**
 * Reads the org-wide email/sms/whatsapp enabled flags from
 * ai-solutions-organization-api. Same endpoint evaluation-management-backend
 * already relies on for the exact same purpose.
 */
@Component
public class SystemParameterClient {

  private static final Logger LOG = LoggerFactory.getLogger(SystemParameterClient.class);
  private static final String NOTIFICATION_CONFIG_PATH = "/api/system-parameters/notifications";

  private final RestTemplate orgApiRestTemplate;

  public SystemParameterClient(RestTemplate orgApiRestTemplate) {
    this.orgApiRestTemplate = orgApiRestTemplate;
  }

  /**
   * Fetches the current notification flags from org-api.
   * Falls back to all-enabled if org-api is unreachable, so a flaky
   * dependency never silently blocks notifications the parameter says
   * should go out.
   */
  public NotificationConfigDTO fetchNotificationConfig() {
    try {
      return doFetchNotificationConfig();
    } catch (Exception e) {
      LOG.error("[SystemParameterClient] Failed to fetch notification config from org-api, defaulting all to enabled", e);
      return buildAllEnabledFallback();
    }
  }

  private NotificationConfigDTO doFetchNotificationConfig() {
    NotificationConfigDTO config = orgApiRestTemplate.getForObject(NOTIFICATION_CONFIG_PATH, NotificationConfigDTO.class);
    return config != null ? config : buildAllEnabledFallback();
  }

  private NotificationConfigDTO buildAllEnabledFallback() {
    NotificationConfigDTO fallback = new NotificationConfigDTO();
    fallback.setEmailEnabled(true);
    fallback.setSmsEnabled(true);
    fallback.setWhatsappEnabled(true);
    return fallback;
  }
}
