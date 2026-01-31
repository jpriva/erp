package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.CredentialValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum CredentialType {
    PASSWORD,
    OPENID;

    public static CredentialType of(String type) {
        var val = new ValidationError.Builder();
        if (type == null || type.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.TYPE_EMPTY).build()
            );
        }
        CredentialType credentialType;
        try {
            credentialType = CredentialType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.TYPE_INVALID).build()
            );
        }
        return credentialType;
    }
}
