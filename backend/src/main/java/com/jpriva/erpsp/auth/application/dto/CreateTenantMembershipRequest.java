package com.jpriva.erpsp.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for adding a member to a tenant.
 * Used by invitation acceptance and manual member addition.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTenantMembershipRequest {
    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("tenant_id")
    private UUID tenantId;

    @JsonProperty("role_ids")
    private Set<UUID> roleIds;
}
