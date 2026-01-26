package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.ports.out.PasswordHasherPort;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

public record Password(String value) {
    private final static String FIELD_NAME = "secret";
    private final static String EMPTY_VALUE = "Can't create an empty secret";

    public Password {
        var val = ValidationError.builder();
        if (value == null || value.isBlank()) {
            throw new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.addError(FIELD_NAME, EMPTY_VALUE).build());
        }
    }

    public static Password create(String raw, PasswordHasherPort hasher) {
        var val = ValidationError.builder();
        if (raw == null) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_NAME, EMPTY_VALUE).build()
            );
        }
        if (raw.length() < 8) {
            val.addError(
                    FIELD_NAME,
                    ValidationErrorUtils.errorGreaterOrEqualThan(FIELD_NAME, 8, "characters")).build();
        }
        if (raw.length() > 40) {
            val.addError(
                    FIELD_NAME,
                    ValidationErrorUtils.errorLessOrEqualThan(FIELD_NAME, 40, "characters")).build();
        }
        if (val.hasErrors()) {
            throw new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
        }
        return new Password(hasher.encode(raw));
    }

    public static Password fromPersistence(String passwordHashed) {
        return new Password(passwordHashed);
    }

    public boolean matches(String raw, PasswordHasherPort hasher) {
        return hasher.matches(raw, this.value);
    }

}
