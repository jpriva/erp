package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public class ErpAuthValidationException extends ErpValidationException {

    public ErpAuthValidationException(ValidationError errors) {
        super(AuthErrorCode.AUTH_MODULE, errors);
    }

    public ErpAuthValidationException(ValidationError errors, Throwable cause) {
        super(AuthErrorCode.AUTH_MODULE, errors, cause);
    }

}
