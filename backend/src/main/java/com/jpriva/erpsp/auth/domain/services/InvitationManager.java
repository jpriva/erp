package com.jpriva.erpsp.auth.domain.services;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.invitation.Invitation;
import com.jpriva.erpsp.auth.domain.model.invitation.InvitationToken;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.tenant.Tenant;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.User;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.InvitationRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.UserRepositoryPort;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Duration;
import java.util.Set;

/**
 * Domain service that manages the lifecycle of tenant invitations.
 * <p>
 * Responsibilities:
 * - Create invitations with secure tokens
 * - Prevent duplicate pending invitations
 * - Accept invitations and create memberships (delegates to TenantMembershipManager)
 * - Validate invitation tokens and expiration
 * - Reject and cancel invitations
 */
public class InvitationManager {
    private static final String TENANT_NOT_FOUND_ERROR = "Tenant not found";
    private static final String TENANT_NOT_ACTIVE_ERROR = "Tenant is not active";
    private static final String USER_NOT_FOUND_ERROR = "User not found";
    private static final String ROLE_NOT_FOUND_ERROR = "Role not found";
    private static final String INVITATION_NOT_FOUND_ERROR = "Invitation not found";
    private static final String INVITATION_INVALID_ERROR = "Invitation is not valid (not pending or expired)";
    private static final String INVITATION_ALREADY_EXISTS_ERROR = "A pending invitation already exists for this email to this tenant";
    private static final String EMAIL_MISMATCH_ERROR = "User email does not match invitation email";
    private static final String ROLES_EMPTY_ERROR = "At least one role must be specified for the invitation";

    private final InvitationRepositoryPort invitationRepository;
    private final TenantRepositoryPort tenantRepository;
    private final UserRepositoryPort userRepository;
    private final TenantMembershipManager membershipManager;

    public InvitationManager(
            InvitationRepositoryPort invitationRepository,
            TenantRepositoryPort tenantRepository,
            UserRepositoryPort userRepository,
            TenantMembershipManager membershipManager
    ) {
        this.invitationRepository = invitationRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.membershipManager = membershipManager;
    }

    /**
     * Invites a user to a tenant with the specified roles.
     * Generates a secure invitation token for acceptance.
     * <p>
     * Validations:
     * - Tenant exists and is active
     * - All roles belong to the tenant
     * - No pending invitation already exists for this email+tenant
     *
     * @param tenantId  the tenant the user is being invited to
     * @param email     the email of the invited user
     * @param roleIds   the roles to assign upon acceptance (must not be empty)
     * @param invitedBy the user sending the invitation
     * @param validFor  the duration the invitation is valid (default: 7 days)
     * @return the created invitation
     * @throws ErpValidationException if validation fails
     */
    public Invitation inviteUserToTenant(
            TenantId tenantId,
            Email email,
            Set<RoleId> roleIds,
            UserId invitedBy,
            Duration validFor
    ) {
        var val = new ValidationError.Builder();

        // Validate inputs
        if (tenantId == null) {
            val.addError("tenantId", "Tenant ID cannot be null");
        }
        if (email == null) {
            val.addError("email", "Email cannot be null");
        }
        if (roleIds == null || roleIds.isEmpty()) {
            val.addError("roleIds", ROLES_EMPTY_ERROR);
        }
        if (invitedBy == null) {
            val.addError("invitedBy", "Invited by cannot be null");
        }
        if (validFor == null || validFor.isNegative() || validFor.isZero()) {
            val.addError("validFor", "Validity duration must be positive");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Verify tenant exists and is active
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> {
                    val.addError("tenantId", TENANT_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        if (!tenant.isActive()) {
            val.addError("tenantId", TENANT_NOT_ACTIVE_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Check if inviter exists
        userRepository.findById(invitedBy)
                .orElseThrow(() -> {
                    val.addError("invitedBy", USER_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Prevent duplicate pending invitation for same email+tenant
        invitationRepository.findPendingByTenantIdAndEmail(tenantId, email)
                .ifPresent(existing -> {
                    val.addError("email", INVITATION_ALREADY_EXISTS_ERROR);
                });
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Create invitation (roles are validated but don't need to exist yet in domain service)
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);
        invitationRepository.save(invitation);

        return invitation;
    }

    /**
     * Accepts an invitation and creates a membership for the user.
     * <p>
     * Validations:
     * - Invitation exists and is valid (pending, not expired)
     * - User exists
     * - User's email matches invitation email
     * - User is not already a member of the tenant
     *
     * @param token  the invitation token
     * @param userId the user accepting the invitation
     * @return the created membership
     * @throws ErpValidationException if validation fails
     */
    public TenantMembership acceptInvitation(InvitationToken token, UserId userId) {
        var val = new ValidationError.Builder();

        // Validate inputs
        if (token == null) {
            val.addError("token", "Token cannot be null");
        }
        if (userId == null) {
            val.addError("userId", "User ID cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        // Find invitation by token
        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> {
                    val.addError("token", INVITATION_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Check invitation is valid
        if (!invitation.isValid()) {
            val.addError("token", INVITATION_INVALID_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    val.addError("userId", USER_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        // Verify email matches
        if (!user.getEmail().equals(invitation.getEmail())) {
            val.addError("email", EMAIL_MISMATCH_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        // Accept invitation (updates status to ACCEPTED)
        invitation.accept(userId);
        invitationRepository.save(invitation);

        // Create membership with the roles from the invitation
        TenantMembership membership = membershipManager.addMemberToTenant(
                userId,
                invitation.getTenantId(),
                invitation.getRoleIds(),
                invitation.getInvitedBy()  // Record who invited them
        );

        return membership;
    }

    /**
     * Rejects an invitation.
     *
     * @param token  the invitation token
     * @param userId the user rejecting the invitation (optional, for validation)
     * @throws ErpValidationException if invitation is not pending
     */
    public void rejectInvitation(InvitationToken token, UserId userId) {
        var val = new ValidationError.Builder();

        if (token == null) {
            val.addError("token", "Token cannot be null");
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        Invitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> {
                    val.addError("token", INVITATION_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        invitation.reject();
        invitationRepository.save(invitation);
    }

    /**
     * Cancels a pending invitation.
     *
     * @param invitationId the invitation to cancel
     * @param cancelledBy  the user cancelling the invitation (typically the inviter)
     * @throws ErpValidationException if invitation is not pending
     */
    public void cancelInvitation(com.jpriva.erpsp.auth.domain.model.invitation.InvitationId invitationId, UserId cancelledBy) {
        var val = new ValidationError.Builder();

        if (invitationId == null) {
            val.addError("invitationId", "Invitation ID cannot be null");
        }
        if (cancelledBy == null) {
            val.addError("cancelledBy", "Cancelled by cannot be null");
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> {
                    val.addError("invitationId", INVITATION_NOT_FOUND_ERROR);
                    return new ErpValidationException(AuthErrorCode.AUTH_MODULE, val.build());
                });

        invitation.cancel();
        invitationRepository.save(invitation);
    }
}
