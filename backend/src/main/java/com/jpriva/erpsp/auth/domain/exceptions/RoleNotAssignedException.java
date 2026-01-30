package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to revoke a role that is not assigned to a member.
 */
public class RoleNotAssignedException extends ErpValidationException {
    public RoleNotAssignedException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("roleId", message)
                        .build()
        );
    }
}
