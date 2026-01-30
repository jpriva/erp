package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

/**
 * Thrown when trying to revoke the last role of an active membership.
 * Use removeMemberFromTenant() instead to completely remove a user.
 */
public class CannotRevokeLastRoleException extends ErpValidationException {
    public CannotRevokeLastRoleException(String message) {
        super(AuthErrorCode.AUTH_MODULE,
                new ValidationError.Builder()
                        .addError("roleId", message)
                        .build()
        );
    }
}
