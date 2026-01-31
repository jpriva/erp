package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for role assignments within a membership.
 * Denormalized snapshot containing role metadata and audit information.
 * Maps to the membership_roles table.
 */
@Entity
@Table(name = "membership_roles", schema = "auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MembershipRoleId.class)
public class MembershipRoleEntity {
    @Id
    @Column(name = "membership_id", columnDefinition = "UUID")
    private UUID membershipId;

    @Id
    @Column(name = "role_id", columnDefinition = "UUID")
    private UUID roleId;

    @Column(name = "role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "assigned_by", nullable = false, columnDefinition = "UUID")
    private UUID assignedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
