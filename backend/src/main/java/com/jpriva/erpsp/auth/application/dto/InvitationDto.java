package com.jpriva.erpsp.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for representing an invitation in API responses.
 * Read-only representation for viewing invitation details.
 */
@Data
@Builder
public class InvitationDto {
    @JsonProperty("invitation_id")
    private UUID invitationId;

    @JsonProperty("tenant_id")
    private UUID tenantId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("status")
    private String status;

    @JsonProperty("token")
    private String token;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("expires_at")
    private Instant expiresAt;

    @JsonProperty("role_ids")
    private Set<UUID> roleIds;

    @JsonProperty("is_valid")
    private boolean isValid;

    @JsonProperty("is_expired")
    private boolean isExpired;
}
