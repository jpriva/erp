package com.jpriva.erpsp.shared.domain.exceptions;

public class ErpException extends RuntimeException {

    private final Object[] args;
    private final String module;
    private final ErrorCode code;

    public ErpException(String module, ErrorCode code, Object... args) {
        super(code.getMessage());
        this.code = code;
        this.module = module;
        this.args = args;
    }

    public ErpException(String module, ErrorCode code, Throwable cause, Object... args) {
        super(code.getMessage(), cause);
        this.code = code;
        this.module = module;
        this.args = args;
    }

    public String getModule() {
        return module;
    }

    public ErrorCode getCode() {
        return code;
    }

    public Object[] getArgs() {
        return args;
    }

}
