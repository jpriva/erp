package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.CredentialValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum CredentialStatus {
    ACTIVE,
    DISABLED,
    EXPIRED,
    COMPROMISED;

    public static CredentialStatus of(String status) {
        var val = new ValidationError.Builder();
        if (status == null || status.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.STATUS_EMPTY).build()
            );
        }
        CredentialStatus credentialStatus;
        try {
            credentialStatus = CredentialStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.STATUS_INVALID).build()
            );
        }
        return credentialStatus;
    }
}
