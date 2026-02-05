package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;

class TenantTest {

    @Nested
    class ConstructorTests {
        @Test
        void constructor_Success() {
            UUID tenantUuid = UUID.randomUUID();
            UUID ownerUuid = UUID.randomUUID();
            Instant createdAt = Instant.now();

            Tenant tenant = new Tenant(
                    new TenantId(tenantUuid),
                    new UserId(ownerUuid),
                    new TenantName("Acme Corporation"),
                    TenantStatus.ACTIVE,
                    createdAt
            );

            assertThat(tenant).isNotNull();
            assertThat(tenant.getTenantId().value()).isEqualTo(tenantUuid);
            assertThat(tenant.getOwnerId().value()).isEqualTo(ownerUuid);
            assertThat(tenant.getName().value()).isEqualTo("Acme Corporation");
            assertThat(tenant.getStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(tenant.getCreatedAt()).isEqualTo(createdAt);
        }

        @Test
        void constructor_ShouldFailForTenantIdNull() {
            assertThatThrownBy(() -> new Tenant(
                    null,
                    UserId.generate(),
                    new TenantName("Acme Corporation"),
                    TenantStatus.ACTIVE,
                    Instant.now()
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "tenantId");
                    });
        }

        @Test
        void constructor_ShouldFailForOwnerIdNull() {
            assertThatThrownBy(() -> new Tenant(
                    TenantId.generate(),
                    null,
                    new TenantName("Acme Corporation"),
                    TenantStatus.ACTIVE,
                    Instant.now()
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "ownerId");
                    });
        }

        @Test
        void constructor_ShouldFailForNameNull() {
            assertThatThrownBy(() -> new Tenant(
                    TenantId.generate(),
                    UserId.generate(),
                    null,
                    TenantStatus.ACTIVE,
                    Instant.now()
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "name");
                    });
        }

        @Test
        void constructor_ShouldFailForStatusNull() {
            assertThatThrownBy(() -> new Tenant(
                    TenantId.generate(),
                    UserId.generate(),
                    new TenantName("Acme Corporation"),
                    null,
                    Instant.now()
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "status");
                    });
        }

        @Test
        void constructor_ShouldFailForCreatedAtNull() {
            assertThatThrownBy(() -> new Tenant(
                    TenantId.generate(),
                    UserId.generate(),
                    new TenantName("Acme Corporation"),
                    TenantStatus.ACTIVE,
                    null
            ))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "createdAt");
                    });
        }
    }

    @Nested
    class CreateTests {
        @Test
        void create_Success() {
            UserId ownerId = UserId.generate();
            Tenant tenant = Tenant.create(ownerId, "Acme Corporation");

            assertThat(tenant).isNotNull();
            assertThat(tenant.getTenantId()).isNotNull();
            assertThat(tenant.getOwnerId()).isEqualTo(ownerId);
            assertThat(tenant.getName().value()).isEqualTo("Acme Corporation");
            assertThat(tenant.getStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(tenant.getCreatedAt()).isNotNull();
            assertThat(tenant.isActive()).isTrue();
        }

        @Test
        void create_ShouldPropagateErrorsFromNameValidation() {
            assertThatThrownBy(() -> Tenant.create(UserId.generate(), "A"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "name");
                    });
        }
    }

    @Nested
    class FromPersistenceTests {
        @Test
        void fromPersistence_Success() {
            UUID tenantUuid = UUID.randomUUID();
            UUID ownerUuid = UUID.randomUUID();
            Instant createdAt = Instant.now();

            Tenant tenant = Tenant.fromPersistence(
                    tenantUuid,
                    ownerUuid,
                    "Acme Corporation",
                    "ACTIVE",
                    createdAt
            );

            assertThat(tenant).isNotNull();
            assertThat(tenant.getTenantId().value()).isEqualTo(tenantUuid);
            assertThat(tenant.getOwnerId().value()).isEqualTo(ownerUuid);
            assertThat(tenant.getName().value()).isEqualTo("Acme Corporation");
            assertThat(tenant.getStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(tenant.getCreatedAt()).isEqualTo(createdAt);
        }

        @Test
        void fromPersistence_ShouldThrowPersistenceCompromisedExceptionIfDoesntMatchDomain() {
            assertThatThrownBy(() -> Tenant.fromPersistence(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    null,
                    "ACTIVE",
                    Instant.now()
            ))
                    .isInstanceOf(ErpPersistenceCompromisedException.class)
                    .satisfies(exception -> {
                        ErpPersistenceCompromisedException ex = (ErpPersistenceCompromisedException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                    });
        }

        @Test
        void fromPersistence_ShouldThrowPersistenceCompromisedExceptionForInvalidStatus() {
            assertThatThrownBy(() -> Tenant.fromPersistence(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "Acme Corporation",
                    "INVALID_STATUS",
                    Instant.now()
            ))
                    .isInstanceOf(ErpPersistenceCompromisedException.class)
                    .satisfies(exception -> {
                        ErpPersistenceCompromisedException ex = (ErpPersistenceCompromisedException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                    });
        }
    }

    @Nested
    class BehaviorTests {
        @Test
        void changeName_Success() {
            Tenant tenant = Tenant.create(UserId.generate(), "Acme Corporation");
            tenant.changeName("New Acme Corporation");
            assertThat(tenant.getName().value()).isEqualTo("New Acme Corporation");
        }

        @Test
        void suspend_Success() {
            Tenant tenant = Tenant.create(UserId.generate(), "Acme Corporation");
            assertThat(tenant.isActive()).isTrue();

            tenant.suspend();

            assertThat(tenant.getStatus()).isEqualTo(TenantStatus.SUSPENDED);
            assertThat(tenant.isActive()).isFalse();
        }

        @Test
        void activate_Success() {
            Tenant tenant = Tenant.create(UserId.generate(), "Acme Corporation");
            tenant.suspend();
            assertThat(tenant.isActive()).isFalse();

            tenant.activate();

            assertThat(tenant.getStatus()).isEqualTo(TenantStatus.ACTIVE);
            assertThat(tenant.isActive()).isTrue();
        }

        @Test
        void markAsDeleted_Success() {
            Tenant tenant = Tenant.create(UserId.generate(), "Acme Corporation");

            tenant.markAsDeleted();

            assertThat(tenant.getStatus()).isEqualTo(TenantStatus.DELETED);
            assertThat(tenant.isActive()).isFalse();
        }

        @Test
        void isActive_ReturnsTrueOnlyForActiveStatus() {
            Tenant tenant = Tenant.create(UserId.generate(), "Acme Corporation");
            assertThat(tenant.isActive()).isTrue();

            tenant.suspend();
            assertThat(tenant.isActive()).isFalse();

            tenant.activate();
            assertThat(tenant.isActive()).isTrue();

            tenant.markAsDeleted();
            assertThat(tenant.isActive()).isFalse();
        }
    }
}
