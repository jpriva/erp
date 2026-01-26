package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpErrorCodes;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.regex.Pattern;

public record Email(String value) {

    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    private static final String FIELD_NAME = "email";
    private static final String EMPTY_VALUE = "Can't create an empty email";
    private static final String INVALID_FORMAT = "Invalid email format";

    public Email {
        var val = ValidationError.builder();
        if (value == null) {
            value = "";
        }
        if (value.isBlank()) {
            val.addError(FIELD_NAME, EMPTY_VALUE);
        }
        value = value.trim();
        if (value.length() > 254){
            val.addError(FIELD_NAME, ValidationErrorUtils.errorLessOrEqualThan(FIELD_NAME, 254, "characters"));
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            val.addError(FIELD_NAME, INVALID_FORMAT);
        }

        ValidationErrorUtils.validate(ErpErrorCodes.SHARED_MODULE, val);
    }
}
