package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when the email of the user accepting an invitation doesn't match the invitation's email.
 */
public class InvitationEmailMismatchException extends ErpValidationException {
    public InvitationEmailMismatchException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("email", message)
                        .build()
        );
    }
}
