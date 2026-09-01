package com.example.survey.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aisolutions.shared.service.email.EmailService;

/**
 * Thin wrapper around the shared EmailService that never throws —
 * a failed send is logged and reported as false, not propagated.
 */
@Service
public class EmailNotificationService {

  private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationService.class);

  private final EmailService emailService;

  public EmailNotificationService(EmailService emailService) {
    this.emailService = emailService;
  }

  public boolean send(String to, String subject, String htmlBody) {
    try {
      emailService.sendEmail(to, subject, htmlBody);
      return true;
    } catch (Exception e) {
      LOG.error("[EmailNotification] Failed to send email to={}", to, e);
      return false;
    }
  }
}
