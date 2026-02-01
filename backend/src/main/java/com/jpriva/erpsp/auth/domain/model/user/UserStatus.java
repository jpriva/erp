package com.jpriva.erpsp.auth.domain.model.user;

import com.jpriva.erpsp.auth.domain.constants.UserValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum UserStatus {
    ACTIVE,
    BLOCKED,
    EMAIL_NOT_VERIFIED;

    public static UserStatus of(String status) {
        var val = new ValidationError.Builder();
        if (status == null || status.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(UserValidationError.STATUS_EMPTY).build()
            );
        }
        UserStatus userStatus;
        try {
            userStatus = UserStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(UserValidationError.STATUS_NOT_FOUND).build()
            );
        }
        return userStatus;
    }
}
