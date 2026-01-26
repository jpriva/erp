package com.jpriva.erpsp.shared.domain.utils;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public class ValidationErrorUtils {
    private ValidationErrorUtils(){}

    public static void validate(String module, ValidationError.Builder val) {
        if (val.hasErrors()){
            throw new ErpValidationException(module, val.build());
        }
    }

    public static String errorGreaterOrEqualThan(String field, int limit, String unit) {
        return String.format("%s must be greater or equal than %d %s", field, limit, unit);
    }
    public static String errorLessOrEqualThan(String field, int limit, String unit) {
        return String.format("%s must be less or equal than %d %s", field, limit, unit);
    }
}
