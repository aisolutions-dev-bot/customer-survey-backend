package com.example.survey.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mirrors ai-solutions-organization-api's /api/system-parameters/notifications response.
 * Tells us whether email/sms/whatsapp sending is currently switched on org-wide.
 */
@Data
@NoArgsConstructor
public class NotificationConfigDTO {
  private boolean emailEnabled;
  private boolean smsEnabled;
  private boolean whatsappEnabled;
}
