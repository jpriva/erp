package com.jpriva.erpsp.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Request DTO for inviting a user to a tenant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InviteUserRequest {
    @JsonProperty("email")
    private String email;

    @JsonProperty("role_ids")
    private Set<UUID> roleIds;

    @JsonProperty("valid_days")
    private Long validDays;
}
