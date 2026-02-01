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
 * JPA entity for tenant memberships.
 * Maps to the tenant_memberships table.
 */
@Entity
@Table(name = "tenant_memberships", schema = "auth", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "tenant_id"}, name = "uk_user_tenant")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantMembershipEntity {
    @Id
    @Column(name = "membership_id", columnDefinition = "UUID")
    private UUID membershipId;

    @Column(name = "user_id", nullable = false, columnDefinition = "UUID")
    private UUID userId;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "UUID")
    private UUID tenantId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    @Column(name = "invited_by", nullable = false, columnDefinition = "UUID")
    private UUID invitedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    @JoinColumn(name = "membership_id")
    @Builder.Default
    private Set<MembershipRoleEntity> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
