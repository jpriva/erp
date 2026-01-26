package com.jpriva.erpsp.auth.domain.model.user;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum UserStatus {
    ACTIVE,
    BLOCKED,
    EMAIL_NOT_VERIFIED;

    private static final String STATUS_NULL_ERROR = "Status can't be empty";
    private static final String STATUS_NOT_FOUND_ERROR = "Status doesn't exist";
    private static final String FIELD_STATUS = "status";


    public static UserStatus of(String status) {
        var val = new ValidationError.Builder();
        if (status == null || status.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_STATUS, STATUS_NULL_ERROR).build()
            );
        }
        UserStatus userStatus;
        try{
            userStatus = UserStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_STATUS, STATUS_NOT_FOUND_ERROR).build()
            );
        }
        return userStatus;
    }
}
