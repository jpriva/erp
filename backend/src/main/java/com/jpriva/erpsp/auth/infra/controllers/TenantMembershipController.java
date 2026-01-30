package com.jpriva.erpsp.auth.infra.controllers;

import com.jpriva.erpsp.auth.application.dto.AssignRoleRequest;
import com.jpriva.erpsp.auth.application.dto.RemoveMemberRequest;
import com.jpriva.erpsp.auth.application.dto.TenantMembershipDto;
import com.jpriva.erpsp.auth.application.services.TenantMembershipApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for tenant membership operations.
 * Endpoints for managing user memberships in tenants.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/members")
public class TenantMembershipController {
    private final TenantMembershipApplicationService applicationService;

    public TenantMembershipController(TenantMembershipApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * GET /api/v1/tenants/{tenantId}/members
     * Get all members of a tenant.
     */
    @GetMapping
    public ResponseEntity<List<TenantMembershipDto>> getTenantMembers(
            @PathVariable UUID tenantId
    ) {
        log.info("GET /tenants/{}/members", tenantId);
        List<TenantMembershipDto> members = applicationService.getTenantMembers(tenantId);
        return ResponseEntity.ok(members);
    }

    /**
     * GET /api/v1/tenants/{tenantId}/members/active
     * Get all active members of a tenant.
     */
    @GetMapping("/active")
    public ResponseEntity<List<TenantMembershipDto>> getActiveTenantMembers(
            @PathVariable UUID tenantId
    ) {
        log.info("GET /tenants/{}/members/active", tenantId);
        List<TenantMembershipDto> members = applicationService.getActiveTenantMembers(tenantId);
        return ResponseEntity.ok(members);
    }

    /**
     * GET /api/v1/tenants/{tenantId}/members/{userId}
     * Get a specific member's details.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<TenantMembershipDto> getMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId
    ) {
        log.info("GET /tenants/{}/members/{}", tenantId, userId);
        TenantMembershipDto membership = applicationService.getMembership(userId, tenantId);
        return ResponseEntity.ok(membership);
    }

    /**
     * POST /api/v1/tenants/{tenantId}/members/{userId}/roles
     * Assign a role to a member.
     */
    @PostMapping("/{userId}/roles")
    public ResponseEntity<TenantMembershipDto> assignRole(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId,
            @RequestBody AssignRoleRequest request,
            @RequestHeader(value = "X-Current-User-ID", required = false) UUID assignedBy
    ) {
        log.info("POST /tenants/{}/members/{}/roles", tenantId, userId);

        if (assignedBy == null) {
            assignedBy = userId; // Default to the current user if not provided
        }

        TenantMembershipDto membership = applicationService.assignRole(
                userId, tenantId, request, assignedBy
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(membership);
    }

    /**
     * DELETE /api/v1/tenants/{tenantId}/members/{userId}/roles/{roleId}
     * Revoke a role from a member.
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<TenantMembershipDto> revokeRole(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId,
            @PathVariable UUID roleId
    ) {
        log.info("DELETE /tenants/{}/members/{}/roles/{}", tenantId, userId, roleId);

        TenantMembershipDto membership = applicationService.revokeRole(userId, tenantId, roleId);
        return ResponseEntity.ok(membership);
    }

    /**
     * PATCH /api/v1/tenants/{tenantId}/members/{userId}/suspend
     * Suspend a member.
     */
    @PatchMapping("/{userId}/suspend")
    public ResponseEntity<Void> suspendMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId
    ) {
        log.info("PATCH /tenants/{}/members/{}/suspend", tenantId, userId);

        applicationService.suspendMember(userId, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/v1/tenants/{tenantId}/members/{userId}/activate
     * Activate a suspended member.
     */
    @PatchMapping("/{userId}/activate")
    public ResponseEntity<Void> activateMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId
    ) {
        log.info("PATCH /tenants/{}/members/{}/activate", tenantId, userId);

        applicationService.activateMember(userId, tenantId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/v1/tenants/{tenantId}/members/{userId}
     * Remove a member from a tenant.
     */
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable UUID tenantId,
            @PathVariable UUID userId,
            @RequestBody(required = false) RemoveMemberRequest request,
            @RequestHeader(value = "X-Current-User-ID", required = false) UUID removedBy
    ) {
        log.info("DELETE /tenants/{}/members/{}", tenantId, userId);

        if (removedBy == null) {
            removedBy = userId; // Default to the current user if not provided
        }

        applicationService.removeMember(userId, tenantId, request, removedBy);
        return ResponseEntity.noContent().build();
    }
}
