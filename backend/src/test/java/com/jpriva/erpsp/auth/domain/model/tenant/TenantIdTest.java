package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;

class TenantIdTest {

    @Nested
    class TenantIdHappyPathsTest {
        @Test
        void constructor_CreateTenantId_Success() {
            UUID uuid = UUID.randomUUID();
            TenantId tenantId = new TenantId(uuid);
            assertThat(tenantId).isNotNull();
            assertThat(tenantId.value()).isEqualTo(uuid);
        }

        @Test
        void from_CreateTenantIdFromString_Success() {
            UUID uuid = UUID.randomUUID();
            TenantId tenantId = TenantId.from(uuid.toString());
            assertThat(tenantId).isNotNull();
            assertThat(tenantId.value()).isEqualTo(uuid);
        }

        @Test
        void generate_CreateTenantId_Success() {
            TenantId tenantId = TenantId.generate();
            assertThat(tenantId).isNotNull();
            assertThat(tenantId.value()).isNotNull();
        }

        @Test
        void toString_TenantIdToString_Success() {
            UUID uuid = UUID.randomUUID();
            TenantId tenantId = new TenantId(uuid);
            assertThat(tenantId.toString()).isEqualTo(uuid.toString());
        }
    }

    @Nested
    class TenantIdValidationTests {

        @Test
        void constructor_CreateTenantId_FailIfNullValue() {
            assertThatThrownBy(() -> new TenantId(null))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertHasFieldError(ex, "tenantId");
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        void from_CreateTenantIdFromString_FailIfNullOrBlank(String value) {
            assertThatThrownBy(() -> TenantId.from(value))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertHasFieldError(ex, "tenantId");
                    });
        }

        @Test
        void from_CreateTenantIdFromString_FailIfInvalidFormat() {
            assertThatThrownBy(() -> TenantId.from("invalid_format"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertHasFieldError(ex, "tenantId");
                    });
        }
    }
}
