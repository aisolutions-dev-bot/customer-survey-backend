package com.example.survey.service.notification;

/**
 * Names an outbound notification channel for delivery-skip logging.
 */
enum NotificationChannel {
    EMAIL("email"),
    SMS("sms"),
    WHATSAPP("whatsapp");

    private final String outboxChannelToken;

    /** Binds the platform channel token used in skip logs. */
    NotificationChannel(String outboxChannelToken) {
        this.outboxChannelToken = outboxChannelToken;
    }

    /** Returns the platform channel token used in skip logs. */
    String outboxChannelToken() {
        return outboxChannelToken;
    }
}
