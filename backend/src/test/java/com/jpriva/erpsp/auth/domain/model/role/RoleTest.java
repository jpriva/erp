package com.jpriva.erpsp.auth.domain.model.role;

import com.jpriva.erpsp.auth.domain.model.tenant.TenantId;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;

class RoleTest {

    @Nested
    class ConstructorTests {
        @Test
        void constructor_Success() {
            UUID roleUuid = UUID.randomUUID();
            UUID tenantUuid = UUID.randomUUID();

            Role role = new Role(
                    new RoleId(roleUuid),
                    new TenantId(tenantUuid),
                    new RoleName("Admin"),
                    new HashSet<>()
            );

            assertThat(role).isNotNull();
            assertThat(role.getRoleId().value()).isEqualTo(roleUuid);
            assertThat(role.getTenantId().value()).isEqualTo(tenantUuid);
            assertThat(role.getName().value()).isEqualTo("Admin");
            assertThat(role.getMembers()).isEmpty();
        }

        @Test
        void constructor_ShouldFailForRoleIdNull() {
            assertThatThrownBy(() -> new Role(
                    null,
                    TenantId.generate(),
                    new RoleName("Admin"),
                    new HashSet<>()
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "roleId");
                    });
        }

        @Test
        void constructor_ShouldFailForTenantIdNull() {
            assertThatThrownBy(() -> new Role(
                    RoleId.generate(),
                    null,
                    new RoleName("Admin"),
                    new HashSet<>()
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "tenantId");
                    });
        }

        @Test
        void constructor_ShouldFailForNameNull() {
            assertThatThrownBy(() -> new Role(
                    RoleId.generate(),
                    TenantId.generate(),
                    null,
                    new HashSet<>()
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "roleName");
                    });
        }
    }

    @Nested
    class CreateTests {

        @Test
        void create_Success() {
            TenantId tenantId = TenantId.generate();
            Role role = Role.create(tenantId, "Admin");

            assertThat(role).isNotNull();
            assertThat(role.getRoleId()).isNotNull();
            assertThat(role.getTenantId()).isEqualTo(tenantId);
            assertThat(role.getName().value()).isEqualTo("Admin");
            assertThat(role.getMembers()).isEmpty();
        }

        @Test
        void create_ShouldPropagateErrorsFromNameValidation() {
            assertThatThrownBy(() -> Role.create(TenantId.generate(), "A"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "roleName");
                    });
        }

        @Test
        void create_ShouldFailIfTenantIdNull() {
            assertThatThrownBy(() -> Role.create(null, "Admin"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "tenantId");
                    });
        }

        @Test
        void createDefaultRoles_Success() {
            TenantId tenantId = TenantId.generate();
            Role role = Role.createDefaultRoles(tenantId);

            assertThat(role).isNotNull();
            assertThat(role.getTenantId()).isEqualTo(tenantId);
            assertThat(role.getName().value()).isEqualTo("ADMIN");
        }
    }

    @Nested
    class FromPersistenceTests {

        @Test
        void fromPersistence_Success() {
            UUID roleUuid = UUID.randomUUID();
            UUID tenantUuid = UUID.randomUUID();
            UUID memberUuid1 = UUID.randomUUID();
            UUID memberUuid2 = UUID.randomUUID();
            Set<UUID> memberIds = new HashSet<>();
            memberIds.add(memberUuid1);
            memberIds.add(memberUuid2);

            Role role = Role.fromPersistence(roleUuid, tenantUuid, "Editor", memberIds);

            assertThat(role).isNotNull();
            assertThat(role.getRoleId().value()).isEqualTo(roleUuid);
            assertThat(role.getTenantId().value()).isEqualTo(tenantUuid);
            assertThat(role.getName().value()).isEqualTo("Editor");
            assertThat(role.getMembers()).hasSize(2);
            assertThat(role.getMembers()).extracting(UserId::value).contains(memberUuid1, memberUuid2);
        }

        @Test
        void fromPersistence_SuccessWithNullMembers() {
            UUID roleUuid = UUID.randomUUID();
            UUID tenantUuid = UUID.randomUUID();

            Role role = Role.fromPersistence(roleUuid, tenantUuid, "Viewer", null);

            assertThat(role).isNotNull();
            assertThat(role.getMembers()).isEmpty();
        }

        @Test
        void fromPersistence_ShouldThrowPersistenceCompromisedExceptionIfDoesntMatchDomain() {
            assertThatThrownBy(() -> Role.fromPersistence(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    null,
                    new HashSet<>()
            ))
                    .isInstanceOf(ErpPersistenceCompromisedException.class)
                    .satisfies(exception -> {
                        ErpPersistenceCompromisedException ex = (ErpPersistenceCompromisedException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                    });
        }
    }

    @Nested
    class MembershipTests {

        @Test
        void assignUser_Success() {
            Role role = Role.create(TenantId.generate(), "Admin");
            UserId userId = UserId.generate();

            role.assignUser(userId);

            assertThat(role.hasMember(userId)).isTrue();
            assertThat(role.getMembers()).contains(userId);
        }

        @Test
        void assignUser_IgnoresNullUserId() {
            Role role = Role.create(TenantId.generate(), "Admin");
            int initialSize = role.getMembers().size();

            role.assignUser(null);

            assertThat(role.getMembers()).hasSize(initialSize);
        }

        @Test
        void assignUser_MultipleUsers() {
            Role role = Role.create(TenantId.generate(), "Admin");
            UserId user1 = UserId.generate();
            UserId user2 = UserId.generate();
            UserId user3 = UserId.generate();

            role.assignUser(user1);
            role.assignUser(user2);
            role.assignUser(user3);

            assertThat(role.getMembers()).hasSize(3);
            assertThat(role.hasMember(user1)).isTrue();
            assertThat(role.hasMember(user2)).isTrue();
            assertThat(role.hasMember(user3)).isTrue();
        }

        @Test
        void revokeUser_Success() {
            Role role = Role.create(TenantId.generate(), "Admin");
            UserId userId = UserId.generate();
            role.assignUser(userId);

            role.revokeUser(userId);

            assertThat(role.hasMember(userId)).isFalse();
            assertThat(role.getMembers()).isEmpty();
        }

        @Test
        void revokeUser_IgnoresNullUserId() {
            Role role = Role.create(TenantId.generate(), "Admin");
            UserId userId = UserId.generate();
            role.assignUser(userId);

            role.revokeUser(null);

            assertThat(role.getMembers()).hasSize(1);
            assertThat(role.hasMember(userId)).isTrue();
        }

        @Test
        void revokeUser_NonExistentUser() {
            Role role = Role.create(TenantId.generate(), "Admin");
            UserId user1 = UserId.generate();
            UserId user2 = UserId.generate();
            role.assignUser(user1);

            role.revokeUser(user2);

            assertThat(role.getMembers()).hasSize(1);
            assertThat(role.hasMember(user1)).isTrue();
        }

        @Test
        void hasMember_ReturnsFalseForNullUserId() {
            Role role = Role.create(TenantId.generate(), "Admin");

            boolean result = role.hasMember(null);

            assertThat(result).isFalse();
        }

        @Test
        void getMembers_ReturnsUnmodifiableSet() {
            Role role = Role.create(TenantId.generate(), "Admin");
            UserId userId = UserId.generate();
            role.assignUser(userId);

            Set<UserId> members = role.getMembers();

            assertThatThrownBy(() -> members.add(UserId.generate()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
