package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * JPA entity for invitations.
 * Maps to the invitations table.
 */
@Entity
@Table(name = "invitations", schema = "auth", indexes = {
        @Index(name = "idx_inv_token", columnList = "token", unique = true),
        @Index(name = "idx_inv_email", columnList = "email"),
        @Index(name = "idx_inv_tenant_status", columnList = "tenant_id, status"),
        @Index(name = "idx_inv_created_at", columnList = "created_at"),
        @Index(name = "idx_inv_expires_at", columnList = "expires_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationEntity {
    @Id
    @Column(name = "invitation_id", columnDefinition = "UUID")
    private UUID invitationId;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "UUID")
    private UUID tenantId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "invited_by", nullable = false, columnDefinition = "UUID")
    private UUID invitedBy;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "token", nullable = false, unique = true, length = 128)
    private String token;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "invitation_id")
    @Builder.Default
    private Set<InvitationRoleEntity> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
