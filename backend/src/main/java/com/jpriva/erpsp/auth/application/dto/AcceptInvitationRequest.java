package com.jpriva.erpsp.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for accepting an invitation.
 * The token is typically in the URL path, not in the body.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptInvitationRequest {
    // Token can be passed here if needed, but typically comes from URL path
    @JsonProperty("token")
    private String token;
}
