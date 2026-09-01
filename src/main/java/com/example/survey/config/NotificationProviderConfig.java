package com.example.survey.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aisolutions.shared.service.email.EmailConfig;
import com.aisolutions.shared.service.email.EmailService;
import com.aisolutions.shared.service.sms.SmsProperties;
import com.aisolutions.shared.service.sms.SmsService;
import com.aisolutions.shared.service.whatsapp.MetaWhatsappProperties;
import com.aisolutions.shared.service.whatsapp.MetaWhatsappService;

/**
 * Wires the framework-agnostic email/sms/whatsapp clients from
 * ai-solutions-java-shared into Spring beans, using the same credential
 * property names already used by evaluation-management-backend so the
 * Railway secrets can be reused as-is.
 */
@Configuration
public class NotificationProviderConfig {

  @Value("${app.email.host}")
  private String smtpHost;

  @Value("${app.email.port}")
  private int smtpPort;

  @Value("${app.email.password}")
  private String smtpPassword;

  @Value("${app.email.sender-email}")
  private String smtpSenderEmail;

  @Value("${sms.url}")
  private String smsUrl;

  @Value("${sms.username}")
  private String smsUsername;

  @Value("${sms.password}")
  private String smsPassword;

  @Value("${sms.sender-id}")
  private String smsSenderId;

  @Value("${whatsapp.meta.phone-number-id}")
  private String whatsappPhoneNumberId;

  @Value("${whatsapp.meta.access-token}")
  private String whatsappAccessToken;

  @Value("${whatsapp.meta.api-version}")
  private String whatsappApiVersion;

  @Bean
  public EmailService emailService() {
    return new EmailService(buildEmailConfig(), smtpHost, smtpPort);
  }

  /** Builds the shared-lib's SMTP credential holder from our properties. */
  private EmailConfig buildEmailConfig() {
    EmailConfig config = new EmailConfig();
    config.setSenderEmail(smtpSenderEmail);
    config.setSmtpPassword(smtpPassword);
    return config;
  }

  @Bean
  public SmsService smsService() {
    return new SmsService(buildSmsProperties());
  }

  /** Builds the shared-lib's SMS provider credential holder from our properties. */
  private SmsProperties buildSmsProperties() {
    SmsProperties properties = new SmsProperties();
    properties.setUrl(smsUrl);
    properties.setUsername(smsUsername);
    properties.setPassword(smsPassword);
    properties.setSenderId(smsSenderId);
    return properties;
  }

  @Bean
  public MetaWhatsappService metaWhatsappService() {
    return new MetaWhatsappService(buildMetaWhatsappProperties());
  }

  /** Builds the shared-lib's Meta WhatsApp Cloud API credential holder from our properties. */
  private MetaWhatsappProperties buildMetaWhatsappProperties() {
    return new MetaWhatsappProperties(whatsappPhoneNumberId, whatsappAccessToken, whatsappApiVersion);
  }
}
