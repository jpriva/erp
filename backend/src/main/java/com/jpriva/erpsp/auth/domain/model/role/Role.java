package com.jpriva.erpsp.auth.domain.model.role;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Role {
    private static final String ROLE_ID_NULL_ERROR = "Role ID cannot be null";
    private static final String TENANT_ID_NULL_ERROR = "Tenant ID cannot be null";
    private static final String NAME_NULL_ERROR = "Role Name cannot be null";

    private static final String FIELD_ROLE_ID = "roleId";
    private static final String FIELD_TENANT_ID = "tenantId";
    private static final String FIELD_NAME = "name";

    private final RoleId roleId;
    private final TenantId tenantId;
    private final RoleName name;
    private final Set<UserId> members;

    public Role(RoleId roleId, TenantId tenantId, RoleName name, Set<UserId> members) {
        var val = new ValidationError.Builder();
        if (roleId == null) {
            val.addError(FIELD_ROLE_ID, ROLE_ID_NULL_ERROR);
        }
        if (tenantId == null) {
            val.addError(FIELD_TENANT_ID, TENANT_ID_NULL_ERROR);
        }
        if (name == null) {
            val.addError(FIELD_NAME, NAME_NULL_ERROR);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        this.roleId = roleId;
        this.tenantId = tenantId;
        this.name = name;
        this.members = members != null ? new HashSet<>(members) : new HashSet<>();
    }

    public static Role create(TenantId tenantId, String name) {
        var val = new ValidationError.Builder();
        RoleName roleName = null;
        try {
            roleName = new RoleName(name);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);

        return new Role(RoleId.generate(), tenantId, roleName, new HashSet<>());
    }

    public static Role createDefaultRoles(TenantId tenantId) {
        return create(tenantId, "ADMIN");
    }

    public static Role fromPersistence(UUID roleId, UUID tenantId, String name, Set<UUID> memberIds) {
        try {
            Set<UserId> members = new HashSet<>();
            if (memberIds != null) {
                for (UUID id : memberIds) {
                    members.add(new UserId(id));
                }
            }
            return new Role(new RoleId(roleId), new TenantId(tenantId), new RoleName(name), members);
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
    }

    public void assignUser(UserId userId) {
        if (userId != null) {
            this.members.add(userId);
        }
    }

    public void revokeUser(UserId userId) {
        if (userId != null) {
            this.members.remove(userId);
        }
    }

    public boolean hasMember(UserId userId) {
        return this.members.contains(userId);
    }

    public RoleId getRoleId() {
        return roleId;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public RoleName getName() {
        return name;
    }

    public Set<UserId> getMembers() {
        return Collections.unmodifiableSet(members);
    }
}
