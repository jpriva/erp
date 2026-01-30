package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to create an invitation when a pending one already exists for the same email and tenant.
 */
public class InvitationAlreadyExistsException extends ErpValidationException {
    public InvitationAlreadyExistsException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("email", message)
                        .build()
        );
    }
}
