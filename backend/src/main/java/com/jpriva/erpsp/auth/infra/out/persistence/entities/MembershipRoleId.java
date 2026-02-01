package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for MembershipRoleEntity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRoleId implements Serializable {
    private UUID membershipId;
    private UUID roleId;
}
