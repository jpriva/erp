package com.jpriva.erpsp.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for removing a member from a tenant.
 * Can optionally include a reason for audit purposes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveMemberRequest {
    @JsonProperty("reason")
    private String reason;
}
