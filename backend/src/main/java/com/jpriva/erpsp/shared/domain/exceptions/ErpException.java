package com.jpriva.erpsp.shared.domain.exceptions;

public class ErpException extends RuntimeException {

    private final String module;
    private final ErrorCode code;

    public ErpException(String module, ErrorCode code) {
        super(code.getMessage());
        this.code = code;
        this.module = module;
    }

    public ErpException(String module, ErrorCode code, String message) {
        super(message);
        this.code = code;
        this.module = module;
    }

    public ErpException(String module, ErrorCode code, Throwable cause) {
        super(code.getMessage(), cause);
        this.code = code;
        this.module = module;
    }

    public ErpException(String module, ErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.module = module;
    }

    public String getModule() {
        return module;
    }

    public ErrorCode getCode(){
        return code;
    }

}
