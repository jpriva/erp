package com.jpriva.erpsp.shared.domain.model.token.access;

import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;

import java.time.Instant;
import java.util.Optional;

/**
 * Value object representing the claims extracted from a validated token.
 * </br>
 * tenantId and roleName are optional - they are present only after
 * the user has performed a token exchange to select their context.
 */
public record TokenClaims(
        UserId userId,
        Email email,
        TokenType type,
        TenantId tenantId,
        String roleName,
        Instant issuedAt,
        Instant expiresAt
) {
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isAccessToken() {
        return type == TokenType.ACCESS;
    }

    public boolean isRefreshToken() {
        return type == TokenType.REFRESH;
    }

    /**
     * Returns true if this token has a tenant context (after exchange).
     */
    public boolean hasTenantContext() {
        return tenantId != null && roleName != null;
    }

    public Optional<TenantId> getTenantId() {
        return Optional.ofNullable(tenantId);
    }

    public Optional<String> getRoleName() {
        return Optional.ofNullable(roleName);
    }
}
