package com.jpriva.erpsp.auth.domain.services;

import com.jpriva.erpsp.auth.domain.model.role.Role;
import com.jpriva.erpsp.auth.domain.model.tenant.Tenant;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.auth.domain.ports.out.RoleRepositoryPort;
import com.jpriva.erpsp.auth.domain.ports.out.TenantRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TenantCreatorTest {

    @Mock
    private TenantRepositoryPort tenantRepository;

    @Mock
    private RoleRepositoryPort roleRepository;

    @InjectMocks
    private TenantCreator tenantCreator;

    @Test
    void createTenant_ShouldCreateTenantAndAdminRole_WhenValidData() {
        // Given
        UserId ownerId = UserId.generate();
        String tenantName = "Acme Corp";

        // When
        Tenant createdTenant = tenantCreator.createTenant(ownerId, tenantName);

        // Then
        assertThat(createdTenant).isNotNull();
        assertThat(createdTenant.getName().value()).isEqualTo(tenantName);
        assertThat(createdTenant.getOwnerId()).isEqualTo(ownerId);

        // Verify Tenant saved
        verify(tenantRepository).save(createdTenant);

        // Verify Role created and saved
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());

        Role savedRole = roleCaptor.getValue();
        assertThat(savedRole.getName().value()).isEqualTo("ADMIN");
        assertThat(savedRole.getTenantId()).isEqualTo(createdTenant.getTenantId());
        assertThat(savedRole.hasMember(ownerId)).isTrue();
    }
}
