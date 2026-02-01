package com.jpriva.erpsp.auth.domain.ports.out;

import com.jpriva.erpsp.auth.domain.model.role.Role;
import com.jpriva.erpsp.auth.domain.model.role.RoleId;
import com.jpriva.erpsp.auth.domain.model.role.RoleName;
import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Port for role persistence operations.
 */
public interface RoleRepositoryPort {
    /**
     * Saves a role.
     *
     * @param role the role to save
     */
    void save(Role role);

    /**
     * Finds a role by its ID.
     *
     * @param roleId the role ID
     * @return the role with the given ID, or empty if not found
     */
    Optional<Role> findById(RoleId roleId);

    /**
     * Finds all roles for a tenant.
     *
     * @param tenantId the tenant ID
     * @return list of roles for the given tenant, or empty if none found
     */
    List<Role> findByTenantId(TenantId tenantId);

    /**
     * Finds a role by tenant and name.
     * Useful for finding the ADMIN role.
     *
     * @param tenantId the tenant ID
     * @param roleName the role name
     * @return the role if found
     */
    Optional<Role> findByTenantIdAndName(TenantId tenantId, RoleName roleName);

    /**
     * Finds multiple roles by their IDs (batch fetch).
     *
     * @param roleIds the role IDs to fetch
     * @return list of roles found
     */
    List<Role> findByIds(Set<RoleId> roleIds);

    /**
     * Deletes a role by its ID.
     *
     * @param roleId the role ID to delete
     */
    void deleteById(RoleId roleId);
}
