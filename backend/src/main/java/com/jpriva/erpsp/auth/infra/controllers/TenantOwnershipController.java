package com.jpriva.erpsp.auth.infra.controllers;

import com.jpriva.erpsp.auth.application.dto.TransferOwnershipRequest;
import com.jpriva.erpsp.auth.application.services.TenantOwnershipApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for tenant ownership operations.
 * Endpoints for managing tenant ownership transfer.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/ownership")
public class TenantOwnershipController {
    private final TenantOwnershipApplicationService applicationService;

    public TenantOwnershipController(TenantOwnershipApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * POST /api/v1/tenants/{tenantId}/ownership/transfer
     * Transfer ownership of a tenant to another member.
     *
     * Authorization: Only the current owner can perform this operation.
     * Validation: New owner must be an active member with ADMIN role.
     */
    @PostMapping("/transfer")
    public ResponseEntity<Void> transferOwnership(
            @PathVariable UUID tenantId,
            @RequestBody TransferOwnershipRequest request,
            @RequestHeader(value = "X-Current-User-ID", required = false) UUID currentOwnerId
    ) {
        log.info("POST /tenants/{}/ownership/transfer", tenantId);

        if (currentOwnerId == null) {
            return ResponseEntity.badRequest().build();
        }

        applicationService.transferOwnership(tenantId, currentOwnerId, request);
        return ResponseEntity.noContent().build();
    }
}
