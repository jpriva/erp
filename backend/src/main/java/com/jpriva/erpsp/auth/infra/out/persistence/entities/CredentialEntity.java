package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

/**
 * Base JPA entity for credentials using Single Table Inheritance.
 * Maps to the credentials table with a discriminator column for type differentiation.
 * </br>
 * Supports:
 * - PASSWORD: Local password-based authentication (has password_hash)
 * - OPENID: OpenID Connect / OAuth2 authentication (has provider and subject)
 */
@Entity
@Table(name = "credentials", schema = "auth",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_credential_user_type", columnNames = {"user_id", "type", "provider", "subject"})
        },
        indexes = {
                @Index(name = "idx_credential_user_id", columnList = "user_id"),
                @Index(name = "idx_credential_status", columnList = "status"),
                @Index(name = "idx_credential_type", columnList = "type")
        })

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 20)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class CredentialEntity {
    @Id
    @Column(name = "credential_id", columnDefinition = "UUID")
    private UUID credentialId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "type", insertable = false, updatable = false)
    private String type;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        // NotNecessary
    }
}
