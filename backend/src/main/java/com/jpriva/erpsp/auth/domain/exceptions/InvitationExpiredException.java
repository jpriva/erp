package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to accept an expired invitation.
 */
public class InvitationExpiredException extends ErpValidationException {
    public InvitationExpiredException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("token", message)
                        .build()
        );
    }
}
