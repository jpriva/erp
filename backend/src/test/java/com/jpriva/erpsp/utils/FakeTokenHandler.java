package com.jpriva.erpsp.utils;

import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenClaims;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenPair;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenType;
import com.jpriva.erpsp.shared.domain.ports.out.TokenHandlerPort;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeTokenHandler implements TokenHandlerPort {

    private final Map<String, TokenClaims> validTokens = new HashMap<>();
    private int tokenCounter = 0;

    @Override
    public TokenPair generateTokens(UserId userId, Email email) {
        String accessToken = generateAccessToken(userId, email);
        String refreshToken = generateRefreshTokenInternal(userId);
        return new TokenPair(accessToken, refreshToken, 900, 604800);
    }

    @Override
    public String generateAccessToken(UserId userId, Email email) {
        return buildAccessToken(userId, email, null, null);
    }

    @Override
    public String generateAccessTokenWithContext(UserId userId, Email email, TenantId tenantId, String roleName) {
        return buildAccessToken(userId, email, tenantId, roleName);
    }

    private String buildAccessToken(UserId userId, Email email, TenantId tenantId, String roleName) {
        String token = "access_token_" + (++tokenCounter);
        TokenClaims claims = new TokenClaims(
                userId,
                email,
                TokenType.ACCESS,
                tenantId,
                roleName,
                Instant.now(),
                Instant.now().plusSeconds(900)
        );
        validTokens.put(token, claims);
        return token;
    }

    private String generateRefreshTokenInternal(UserId userId) {
        String token = "refresh_token_" + (++tokenCounter);
        TokenClaims claims = new TokenClaims(
                userId,
                null,
                TokenType.REFRESH,
                null,
                null,
                Instant.now(),
                Instant.now().plusSeconds(604800)
        );
        validTokens.put(token, claims);
        return token;
    }

    @Override
    public Optional<TokenClaims> validateToken(String token) {
        return Optional.ofNullable(validTokens.get(token));
    }

    @Override
    public Optional<TokenClaims> validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken).filter(TokenClaims::isRefreshToken);
    }

    public void registerValidAccessToken(String token, UserId userId) {
        TokenClaims claims = new TokenClaims(
                userId,
                null,
                TokenType.ACCESS,
                null,
                null,
                Instant.now(),
                Instant.now().plusSeconds(900)
        );
        validTokens.put(token, claims);
    }

    public void registerValidRefreshToken(String token, UserId userId) {
        TokenClaims claims = new TokenClaims(
                userId,
                null,
                TokenType.REFRESH,
                null,
                null,
                Instant.now(),
                Instant.now().plusSeconds(604800)
        );
        validTokens.put(token, claims);
    }

    public void clear() {
        validTokens.clear();
        tokenCounter = 0;
    }
}
