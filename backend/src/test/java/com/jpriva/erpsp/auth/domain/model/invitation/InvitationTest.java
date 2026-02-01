package com.jpriva.erpsp.auth.domain.model.invitation;

import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Invitation")
class InvitationTest {
    private TenantId tenantId;
    private Email email;
    private UserId invitedBy;
    private Set<RoleId> roleIds;
    private Duration validFor;

    @BeforeEach
    void setUp() {
        tenantId = new TenantId(UUID.randomUUID());
        email = new Email("user@example.com");
        invitedBy = new UserId(UUID.randomUUID());
        roleIds = new HashSet<>();
        roleIds.add(new RoleId(UUID.randomUUID()));
        validFor = Duration.ofDays(7);
    }

    @Test
    @DisplayName("should create invitation with secure token")
    void testCreate() {
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);

        assertNotNull(invitation.getInvitationId());
        assertEquals(tenantId, invitation.getTenantId());
        assertEquals(email, invitation.getEmail());
        assertEquals(invitedBy, invitation.getInvitedBy());
        assertEquals(InvitationStatus.PENDING, invitation.getStatus());
        assertNotNull(invitation.getToken());
        assertEquals(1, invitation.getRoleIds().size());
        assertTrue(invitation.isValid());
        assertTrue(invitation.isPending());
    }

    @Test
    @DisplayName("should fail to create invitation with empty roles")
    void testCreateWithEmptyRoles() {
        Set<RoleId> emptyRoles = new HashSet<>();

        assertThrows(ErpValidationException.class, () ->
                Invitation.create(tenantId, email, invitedBy, emptyRoles, validFor)
        );
    }

    @Test
    @DisplayName("should fail to create invitation with invalid duration")
    void testCreateWithInvalidDuration() {
        assertThrows(ErpValidationException.class, () ->
                Invitation.create(tenantId, email, invitedBy, roleIds, Duration.ZERO)
        );

        assertThrows(ErpValidationException.class, () ->
                Invitation.create(tenantId, email, invitedBy, roleIds, Duration.ofDays(-1))
        );
    }

    @Test
    @DisplayName("should accept valid invitation")
    void testAccept() {
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);
        UserId userId = new UserId(UUID.randomUUID());

        invitation.accept(userId);

        assertEquals(InvitationStatus.ACCEPTED, invitation.getStatus());
        assertTrue(invitation.isAccepted());
        assertFalse(invitation.isPending());
    }

    @Test
    @DisplayName("should reject pending invitation")
    void testReject() {
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);

        invitation.reject();

        assertEquals(InvitationStatus.REJECTED, invitation.getStatus());
        assertFalse(invitation.isPending());
    }

    @Test
    @DisplayName("should cancel pending invitation")
    void testCancel() {
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);

        invitation.cancel();

        assertEquals(InvitationStatus.CANCELLED, invitation.getStatus());
        assertFalse(invitation.isPending());
    }

    @Test
    @DisplayName("should detect expired invitation")
    void testIsExpired() {
        // Create invitation that expired 1 hour ago
        Instant now = Instant.now();
        Invitation invitation = new Invitation(
                new InvitationId(UUID.randomUUID()),
                tenantId,
                email,
                invitedBy,
                roleIds,
                InvitationStatus.PENDING,
                InvitationToken.generate(),
                now.minusSeconds(3600),
                now.minusSeconds(1800)  // Expired 30 minutes ago
        );

        assertTrue(invitation.isExpired());
        assertFalse(invitation.isValid());
    }

    @Test
    @DisplayName("should detect valid (not expired) invitation")
    void testIsValid() {
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, Duration.ofDays(7));

        assertFalse(invitation.isExpired());
        assertTrue(invitation.isValid());
    }

    @Test
    @DisplayName("should fail to accept expired invitation")
    void testAcceptExpiredInvitation() {
        Instant now = Instant.now();
        Invitation invitation = new Invitation(
                new InvitationId(UUID.randomUUID()),
                tenantId,
                email,
                invitedBy,
                roleIds,
                InvitationStatus.PENDING,
                InvitationToken.generate(),
                now.minusSeconds(3600),
                now.minusSeconds(1800)  // Expired 30 minutes ago
        );
        UserId userId = new UserId(UUID.randomUUID());

        assertThrows(ErpValidationException.class, () ->
                invitation.accept(userId)
        );
    }

    @Test
    @DisplayName("should fail to reject non-pending invitation")
    void testRejectNonPendingInvitation() {
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);
        invitation.reject();

        assertThrows(ErpValidationException.class, invitation::reject
        );
    }

    @Test
    @DisplayName("should fail to cancel non-pending invitation")
    void testCancelNonPendingInvitation() {
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);
        invitation.accept(new UserId(UUID.randomUUID()));

        assertThrows(ErpValidationException.class, invitation::cancel
        );
    }

    @Test
    @DisplayName("should generate unique secure tokens")
    void testTokensAreUnique() {
        Invitation invitation1 = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);
        Invitation invitation2 = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);

        assertNotEquals(invitation1.getToken().value(), invitation2.getToken().value());
    }

    @Test
    @DisplayName("token should be 64 characters")
    void testTokenLength() {
        Invitation invitation = Invitation.create(tenantId, email, invitedBy, roleIds, validFor);

        assertEquals(64, invitation.getToken().value().length());
    }
}
