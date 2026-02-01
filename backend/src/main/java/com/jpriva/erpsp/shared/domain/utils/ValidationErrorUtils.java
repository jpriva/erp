package com.jpriva.erpsp.shared.domain.utils;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public class ValidationErrorUtils {
    private ValidationErrorUtils() {
    }

    public static void validate(String module, ValidationError.Builder val) {
        if (val.hasErrors()) {
            ValidationError errors = val.build();
            for (ValidationErrorCode error : errors.errors()) {
                System.out.println("ERRORS");
                System.out.println(error.getField() + ": " + error.getMessage());
            }
            throw new ErpValidationException(module, errors);
        }
    }

    public static String errorGreaterOrEqualThan(String field, int limit, String unit) {
        return String.format("%s must be greater or equal than %d %s", field, limit, unit);
    }

    public static String errorLessOrEqualThan(String field, int limit, String unit) {
        return String.format("%s must be less or equal than %d %s", field, limit, unit);
    }
}
