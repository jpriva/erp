package com.jpriva.erpsp.notification.domain.exceptions;

import com.jpriva.erpsp.notification.domain.constants.NotificationErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public class ErpNotificationValidationException extends ErpValidationException {

    public ErpNotificationValidationException(ValidationError errors) {
        super(NotificationErrorCode.NOTIFICATION_MODULE, errors);
    }

    public ErpNotificationValidationException(ValidationError errors, Throwable cause) {
        super(NotificationErrorCode.NOTIFICATION_MODULE, errors, cause);
    }

}
