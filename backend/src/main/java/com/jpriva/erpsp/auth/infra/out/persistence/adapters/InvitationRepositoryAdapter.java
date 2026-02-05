package com.jpriva.erpsp.auth.infra.out.persistence.adapters;

import com.jpriva.erpsp.auth.domain.model.invitation.Invitation;
import com.jpriva.erpsp.auth.domain.model.invitation.InvitationId;
import com.jpriva.erpsp.auth.domain.model.invitation.InvitationToken;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.InvitationRepositoryPort;
import com.jpriva.erpsp.auth.infra.out.persistence.mapper.InvitationMapper;
import com.jpriva.erpsp.shared.domain.model.Email;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
        var entity = InvitationMapper.domainToEntity(invitation);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Invitation> findById(InvitationId invitationId) {
        return jpaRepository.findById(invitationId.value())
                .map(InvitationMapper::entityToDomain);
    }

    @Override
    public Optional<Invitation> findByToken(InvitationToken token) {
        return jpaRepository.findByToken(token.value())
                .map(InvitationMapper::entityToDomain);
    }

    @Override
    public List<Invitation> findByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantId(tenantId.value())
                .stream()
                .map(InvitationMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findPendingByTenantId(TenantId tenantId) {
        return jpaRepository.findByTenantIdAndStatus(tenantId.value(), "PENDING")
                .stream()
                .map(InvitationMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Invitation> findPendingByTenantIdAndEmail(TenantId tenantId, Email email) {
        return jpaRepository.findByTenantIdAndEmailAndStatus(tenantId.value(), email.value(), "PENDING")
                .map(InvitationMapper::entityToDomain);
    }

    @Override
    public List<Invitation> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
                .stream()
                .map(InvitationMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findPendingByEmail(Email email) {
        return jpaRepository.findByEmailAndStatus(email.value(), "PENDING")
                .stream()
                .map(InvitationMapper::entityToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Invitation> findByInvitedBy(UserId userId) {
        return jpaRepository.findByInvitedBy(userId.value())
                .stream()
                .map(InvitationMapper::entityToDomain)
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
}
