package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when a non-owner tries to transfer ownership.
 */
public class OnlyOwnerCanTransferException extends ErpValidationException {
    public OnlyOwnerCanTransferException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("currentOwner", message)
                        .build()
        );
    }
}
