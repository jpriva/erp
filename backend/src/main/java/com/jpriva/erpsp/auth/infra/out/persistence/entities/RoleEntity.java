package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for roles.
 * Maps to the roles table.
 * <p>
 * NOTE: The members of a role are stored in the membership_roles table,
 * not directly in this entity. They are loaded separately when needed.
 */
@Entity
@Table(name = "roles", schema = "auth", indexes = {
        @Index(name = "idx_role_tenant_id", columnList = "tenant_id")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "name"}, name = "uk_role_tenant_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
    @Id
    @Column(name = "role_id", columnDefinition = "UUID")
    private UUID roleId;

    @Column(name = "tenant_id", nullable = false, columnDefinition = "UUID")
    private UUID tenantId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

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
