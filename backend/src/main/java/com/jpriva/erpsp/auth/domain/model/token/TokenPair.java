package com.jpriva.erpsp.auth.domain.model.token;

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
