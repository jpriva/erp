package com.jpriva.erpsp.auth.domain.exceptions;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpException;

public class ErpAuthException extends ErpException {
    public ErpAuthException(AuthErrorCode code, Object... args) {
        super(AuthErrorCode.AUTH_MODULE, code, args);
    }

    public ErpAuthException(AuthErrorCode code, Throwable cause, Object... args) {
        super(AuthErrorCode.AUTH_MODULE, code, cause, args);
    }
}
