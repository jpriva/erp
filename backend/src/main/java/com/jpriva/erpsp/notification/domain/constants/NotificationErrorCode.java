package com.jpriva.erpsp.notification.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ErrorCode;

public enum NotificationErrorCode implements ErrorCode {
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "Notification not found.", 404),
    EMAIL_NOTIFICATION_NOT_SENT("EMAIL_NOTIFICATION_NOT_SENT", "Failed to send email notification.", 500);

    public static final String NOTIFICATION_MODULE = "NOTIFICATION";

    private final String code;
    private final String message;
    private final int status;

    NotificationErrorCode(String code, String message, int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getStatus() {
        return status;
    }
}
