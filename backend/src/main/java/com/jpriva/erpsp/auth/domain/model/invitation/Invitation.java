package com.jpriva.erpsp.auth.domain.model.invitation;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.Email;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

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
    private static final String INVITATION_ID_NULL_ERROR = "Invitation ID can't be empty";
    private static final String TENANT_ID_NULL_ERROR = "Tenant ID can't be empty";
    private static final String EMAIL_NULL_ERROR = "Email can't be empty";
    private static final String INVITED_BY_NULL_ERROR = "Invited by can't be empty";
    private static final String STATUS_NULL_ERROR = "Status can't be empty";
    private static final String TOKEN_NULL_ERROR = "Token can't be empty";
    private static final String CREATED_AT_NULL_ERROR = "Created at can't be empty";
    private static final String EXPIRES_AT_NULL_ERROR = "Expires at can't be empty";
    private static final String ROLE_IDS_NULL_ERROR = "Role IDs can't be null";
    private static final String ROLE_IDS_EMPTY_ERROR = "Invitation must have at least one role";
    private static final String INVITATION_EXPIRED_ERROR = "Invitation has expired";
    private static final String INVITATION_NOT_PENDING_ERROR = "Only pending invitations can be modified";

    private static final String FIELD_INVITATION_ID = "invitationId";
    private static final String FIELD_TENANT_ID = "tenantId";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_INVITED_BY = "invitedBy";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_TOKEN = "token";
    private static final String FIELD_CREATED_AT = "createdAt";
    private static final String FIELD_EXPIRES_AT = "expiresAt";
    private static final String FIELD_ROLE_IDS = "roleIds";

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
            val.addError(FIELD_INVITATION_ID, INVITATION_ID_NULL_ERROR);
        }
        if (tenantId == null) {
            val.addError(FIELD_TENANT_ID, TENANT_ID_NULL_ERROR);
        }
        if (email == null) {
            val.addError(FIELD_EMAIL, EMAIL_NULL_ERROR);
        }
        if (invitedBy == null) {
            val.addError(FIELD_INVITED_BY, INVITED_BY_NULL_ERROR);
        }
        if (roleIds == null) {
            val.addError(FIELD_ROLE_IDS, ROLE_IDS_NULL_ERROR);
        } else if (roleIds.isEmpty()) {
            val.addError(FIELD_ROLE_IDS, ROLE_IDS_EMPTY_ERROR);
        }
        if (status == null) {
            val.addError(FIELD_STATUS, STATUS_NULL_ERROR);
        }
        if (token == null) {
            val.addError(FIELD_TOKEN, TOKEN_NULL_ERROR);
        }
        if (createdAt == null) {
            val.addError(FIELD_CREATED_AT, CREATED_AT_NULL_ERROR);
        }
        if (expiresAt == null) {
            val.addError(FIELD_EXPIRES_AT, EXPIRES_AT_NULL_ERROR);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        this.invitationId = invitationId;
        this.tenantId = tenantId;
        this.email = email;
        this.invitedBy = invitedBy;
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
            java.time.Duration validFor
    ) {
        var val = new ValidationError.Builder();
        if (roleIds == null || roleIds.isEmpty()) {
            val.addError(FIELD_ROLE_IDS, ROLE_IDS_EMPTY_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }
        if (validFor == null || validFor.isNegative() || validFor.isZero()) {
            val.addError("validFor", "Validity duration must be positive");
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        Instant now = Instant.now();
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
            val.addError("userId", "User ID cannot be null");
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        if (status != InvitationStatus.PENDING) {
            val.addError(FIELD_STATUS, INVITATION_NOT_PENDING_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

        if (isExpired()) {
            status = InvitationStatus.EXPIRED;
            val.addError(FIELD_STATUS, INVITATION_EXPIRED_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        }

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
            var val = new ValidationError.Builder();
            val.addError(FIELD_STATUS, INVITATION_NOT_PENDING_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
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
            var val = new ValidationError.Builder();
            val.addError(FIELD_STATUS, INVITATION_NOT_PENDING_ERROR);
            ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
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
