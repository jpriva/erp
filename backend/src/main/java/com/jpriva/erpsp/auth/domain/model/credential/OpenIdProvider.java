package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.CredentialValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum OpenIdProvider {
    GOOGLE,
    GITHUB,
    MICROSOFT,
    APPLE;

    public static OpenIdProvider of(String provider) {
        var val = new ValidationError.Builder();
        if (provider == null || provider.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.OPEN_ID_PROVIDER_EMPTY).build()
            );
        }
        OpenIdProvider openIdProvider;
        try {
            openIdProvider = OpenIdProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(CredentialValidationError.OPEN_ID_PROVIDER_NOT_FOUND).build()
            );
        }
        return openIdProvider;
    }
}
