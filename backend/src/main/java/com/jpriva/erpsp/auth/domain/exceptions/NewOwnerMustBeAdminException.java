package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to transfer ownership to a user who is not an ADMIN.
 */
public class NewOwnerMustBeAdminException extends ErpValidationException {
    public NewOwnerMustBeAdminException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("newOwner", message)
                        .build()
        );
    }
}
