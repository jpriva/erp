package com.jpriva.erpsp.notification.domain.exceptions;

import com.jpriva.erpsp.notification.domain.constants.NotificationErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpException;

public class ErpNotificationException extends ErpException {
    public ErpNotificationException(NotificationErrorCode code, Object... args) {
        super(NotificationErrorCode.NOTIFICATION_MODULE, code, args);
    }

    public ErpNotificationException(NotificationErrorCode code, Throwable cause, Object... args) {
        super(NotificationErrorCode.NOTIFICATION_MODULE, code, cause, args);
    }
}
