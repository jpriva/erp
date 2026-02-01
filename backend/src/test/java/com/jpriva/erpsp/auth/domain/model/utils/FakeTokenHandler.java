package com.jpriva.erpsp.auth.domain.model.utils;

import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.token.TokenClaims;
import com.jpriva.erpsp.auth.domain.model.token.TokenPair;
import com.jpriva.erpsp.auth.domain.model.token.TokenType;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.TokenHandlerPort;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeTokenHandler implements TokenHandlerPort {

    private final Map<String, TokenClaims> validTokens = new HashMap<>();
    private int tokenCounter = 0;

    @Override
    public TokenPair generateTokens(User user) {
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshTokenInternal(user);
        return new TokenPair(accessToken, refreshToken, 900, 604800);
    }

    @Override
    public String generateAccessToken(User user) {
        return buildAccessToken(user, null, null);
    }

    @Override
    public String generateAccessTokenWithContext(User user, TenantId tenantId, String roleName) {
        return buildAccessToken(user, tenantId, roleName);
    }

    private String buildAccessToken(User user, TenantId tenantId, String roleName) {
        String token = "access_token_" + (++tokenCounter);
        TokenClaims claims = new TokenClaims(
                user.getUserId(),
                user.getEmail(),
                TokenType.ACCESS,
                tenantId,
                roleName,
                Instant.now(),
                Instant.now().plusSeconds(900)
        );
        validTokens.put(token, claims);
        return token;
    }

    private String generateRefreshTokenInternal(User user) {
        String token = "refresh_token_" + (++tokenCounter);
        TokenClaims claims = new TokenClaims(
                user.getUserId(),
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
