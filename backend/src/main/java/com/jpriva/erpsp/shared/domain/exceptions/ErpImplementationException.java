package com.jpriva.erpsp.shared.domain.exceptions;

public class ErpImplementationException extends ErpException {
    public ErpImplementationException(Object... args) {
        super(ErpErrorCodes.SHARED_MODULE, ErpErrorCodes.IMPLEMENTATION_ERROR, args);
    }

    public ErpImplementationException(Throwable cause, Object... args) {
        super(ErpErrorCodes.SHARED_MODULE, ErpErrorCodes.IMPLEMENTATION_ERROR, cause, args);
    }
}
