package com.jpriva.erpsp.shared.domain.exceptions;

public class ErpPersistenceCompromisedException extends ErpException {
    public ErpPersistenceCompromisedException(String module, Throwable cause) {
        super(module, ErpErrorCodes.PERSISTENCE_COMPROMISED, cause);
    }
}
