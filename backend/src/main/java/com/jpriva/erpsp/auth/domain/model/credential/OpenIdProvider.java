package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum OpenIdProvider {
    GOOGLE,
    GITHUB,
    MICROSOFT,
    APPLE;

    private static final String PROVIDER_NULL_ERROR = "OpenID provider can't be empty";
    private static final String PROVIDER_NOT_FOUND_ERROR = "OpenID provider doesn't exist";
    private static final String FIELD_PROVIDER = "openIdProvider";

    public static OpenIdProvider of(String provider) {
        var val = new ValidationError.Builder();
        if (provider == null || provider.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_PROVIDER, PROVIDER_NULL_ERROR).build()
            );
        }
        OpenIdProvider openIdProvider;
        try {
            openIdProvider = OpenIdProvider.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_PROVIDER, PROVIDER_NOT_FOUND_ERROR).build()
            );
        }
        return openIdProvider;
    }
}
