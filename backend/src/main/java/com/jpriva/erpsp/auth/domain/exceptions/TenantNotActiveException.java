package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to perform an action that requires an active tenant.
 */
public class TenantNotActiveException extends ErpValidationException {
    public TenantNotActiveException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("tenantId", message)
                        .build()
        );
    }
}
