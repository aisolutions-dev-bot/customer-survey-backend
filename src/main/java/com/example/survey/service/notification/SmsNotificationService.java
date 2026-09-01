package com.example.survey.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aisolutions.shared.service.sms.SmsService;

/**
 * Thin wrapper around the shared SmsService that never throws —
 * a failed send is logged and reported as false, not propagated.
 */
@Service
public class SmsNotificationService {

  private static final Logger LOG = LoggerFactory.getLogger(SmsNotificationService.class);

  private final SmsService smsService;

  public SmsNotificationService(SmsService smsService) {
    this.smsService = smsService;
  }

  public boolean send(String e164Mobile, String message) {
    try {
      smsService.sendSms(e164Mobile, message);
      return true;
    } catch (Exception e) {
      LOG.error("[SmsNotification] Failed to send SMS to={}", e164Mobile, e);
      return false;
    }
  }
}
