package com.jpriva.erpsp.auth.infra.persistence.adapters;

import com.jpriva.erpsp.auth.domain.model.invitation.Invitation;
import com.jpriva.erpsp.auth.domain.model.invitation.InvitationId;
import com.jpriva.erpsp.auth.domain.model.invitation.InvitationToken;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.InvitationRepositoryPort;
import com.jpriva.erpsp.auth.infra.persistence.entities.InvitationEntity;
import com.jpriva.erpsp.auth.infra.persistence.entities.InvitationRoleEntity;
import com.jpriva.erpsp.shared.domain.model.Email;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adapter that implements InvitationRepositoryPort using Spring Data JPA.
 * Converts between domain model (Invitation) and JPA entity (InvitationEntity).
 */
@Component
public class InvitationRepositoryAdapter implements InvitationRepositoryPort {
    private final InvitationJpaRepository jpaRepository;

    public InvitationRepositoryAdapter(InvitationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Invitation invitation) {
        InvitationEntity entity = domainToEntity(invitation);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Invitation> findById(InvitationId invitationId) {
        return jpaRepository.findById(invitationId.value())
                .map(this::entityToDomain);
    }

    @Override
    public Optional<Invitation> findByToken(InvitationToken token) {
        return jpaRepository.findByToken(token.value())
                .map(this::entityToDomain);
    }

    @Override
    public List<Invitation> findByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantId(tenantId.value())
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findPendingByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantIdAndStatus(tenantId.value(), "PENDING")
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Invitation> findPendingByTenantIdAndEmail(TenantId tenantId, Email email) {
        return jpaRepository.findByTenantIdAndEmailAndStatus(tenantId.value(), email.value(), "PENDING")
                .map(this::entityToDomain);
    }

    @Override
    public List<Invitation> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findPendingByEmail(Email email) {
        return jpaRepository.findByEmailAndStatus(email.value(), "PENDING")
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findByInvitedBy(UserId userId) {
        return jpaRepository.findByInvitedBy(userId.value())
                .stream()
                .map(this::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(InvitationId invitationId) {
        jpaRepository.deleteById(invitationId.value());
    }

    @Override
    @Transactional
    public void deleteExpiredBefore(Instant cutoffDate) {
        jpaRepository.deleteExpiredBefore(cutoffDate);
    }

    /**
     * Converts domain model to JPA entity.
     */
    private InvitationEntity domainToEntity(Invitation domain) {
        Set<InvitationRoleEntity> roleEntities = domain.getRoleIds()
                .stream()
                .map(roleId -> InvitationRoleEntity.builder()
                        .invitationId(null) // Set by JPA relationship
                        .roleId(roleId.value())
                        .build())
                .collect(Collectors.toSet());

        return InvitationEntity.builder()
                .invitationId(domain.getInvitationId().value())
                .tenantId(domain.getTenantId().value())
                .email(domain.getEmail().value())
                .invitedBy(domain.getInvitedBy().value())
                .status(domain.getStatus().toString())
                .token(domain.getToken().value())
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .roles(roleEntities)
                .build();
    }

    /**
     * Converts JPA entity to domain model.
     */
    private Invitation entityToDomain(InvitationEntity entity) {
        Set<java.util.UUID> roleIds = entity.getRoles()
                .stream()
                .map(InvitationRoleEntity::getRoleId)
                .collect(Collectors.toSet());

        return Invitation.fromPersistence(
                entity.getInvitationId(),
                entity.getTenantId(),
                entity.getEmail(),
                entity.getInvitedBy(),
                roleIds,
                entity.getStatus(),
                entity.getToken(),
                entity.getCreatedAt(),
                entity.getExpiresAt()
        );
    }
}
