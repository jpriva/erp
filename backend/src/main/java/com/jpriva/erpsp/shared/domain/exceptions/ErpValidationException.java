package com.jpriva.erpsp.shared.domain.exceptions;

import com.jpriva.erpsp.shared.domain.model.ValidationError;

public class ErpValidationException extends ErpException {

    private final ValidationError errors;

    public ErpValidationException(String module, ValidationError errors) {
        super(module, ErpErrorCodes.VALIDATION_ERROR);
        this.errors = errors;
    }

    public ErpValidationException(String module, ValidationError errors, Throwable cause) {
        super(module, ErpErrorCodes.VALIDATION_ERROR, cause);
        this.errors = errors;
    }

    public ValidationError getValidationErrors() {
        return errors;
    }
}
