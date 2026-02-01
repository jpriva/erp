package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.invitation.Invitation;
import com.jpriva.erpsp.auth.domain.model.invitation.InvitationId;
import com.jpriva.erpsp.auth.domain.model.invitation.InvitationToken;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.model.Email;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Port for persisting and querying invitations.
 * <p>
 * Implementations should ensure the following constraints:
 * - UNIQUE(token) to ensure tokens are unique
 * - UNIQUE(tenant_id, email) WHERE status = 'PENDING' to prevent duplicate pending invitations
 * - Cascade delete invitation_roles when invitation is deleted
 */
public interface InvitationRepositoryPort {
    /**
     * Saves an invitation (insert or update).
     */
    void save(Invitation invitation);

    /**
     * Finds an invitation by its ID.
     */
    Optional<Invitation> findById(InvitationId invitationId);

    /**
     * Finds an invitation by its token.
     */
    Optional<Invitation> findByToken(InvitationToken token);

    /**
     * Finds all invitations for a tenant (all statuses).
     */
    List<Invitation> findByTenantId(TenantId tenantId);

    /**
     * Finds all pending invitations for a tenant.
     */
    List<Invitation> findPendingByTenantId(TenantId tenantId);

    /**
     * Finds a pending invitation for a specific email and tenant.
     */
    Optional<Invitation> findPendingByTenantIdAndEmail(TenantId tenantId, Email email);

    /**
     * Finds all invitations sent to a specific email across all tenants.
     */
    List<Invitation> findByEmail(Email email);

    /**
     * Finds all pending invitations for a specific email.
     */
    List<Invitation> findPendingByEmail(Email email);

    /**
     * Finds all invitations sent by a specific user.
     */
    List<Invitation> findByInvitedBy(UserId userId);

    /**
     * Deletes an invitation by ID.
     */
    void deleteById(InvitationId invitationId);

    /**
     * Deletes all expired invitations before a given date.
     * Useful for cleanup jobs.
     */
    void deleteExpiredBefore(Instant cutoffDate);
}
