package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to use a role that does not belong to the specified tenant.
 */
public class RoleDoesNotBelongToTenantException extends ErpValidationException {
    public RoleDoesNotBelongToTenantException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("roleId", message)
                        .build()
        );
    }
}
