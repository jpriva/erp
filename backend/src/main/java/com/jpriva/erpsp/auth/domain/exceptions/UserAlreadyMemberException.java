package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to add a user who is already a member of the tenant.
 */
public class UserAlreadyMemberException extends ErpValidationException {
    public UserAlreadyMemberException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("userId", message)
                        .build()
        );
    }
}
