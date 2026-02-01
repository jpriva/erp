package com.jpriva.erpsp.auth.application.dto;

public record LocalLoginCommand(
        String email,
        String password
) {
}
