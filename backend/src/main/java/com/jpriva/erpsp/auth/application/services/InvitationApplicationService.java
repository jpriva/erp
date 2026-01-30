package com.jpriva.erpsp.auth.application.services;

import com.jpriva.erpsp.auth.application.dto.InvitationDto;
import com.jpriva.erpsp.auth.application.dto.InviteUserRequest;
import com.jpriva.erpsp.auth.application.mappers.InvitationMapper;
import com.jpriva.erpsp.auth.domain.model.invitation.Invitation;
import com.jpriva.erpsp.auth.domain.model.invitation.InvitationToken;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.services.InvitationManager;
import com.jpriva.erpsp.shared.domain.model.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for invitation operations.
 * Orchestrates domain services and handles transactional boundaries.
 */
@Slf4j
@Service
public class InvitationApplicationService {
    private final InvitationManager invitationManager;
    private final InvitationMapper mapper;

    public InvitationApplicationService(
            InvitationManager invitationManager,
            InvitationMapper mapper
    ) {
        this.invitationManager = invitationManager;
        this.mapper = mapper;
    }

    /**
     * Invites a user to a tenant with specific roles.
     * Default validity: 7 days.
     */
    @Transactional
    public InvitationDto inviteUser(UUID tenantId, InviteUserRequest request, UUID invitedBy) {
        log.info("Inviting user {} to tenant {}", request.getEmail(), tenantId);

        Set<RoleId> roleIds = request.getRoleIds()
                .stream()
                .map(RoleId::new)
                .collect(Collectors.toSet());

        long validDays = request.getValidDays() != null ? request.getValidDays() : 7L;
        Duration validFor = Duration.ofDays(validDays);

        Invitation invitation = invitationManager.inviteUserToTenant(
                new TenantId(tenantId),
                new Email(request.getEmail()),
                roleIds,
                new UserId(invitedBy),
                validFor
        );

        return mapper.toDto(invitation);
    }

    /**
     * Gets a pending invitation for a specific email and tenant.
     */
    @Transactional(readOnly = true)
    public InvitationDto getPendingInvitation(UUID tenantId, String email) {
        log.info("Fetching pending invitation for {} to tenant {}", email, tenantId);

        return invitationManager
                .findPendingByTenantIdAndEmail(new TenantId(tenantId), new Email(email))
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Pending invitation not found"));
    }

    /**
     * Gets all pending invitations for a user's email.
     */
    @Transactional(readOnly = true)
    public List<InvitationDto> getPendingInvitationsForEmail(String email) {
        log.info("Fetching all pending invitations for email {}", email);

        return invitationManager
                .findPendingByEmail(new Email(email))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets all invitations sent by a user.
     */
    @Transactional(readOnly = true)
    public List<InvitationDto> getInvitationsSentBy(UUID userId) {
        log.info("Fetching all invitations sent by user {}", userId);

        return invitationManager
                .findByInvitedBy(new UserId(userId))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets all invitations for a tenant.
     */
    @Transactional(readOnly = true)
    public List<InvitationDto> getTenantInvitations(UUID tenantId) {
        log.info("Fetching all invitations for tenant {}", tenantId);

        return invitationManager
                .findByTenantId(new TenantId(tenantId))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Gets all pending invitations for a tenant.
     */
    @Transactional(readOnly = true)
    public List<InvitationDto> getPendingTenantInvitations(UUID tenantId) {
        log.info("Fetching all pending invitations for tenant {}", tenantId);

        return invitationManager
                .findPendingByTenantId(new TenantId(tenantId))
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Accepts an invitation and creates a membership for the user.
     * This is typically called after the user verifies the invitation token.
     */
    @Transactional
    public void acceptInvitation(String token, UUID userId) {
        log.info("Accepting invitation with token {} for user {}", token, userId);

        invitationManager.acceptInvitation(
                new InvitationToken(token),
                new UserId(userId)
        );
    }

    /**
     * Rejects an invitation.
     */
    @Transactional
    public void rejectInvitation(String token, UUID userId) {
        log.info("Rejecting invitation with token {} for user {}", token, userId);

        invitationManager.rejectInvitation(
                new InvitationToken(token),
                new UserId(userId)
        );
    }

    /**
     * Cancels a pending invitation.
     */
    @Transactional
    public void cancelInvitation(UUID invitationId, UUID cancelledBy) {
        log.info("Cancelling invitation {} by user {}", invitationId, cancelledBy);

        invitationManager.cancelInvitation(
                new com.jpriva.erpsp.auth.domain.model.invitation.InvitationId(invitationId),
                new UserId(cancelledBy)
        );
    }

    // Package-private method for internal operations
    @Transactional
    Invitation getInvitationByToken(String token) {
        return invitationManager.findByToken(new InvitationToken(token))
                .orElseThrow(() -> new RuntimeException("Invitation not found"));
    }

    // Package-private method for internal operations
    @Transactional(readOnly = true)
    List<Invitation> findByTenantId(TenantId tenantId) {
        return invitationManager.findByTenantId(tenantId);
    }

    @Transactional(readOnly = true)
    List<Invitation> findByEmail(Email email) {
        return invitationManager.findByEmail(email);
    }
}
