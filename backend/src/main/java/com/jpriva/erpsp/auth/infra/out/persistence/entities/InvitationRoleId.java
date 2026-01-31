package com.jpriva.erpsp.auth.infra.out.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key for InvitationRoleEntity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationRoleId implements Serializable {
    private UUID invitationId;
    private UUID roleId;
}
