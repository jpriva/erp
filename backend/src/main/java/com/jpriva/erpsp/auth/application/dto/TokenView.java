package com.jpriva.erpsp.auth.application.dto;

public record TokenView(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken,
        String scope
) {
}
