package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum CredentialStatus {
    ACTIVE,
    DISABLED,
    EXPIRED,
    COMPROMISED;

    private static final String STATUS_NULL_ERROR = "Credential status can't be empty";
    private static final String STATUS_NOT_FOUND_ERROR = "Credential status doesn't exist";
    private static final String FIELD_STATUS = "credentialStatus";

    public static CredentialStatus of(String status) {
        var val = new ValidationError.Builder();
        if (status == null || status.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_STATUS, STATUS_NULL_ERROR).build()
            );
        }
        CredentialStatus credentialStatus;
        try {
            credentialStatus = CredentialStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_STATUS, STATUS_NOT_FOUND_ERROR).build()
            );
        }
        return credentialStatus;
    }
}
