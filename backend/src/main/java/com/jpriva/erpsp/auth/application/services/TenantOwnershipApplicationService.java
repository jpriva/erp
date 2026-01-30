package com.jpriva.erpsp.auth.application.services;

import com.jpriva.erpsp.auth.application.dto.TransferOwnershipRequest;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.services.TenantOwnershipManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Application service for tenant ownership operations.
 * Handles ownership transfer with proper authorization and transactional boundaries.
 */
@Slf4j
@Service
public class TenantOwnershipApplicationService {
    private final TenantOwnershipManager ownershipManager;

    public TenantOwnershipApplicationService(TenantOwnershipManager ownershipManager) {
        this.ownershipManager = ownershipManager;
    }

    /**
     * Transfers ownership of a tenant to another member.
     *
     * Authorization: Only the current owner can transfer ownership.
     * Validation: New owner must be an active member with ADMIN role.
     *
     * @param tenantId the tenant whose ownership is being transferred
     * @param currentOwnerId the current owner performing the transfer
     * @param request contains the new owner ID
     */
    @Transactional
    public void transferOwnership(
            UUID tenantId,
            UUID currentOwnerId,
            TransferOwnershipRequest request
    ) {
        log.info("Transferring ownership of tenant {} from {} to {}",
                tenantId, currentOwnerId, request.getNewOwnerId());

        ownershipManager.transferOwnership(
                new TenantId(tenantId),
                new UserId(currentOwnerId),
                new UserId(request.getNewOwnerId())
        );

        log.info("Ownership of tenant {} successfully transferred to {}",
                tenantId, request.getNewOwnerId());
    }
}
