package com.jpriva.erpsp.shared.domain.exceptions;

public interface ValidationErrorCode {
    String getField();

    String getCode();

    String getMessage();

    default String getFullMessage() {
        return String.format("%s: %s", getCode(), getMessage());
    }
}
