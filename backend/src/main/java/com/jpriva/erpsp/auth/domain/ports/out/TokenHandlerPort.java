package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.token.TokenClaims;
import com.jpriva.erpsp.auth.domain.model.token.TokenPair;
import com.jpriva.erpsp.auth.domain.model.user.User;

import java.util.Optional;

/**
 * Port for JWT token operations.
 * Follows the same pattern as PasswordHasherPort for infrastructure abstraction.
 */
public interface TokenHandlerPort {

    /**
     * Generates a pair of access and refresh tokens for the given user.
     * The access token will NOT have tenant/role context.
     *
     * @param user The authenticated user
     * @return TokenPair containing access and refresh tokens
     */
    TokenPair generateTokens(User user);

    /**
     * Generates only an access token without tenant context (used during refresh flow).
     *
     * @param user The authenticated user
     * @return The access token string
     */
    String generateAccessToken(User user);

    /**
     * Generates an access token WITH tenant and role context.
     * Used after the user selects a tenant and role (exchange token flow).
     *
     * @param user     The authenticated user
     * @param tenantId The selected tenant
     * @param roleName The selected role name
     * @return The access token string with tenant context
     */
    String generateAccessTokenWithContext(User user, TenantId tenantId, String roleName);

    /**
     * Validates a token and extracts its claims.
     *
     * @param token The JWT token string
     * @return Optional containing TokenClaims if valid, empty if invalid/expired
     */
    Optional<TokenClaims> validateToken(String token);

    /**
     * Validates specifically a refresh token.
     * More strict validation for security.
     *
     * @param refreshToken The refresh token string
     * @return Optional containing TokenClaims if valid refresh token
     */
    Optional<TokenClaims> validateRefreshToken(String refreshToken);
}
