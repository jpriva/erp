package com.jpriva.erpsp.shared.domain.model.token.access;

/**
 * Value object representing a pair of access and refresh tokens.
 */
public record TokenPair(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {
}
