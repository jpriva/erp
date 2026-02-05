package com.jpriva.erpsp.shared.domain.model.token.verification;

import com.jpriva.erpsp.shared.domain.model.UserId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class VerificationToken {
    private final UUID id;
    private final UserId userId;
    private final Instant expiryDate;
    private final Instant createdAt;

    private VerificationToken(UUID id, UserId userId, Instant expiryDate, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
    }

    public static VerificationToken create(UserId userId) {
        return new VerificationToken(
                UUID.randomUUID(),
                userId,
                Instant.now().plus(20, ChronoUnit.MINUTES),
                Instant.now()
        );
    }

    public static VerificationToken fromDatabase(UUID id, UUID userId, Instant expiryDate, Instant createdAt) {
        return new VerificationToken(
                id,
                new UserId(userId),
                expiryDate,
                createdAt
        );
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiryDate);
    }

    public UUID getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
