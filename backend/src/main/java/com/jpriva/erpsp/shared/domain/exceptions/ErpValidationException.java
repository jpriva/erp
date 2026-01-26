package com.jpriva.erpsp.shared.domain.exceptions;

import com.jpriva.erpsp.shared.domain.model.ValidationError;

import java.util.List;
import java.util.Map;

public class ErpValidationException extends ErpException {

    private final ValidationError errors;

    public ErpValidationException(String module, ValidationError errors) {
        super(module, ErpErrorCodes.VALIDATION_ERROR);
        this.errors = errors;
    }

    public ErpValidationException(String module, ValidationError errors, String message) {
        super(module, ErpErrorCodes.VALIDATION_ERROR, message);
        this.errors = errors;
    }

    public ErpValidationException(String module, ValidationError errors, Throwable cause) {
        super(module, ErpErrorCodes.VALIDATION_ERROR, cause);
        this.errors = errors;
    }

    public ErpValidationException(String module, ValidationError errors, String message, Throwable cause) {
        super(module, ErpErrorCodes.VALIDATION_ERROR, message, cause);
        this.errors = errors;
    }

    public Map<String, List<String>> getPlainErrors() {
        return errors.errors();
    }

    public ValidationError getValidationErrors() {
        return errors;
    }
}
