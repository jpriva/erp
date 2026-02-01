package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity for tenants.
 * Maps to the tenants table.
 */
@Entity
@Table(name = "tenants", schema = "auth", indexes = {
        @Index(name = "idx_tenant_owner_id", columnList = "owner_id"),
        @Index(name = "idx_tenant_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantEntity {
    @Id
    @Column(name = "tenant_id", columnDefinition = "UUID")
    private UUID tenantId;

    @Column(name = "owner_id", nullable = false, columnDefinition = "UUID")
    private UUID ownerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

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
