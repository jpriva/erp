package com.jpriva.erpsp.auth.infra.in.rest.dto;

import com.jpriva.erpsp.auth.application.dto.TokenView;

public record TokenResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn,
        String refreshToken,
        String scope
) {
    public static TokenResponseDto from(TokenView tokenView) {
        return new TokenResponseDto(
                tokenView.accessToken(),
                tokenView.tokenType(),
                tokenView.expiresIn(),
                tokenView.refreshToken(),
                tokenView.scope()
        );
    }
}
