package com.jpriva.erpsp.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for transferring tenant ownership to another member.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferOwnershipRequest {
    @JsonProperty("new_owner_id")
    private UUID newOwnerId;
}
