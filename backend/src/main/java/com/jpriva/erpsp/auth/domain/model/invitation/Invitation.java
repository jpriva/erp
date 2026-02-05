package com.jpriva.erpsp.auth.domain.model.invitation;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.InvitationValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate root representing an invitation to join a tenant with specific roles.
 * </br>
 * Invariants:
 * - Invitation must have at least one role
 * - Email must be valid
 * - Token must be cryptographically secure
 * - Cannot accept expired invitation
 * - Cannot modify the status of a non-pending invitation
 */
public class Invitation {

    private final InvitationId invitationId;
    private final TenantId tenantId;
    private final Email email;
    private final UserId invitedBy;
    private final InvitationToken token;
    private final Instant createdAt;
    private final Instant expiresAt;
    private final Set<RoleId> roleIds;
    private InvitationStatus status;

    public Invitation(
            InvitationId invitationId,
            TenantId tenantId,
            Email email,
            UserId invitedBy,
            Set<RoleId> roleIds,
            InvitationStatus status,
            InvitationToken token,
            Instant createdAt,
            Instant expiresAt
    ) {
        var val = new ValidationError.Builder();
        if (invitationId == null) {
            val.addError(InvitationValidationError.ID_EMPTY);
        }
        if (tenantId == null) {
            val.addError(InvitationValidationError.TENANT_ID_EMPTY);
        }
        if (email == null) {
            val.addError(InvitationValidationError.EMAIL_EMPTY);
        }
        if (invitedBy == null) {
            val.addError(InvitationValidationError.INVITED_BY_EMPTY);
        }
        if (roleIds == null) {
            val.addError(InvitationValidationError.ROLE_IDS_EMPTY);
        } else if (roleIds.isEmpty()) {
            val.addError(InvitationValidationError.ROLE_IDS_EMPTY);
        }
        if (status == null) {
            val.addError(InvitationValidationError.STATUS_EMPTY);
        }
        if (token == null) {
            val.addError(InvitationValidationError.TOKEN_EMPTY);
        }
        if (createdAt == null) {
            val.addError(InvitationValidationError.CREATED_AT_EMPTY);
        }
        if (expiresAt == null) {
            val.addError(InvitationValidationError.EXPIRES_AT_EMPTY);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        this.invitationId = invitationId;
        this.tenantId = tenantId;
        this.email = email;
        this.invitedBy = invitedBy;
        assert roleIds != null;
        this.roleIds = new HashSet<>(roleIds);
        this.status = status;
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    /**
     * Factory method to create a new invitation.
     *
     * @param tenantId  the tenant being invited to
     * @param email     the email of the invited user
     * @param invitedBy the user sending the invitation
     * @param roleIds   the roles to assign upon acceptance (must not be empty)
     * @param validFor  the duration the invitation is valid for
     */
    public static Invitation create(
            TenantId tenantId,
            Email email,
            UserId invitedBy,
            Set<RoleId> roleIds,
            Duration validFor
    ) {
        var val = new ValidationError.Builder();
        if (roleIds == null || roleIds.isEmpty()) {
            val.addError(InvitationValidationError.ROLE_IDS_EMPTY);
        }
        if (validFor == null || validFor.isNegative() || validFor.isZero()) {
            val.addError(InvitationValidationError.VALID_FOR_INVALID);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        Instant now = Instant.now();
        assert validFor != null;
        return new Invitation(
                InvitationId.generate(),
                tenantId,
                email,
                invitedBy,
                roleIds,
                InvitationStatus.PENDING,
                InvitationToken.generate(),
                now,
                now.plus(validFor)
        );
    }

    /**
     * Factory method to restore invitation from the persistence layer.
     */
    public static Invitation fromPersistence(
            UUID invitationId,
            UUID tenantId,
            String email,
            UUID invitedBy,
            Set<UUID> roleIds,
            String status,
            String token,
            Instant createdAt,
            Instant expiresAt
    ) {
        try {
            Set<RoleId> roleIdSet = new HashSet<>();
            if (roleIds != null) {
                roleIds.forEach(id -> roleIdSet.add(new RoleId(id)));
            }
            return new Invitation(
                    new InvitationId(invitationId),
                    new TenantId(tenantId),
                    new Email(email),
                    new UserId(invitedBy),
                    roleIdSet,
                    InvitationStatus.of(status),
                    new InvitationToken(token),
                    createdAt,
                    expiresAt
            );
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
    }

    /**
     * Accepts this invitation.
     * Can only accept pending, non-expired invitations.
     *
     * @throws ErpValidationException if the invitation is not pending or has expired
     */
    public void accept(UserId userId) {
        var val = new ValidationError.Builder();
        if (userId == null) {
            val.addError(InvitationValidationError.USER_ID_EMPTY);
        }

        if (status != InvitationStatus.PENDING) {
            val.addError(InvitationValidationError.STATUS_INVALID);
        }

        if (isExpired()) {
            status = InvitationStatus.EXPIRED;
            val.addError(InvitationValidationError.STATUS_EXPIRED);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        this.status = InvitationStatus.ACCEPTED;
    }

    /**
     * Rejects this invitation.
     * Can only reject pending invitations.
     *
     * @throws ErpValidationException if the invitation is not pending
     */
    public void reject() {
        if (status != InvitationStatus.PENDING) {
            throw new ErpAuthValidationException(ValidationError.createSingle(InvitationValidationError.STATUS_INVALID));
        }
        this.status = InvitationStatus.REJECTED;
    }

    /**
     * Cancels this invitation.
     * Can only cancel pending invitations.
     *
     * @throws ErpValidationException if the invitation is not pending
     */
    public void cancel() {
        if (status != InvitationStatus.PENDING) {
            throw new ErpAuthValidationException(ValidationError.createSingle(InvitationValidationError.STATUS_INVALID));
        }
        this.status = InvitationStatus.CANCELLED;
    }

    /**
     * Checks if this invitation has expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    /**
     * Checks if this invitation is valid (pending and not expired).
     */
    public boolean isValid() {
        return status == InvitationStatus.PENDING && !isExpired();
    }

    /**
     * Checks if this invitation is pending.
     */
    public boolean isPending() {
        return status == InvitationStatus.PENDING;
    }

    /**
     * Checks if this invitation has been accepted.
     */
    public boolean isAccepted() {
        return status == InvitationStatus.ACCEPTED;
    }

    /**
     * Getters
     */
    public InvitationId getInvitationId() {
        return invitationId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public Email getEmail() {
        return email;
    }

    public UserId getInvitedBy() {
        return invitedBy;
    }

    public InvitationToken getToken() {
        return token;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Set<RoleId> getRoleIds() {
        return Collections.unmodifiableSet(roleIds);
    }

    public InvitationStatus getStatus() {
        return status;
    }
}
