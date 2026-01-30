package com.jpriva.erpsp.auth.application.mappers;

import com.jpriva.erpsp.auth.application.dto.InvitationDto;
import com.jpriva.erpsp.auth.domain.model.invitation.Invitation;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting between Invitation domain model and InvitationDto.
 */
@Component
public class InvitationMapper {
    /**
     * Converts a domain Invitation to a DTO.
     */
    public InvitationDto toDto(Invitation domain) {
        return InvitationDto.builder()
                .invitationId(domain.getInvitationId().value())
                .tenantId(domain.getTenantId().value())
                .email(domain.getEmail().value())
                .status(domain.getStatus().toString())
                .token(domain.getToken().value())
                .createdAt(domain.getCreatedAt())
                .expiresAt(domain.getExpiresAt())
                .roleIds(domain.getRoleIds()
                        .stream()
                        .map(RoleId::value)
                        .collect(Collectors.toSet()))
                .isValid(domain.isValid())
                .isExpired(domain.isExpired())
                .build();
    }
}
