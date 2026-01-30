package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to remove the owner without first transferring ownership to another user.
 */
public class MustTransferOwnershipFirstException extends ErpValidationException {
    public MustTransferOwnershipFirstException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("userId", message)
                        .build()
        );
    }
}
