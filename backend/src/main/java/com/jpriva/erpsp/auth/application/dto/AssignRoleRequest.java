package com.jpriva.erpsp.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for assigning a role to a member.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignRoleRequest {
    @JsonProperty("role_id")
    private UUID roleId;
}
