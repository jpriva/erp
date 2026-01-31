package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpErrorCodes;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.regex.Pattern;

public record Email(String value) {

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");

    public Email {
        var val = ValidationError.builder();
        if (value == null) {
            value = "";
        }
        if (value.isBlank()) {
            val.addError(ErpValidationError.EMAIL_EMPTY);
        }
        value = value.trim();
        if (value.length() > 254) {
            val.addError(ErpValidationError.EMAIL_MAX_LENGTH);
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            val.addError(ErpValidationError.EMAIL_INVALID_FORMAT);
        }

        ValidationErrorUtils.validate(ErpErrorCodes.SHARED_MODULE, val);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return value;
    }
}
