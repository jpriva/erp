package com.jpriva.erpsp.shared.domain.exceptions;

public interface ErrorCode {
    String getCode();
    String getMessage();
    int getStatus();

    default String getFullMessage() {
        return String.format("%s: %s", getCode(), getMessage());
    }
}
