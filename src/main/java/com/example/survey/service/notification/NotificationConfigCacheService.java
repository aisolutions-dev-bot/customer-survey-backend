package com.example.survey.service.notification;

import jakarta.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.survey.client.SystemParameterClient;
import com.example.survey.dto.NotificationConfigDTO;

/**
 * Caches the org-wide email/sms/whatsapp enabled flags in memory so every
 * evaluation submission doesn't have to call org-api synchronously.
 * Refreshed on startup and periodically thereafter.
 */
@Service
public class NotificationConfigCacheService {

  private static final long REFRESH_INTERVAL_MS = 5 * 60 * 1000;

  private final SystemParameterClient systemParameterClient;

  private volatile boolean emailEnabled = true;
  private volatile boolean smsEnabled = true;
  private volatile boolean whatsappEnabled = true;

  public NotificationConfigCacheService(SystemParameterClient systemParameterClient) {
    this.systemParameterClient = systemParameterClient;
  }

  @PostConstruct
  public void loadInitialConfig() {
    refreshConfig();
  }

  @Scheduled(fixedRate = REFRESH_INTERVAL_MS)
  public void refreshConfig() {
    NotificationConfigDTO config = systemParameterClient.fetchNotificationConfig();
    applyConfig(config);
  }

  private void applyConfig(NotificationConfigDTO config) {
    this.emailEnabled = config.isEmailEnabled();
    this.smsEnabled = config.isSmsEnabled();
    this.whatsappEnabled = config.isWhatsappEnabled();
  }

  public boolean isEmailEnabled() {
    return emailEnabled;
  }

  public boolean isSmsEnabled() {
    return smsEnabled;
  }

  public boolean isWhatsappEnabled() {
    return whatsappEnabled;
  }
}
