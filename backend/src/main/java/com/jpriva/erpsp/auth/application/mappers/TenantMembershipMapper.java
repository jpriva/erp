package com.jpriva.erpsp.auth.application.mappers;

import com.jpriva.erpsp.auth.domain.model.membership.MembershipRole;
import com.jpriva.erpsp.auth.domain.model.membership.TenantMembership;
import com.jpriva.erpsp.auth.application.dto.TenantMembershipDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for converting between TenantMembership domain model and TenantMembershipDto.
 */
@Component
public class TenantMembershipMapper {
    /**
     * Converts a domain TenantMembership to a DTO.
     */
    public TenantMembershipDto toDto(TenantMembership domain) {
        return TenantMembershipDto.builder()
                .membershipId(domain.getMembershipId().value())
                .userId(domain.getUserId().value())
                .tenantId(domain.getTenantId().value())
                .status(domain.getStatus().toString())
                .joinedAt(domain.getJoinedAt())
                .roles(domain.getRoles()
                        .stream()
                        .map(this::membershipRoleToDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    /**
     * Converts a domain MembershipRole to a DTO.
     */
    private TenantMembershipDto.MembershipRoleDto membershipRoleToDto(MembershipRole domain) {
        return TenantMembershipDto.MembershipRoleDto.builder()
                .roleId(domain.roleId().value())
                .roleName(domain.roleName().value())
                .assignedAt(domain.assignedAt())
                .assignedBy(domain.assignedBy().value())
                .build();
    }
}
