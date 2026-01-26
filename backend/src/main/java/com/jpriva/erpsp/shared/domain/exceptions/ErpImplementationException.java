package com.jpriva.erpsp.shared.domain.exceptions;

public class ErpImplementationException extends ErpException {
    public ErpImplementationException(String message) {
        super(ErpErrorCodes.SHARED_MODULE, ErpErrorCodes.IMPLEMENTATION_ERROR, message);
    }
    public ErpImplementationException(String message, Throwable cause) {
        super(ErpErrorCodes.SHARED_MODULE, ErpErrorCodes.IMPLEMENTATION_ERROR, message, cause);
    }
}
