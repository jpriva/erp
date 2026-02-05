package com.jpriva.erpsp.notification.domain.constants;

import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

public enum NotificationValidationError implements ValidationErrorCode {
    ID_EMPTY("notificationId", "validation.notification.id.empty", "Notification ID cannot be empty."),
    USER_ID_EMPTY("userId", "validation.notification.user.id.empty", "User ID cannot be empty.");


    private final String code;
    private final String message;
    private final String field;

    NotificationValidationError(String field, String code, String message) {
        this.field = field;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getField() {
        return this.field;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}