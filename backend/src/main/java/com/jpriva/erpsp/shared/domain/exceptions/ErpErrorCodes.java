package com.jpriva.erpsp.shared.domain.exceptions;

public enum ErpErrorCodes implements ErrorCode{

    IMPLEMENTATION_ERROR("IMPLEMENTATION_ERROR", "Implementation error", 500),
    VALIDATION_ERROR("VALIDATION_ERROR", "Validation error", 400),
    PERSISTENCE_COMPROMISED("PERSISTENCE_COMPROMISED", "Persistence doesn't match domain", 500);

    public final static String SHARED_MODULE = "SHARED";

    ErpErrorCodes(String code, String message, int status){
        this.code = code;
        this.message = message;
        this.status = status;
    }

    private final String code;
    private final String message;
    private final int status;

    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
    public int getStatus() {
        return status;
    }
}
