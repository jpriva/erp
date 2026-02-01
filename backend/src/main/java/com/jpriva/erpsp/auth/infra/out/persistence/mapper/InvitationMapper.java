package com.jpriva.erpsp.auth.infra.out.persistence.mapper;

import com.jpriva.erpsp.auth.domain.model.invitation.Invitation;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.InvitationEntity;
import com.jpriva.erpsp.auth.infra.out.persistence.entities.InvitationRoleEntity;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper for converting between Invitation domain model and InvitationEntity JPA entity.
 */
public class InvitationMapper {
    private InvitationMapper() {
    }

    /**
     * Converts an Invitation domain model to an InvitationEntity JPA entity.
     *
     * @param domain the Invitation domain model
     * @return the InvitationEntity JPA entity
     */
    public static InvitationEntity domainToEntity(Invitation domain) {
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
     * Converts an InvitationEntity JPA entity to an Invitation domain model.
     *
     * @param entity the InvitationEntity JPA entity
     * @return the Invitation domain model
     */
    public static Invitation entityToDomain(InvitationEntity entity) {
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
