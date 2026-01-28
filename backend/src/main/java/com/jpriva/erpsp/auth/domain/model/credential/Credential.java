package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.model.user.UserId;

import java.time.Instant;

/**
 * Base sealed class for all credential types.
 * Uses sealed class hierarchy for type safety with pattern matching.
 */
public abstract sealed class Credential
        permits PasswordCredential, OpenIdCredential, BiometricCredential {

    private final CredentialId credentialId;
    private final UserId userId;
    private final CredentialType type;
    private CredentialStatus status;
    private final Instant createdAt;
    private Instant lastUsedAt;

    protected Credential(
            CredentialId credentialId,
            UserId userId,
            CredentialType type,
            CredentialStatus status,
            Instant createdAt,
            Instant lastUsedAt
    ) {
        this.credentialId = credentialId;
        this.userId = userId;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
    }

    public CredentialId getCredentialId() {
        return credentialId;
    }

    public UserId getUserId() {
        return userId;
    }

    public CredentialType getType() {
        return type;
    }

    public CredentialStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public boolean isActive() {
        return status == CredentialStatus.ACTIVE;
    }

    public void disable() {
        this.status = CredentialStatus.DISABLED;
    }

    public void markAsCompromised() {
        this.status = CredentialStatus.COMPROMISED;
    }

    public void markAsExpired() {
        this.status = CredentialStatus.EXPIRED;
    }

    public void activate() {
        this.status = CredentialStatus.ACTIVE;
    }

    public void recordUsage() {
        this.lastUsedAt = Instant.now();
    }

    protected void setStatus(CredentialStatus status) {
        this.status = status;
    }
}
