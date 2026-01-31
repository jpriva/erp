package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.infra.out.persistence.entities.InvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for InvitationEntity.
 * Provides database access for invitations.
 */
@Repository
public interface InvitationJpaRepository extends JpaRepository<InvitationEntity, UUID> {
    /**
     * Finds an invitation by its token.
     */
    Optional<InvitationEntity> findByToken(String token);

    /**
     * Finds all invitations for a tenant.
     */
    List<InvitationEntity> findByTenantId(UUID tenantId);

    /**
     * Finds all pending invitations for a tenant.
     */
    List<InvitationEntity> findByTenantIdAndStatus(UUID tenantId, String status);

    /**
     * Finds a pending invitation for a specific email and tenant.
     */
    Optional<InvitationEntity> findByTenantIdAndEmailAndStatus(UUID tenantId, String email, String status);

    /**
     * Finds all invitations sent to a specific email.
     */
    List<InvitationEntity> findByEmail(String email);

    /**
     * Finds all pending invitations for a specific email.
     */
    List<InvitationEntity> findByEmailAndStatus(String email, String status);

    /**
     * Finds all invitations sent by a specific user.
     */
    List<InvitationEntity> findByInvitedBy(UUID userId);

    /**
     * Deletes all expired invitations before a given date.
     */
    @Modifying
    @Query("DELETE FROM InvitationEntity i WHERE i.expiresAt < :cutoffDate")
    void deleteExpiredBefore(@Param("cutoffDate") Instant cutoffDate);
}
