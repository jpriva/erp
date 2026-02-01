package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for roles assigned by an invitation.
 * Maps to the invitation_roles table.
 */
@Entity
@Table(name = "invitation_roles", schema = "auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(InvitationRoleId.class)
public class InvitationRoleEntity {
    @Id
    @Column(name = "invitation_id", columnDefinition = "UUID")
    private UUID invitationId;

    @Id
    @Column(name = "role_id", columnDefinition = "UUID")
    private UUID roleId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
