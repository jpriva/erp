package com.jpriva.erpsp.auth.infra.controllers;

import com.jpriva.erpsp.auth.application.dto.AcceptInvitationRequest;
import com.jpriva.erpsp.auth.application.dto.InvitationDto;
import com.jpriva.erpsp.auth.application.dto.InviteUserRequest;
import com.jpriva.erpsp.auth.application.services.InvitationApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for invitation operations.
 * Endpoints for managing tenant invitations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/invitations")
public class InvitationController {
    private final InvitationApplicationService applicationService;

    public InvitationController(InvitationApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * POST /api/v1/tenants/{tenantId}/invitations
     * Invite a user to a tenant.
     */
    @PostMapping("/tenants/{tenantId}")
    public ResponseEntity<InvitationDto> inviteUser(
            @PathVariable UUID tenantId,
            @RequestBody InviteUserRequest request,
            @RequestHeader(value = "X-Current-User-ID", required = false) UUID invitedBy
    ) {
        log.info("POST /invitations/tenants/{}", tenantId);

        if (invitedBy == null) {
            return ResponseEntity.badRequest().build();
        }

        InvitationDto invitation = applicationService.inviteUser(tenantId, request, invitedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(invitation);
    }

    /**
     * GET /api/v1/invitations/tenants/{tenantId}
     * Get all invitations for a tenant.
     */
    @GetMapping("/tenants/{tenantId}")
    public ResponseEntity<List<InvitationDto>> getTenantInvitations(
            @PathVariable UUID tenantId
    ) {
        log.info("GET /invitations/tenants/{}", tenantId);
        List<InvitationDto> invitations = applicationService.getTenantInvitations(tenantId);
        return ResponseEntity.ok(invitations);
    }

    /**
     * GET /api/v1/invitations/tenants/{tenantId}/pending
     * Get all pending invitations for a tenant.
     */
    @GetMapping("/tenants/{tenantId}/pending")
    public ResponseEntity<List<InvitationDto>> getPendingTenantInvitations(
            @PathVariable UUID tenantId
    ) {
        log.info("GET /invitations/tenants/{}/pending", tenantId);
        List<InvitationDto> invitations = applicationService.getPendingTenantInvitations(tenantId);
        return ResponseEntity.ok(invitations);
    }

    /**
     * GET /api/v1/invitations/sent-by/{userId}
     * Get all invitations sent by a user.
     */
    @GetMapping("/sent-by/{userId}")
    public ResponseEntity<List<InvitationDto>> getInvitationsSentBy(
            @PathVariable UUID userId
    ) {
        log.info("GET /invitations/sent-by/{}", userId);
        List<InvitationDto> invitations = applicationService.getInvitationsSentBy(userId);
        return ResponseEntity.ok(invitations);
    }

    /**
     * GET /api/v1/invitations/pending?email={email}
     * Get all pending invitations for an email.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<InvitationDto>> getPendingInvitationsForEmail(
            @RequestParam String email
    ) {
        log.info("GET /invitations/pending?email={}", email);
        List<InvitationDto> invitations = applicationService.getPendingInvitationsForEmail(email);
        return ResponseEntity.ok(invitations);
    }

    /**
     * GET /api/v1/invitations/tenants/{tenantId}/pending-by-email
     * Get pending invitation for a specific email and tenant.
     */
    @GetMapping("/tenants/{tenantId}/pending-by-email")
    public ResponseEntity<InvitationDto> getPendingInvitation(
            @PathVariable UUID tenantId,
            @RequestParam String email
    ) {
        log.info("GET /invitations/tenants/{}/pending-by-email?email={}", tenantId, email);
        InvitationDto invitation = applicationService.getPendingInvitation(tenantId, email);
        return ResponseEntity.ok(invitation);
    }

    /**
     * POST /api/v1/invitations/{token}/accept
     * Accept an invitation.
     */
    @PostMapping("/{token}/accept")
    public ResponseEntity<Void> acceptInvitation(
            @PathVariable String token,
            @RequestHeader(value = "X-Current-User-ID", required = false) UUID userId
    ) {
        log.info("POST /invitations/{}/accept", token);

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        applicationService.acceptInvitation(token, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/invitations/{token}/reject
     * Reject an invitation.
     */
    @PostMapping("/{token}/reject")
    public ResponseEntity<Void> rejectInvitation(
            @PathVariable String token,
            @RequestHeader(value = "X-Current-User-ID", required = false) UUID userId
    ) {
        log.info("POST /invitations/{}/reject", token);

        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }

        applicationService.rejectInvitation(token, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /api/v1/invitations/{invitationId}
     * Cancel a pending invitation.
     */
    @DeleteMapping("/{invitationId}")
    public ResponseEntity<Void> cancelInvitation(
            @PathVariable UUID invitationId,
            @RequestHeader(value = "X-Current-User-ID", required = false) UUID cancelledBy
    ) {
        log.info("DELETE /invitations/{}", invitationId);

        if (cancelledBy == null) {
            return ResponseEntity.badRequest().build();
        }

        applicationService.cancelInvitation(invitationId, cancelledBy);
        return ResponseEntity.noContent().build();
    }
}
