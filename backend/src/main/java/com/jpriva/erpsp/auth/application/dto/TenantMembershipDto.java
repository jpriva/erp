package com.jpriva.erpsp.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for representing a tenant membership in API responses.
 * Read-only representation of a user's membership in a tenant.
 */
@Data
@Builder
public class TenantMembershipDto {
    @JsonProperty("membership_id")
    private UUID membershipId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("tenant_id")
    private UUID tenantId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("joined_at")
    private Instant joinedAt;

    @JsonProperty("roles")
    private Set<MembershipRoleDto> roles;

    @Data
    @Builder
    public static class MembershipRoleDto {
        @JsonProperty("role_id")
        private UUID roleId;

        @JsonProperty("role_name")
        private String roleName;

        @JsonProperty("assigned_at")
        private Instant assignedAt;

        @JsonProperty("assigned_by")
        private UUID assignedBy;
    }
}
