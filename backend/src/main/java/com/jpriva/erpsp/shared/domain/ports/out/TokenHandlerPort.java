package com.jpriva.erpsp.shared.domain.ports.out;

import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenClaims;
import com.jpriva.erpsp.shared.domain.model.token.access.TokenPair;

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
     * @param userId The authenticated user's Id
     * @param email  The user's email
     * @return TokenPair containing access and refresh tokens
     */
    TokenPair generateTokens(UserId userId, Email email);

    /**
     * Generates only an access token without tenant context (used during refresh flow).
     *
     * @param userId The authenticated user's Id
     * @param email  The user's email
     * @return The access token string
     */
    String generateAccessToken(UserId userId, Email email);

    /**
     * Generates an access token WITH tenant and role context.
     * Used after the user selects a tenant and role (exchange token flow).
     *
     * @param userId   The authenticated user's Id
     * @param email    The user's email
     * @param tenantId The selected tenant
     * @param roleName The selected role name
     * @return The access token string with tenant context
     */
    String generateAccessTokenWithContext(UserId userId, Email email, TenantId tenantId, String roleName);

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
