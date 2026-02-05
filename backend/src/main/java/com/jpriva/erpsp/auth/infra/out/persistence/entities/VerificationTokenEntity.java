package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for users.
 * Maps to the users table.
 */
@Entity
@Table(name = "verification_tokens", schema = "auth", indexes = {
        @Index(name = "idx_verification_token_user", columnList = "user_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerificationTokenEntity {
    @Id
    @Column(name = "verification_token_id", columnDefinition = "UUID")
    private UUID verificationTokenId;

    @Column(name = "user_id", nullable = false, updatable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "expiry_date", nullable = false, updatable = false)
    private Instant expiryDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
