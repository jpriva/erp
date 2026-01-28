package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TenantNameTest {

    @Nested
    class TenantNameHappyPathsTest {
        @Test
        void constructor_CreateTenantName_Success() {
            TenantName tenantName = new TenantName("Acme Corporation");
            assertThat(tenantName).isNotNull();
            assertThat(tenantName.value()).isEqualTo("Acme Corporation");
        }

        @Test
        void constructor_CreateTenantName_TrimsWhitespace() {
            TenantName tenantName = new TenantName("  Acme Corporation  ");
            assertThat(tenantName.value()).isEqualTo("Acme Corporation");
        }

        @Test
        void constructor_CreateTenantName_MinLength() {
            TenantName tenantName = new TenantName("AB");
            assertThat(tenantName.value()).isEqualTo("AB");
        }

        @Test
        void constructor_CreateTenantName_MaxLength() {
            String maxName = "A".repeat(100);
            TenantName tenantName = new TenantName(maxName);
            assertThat(tenantName.value()).isEqualTo(maxName);
        }

        @Test
        void toString_TenantNameToString_Success() {
            TenantName tenantName = new TenantName("Acme Corporation");
            assertThat(tenantName.toString()).isEqualTo("Acme Corporation");
        }
    }

    @Nested
    class TenantNameValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"A", " A "})
        void constructor_CreateTenantName_FailIfTooShort(String value) {
            assertThatThrownBy(() -> new TenantName(value))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("tenantName");
                    });
        }

        @Test
        void constructor_CreateTenantName_FailIfTooLong() {
            String tooLongName = "A".repeat(101);
            assertThatThrownBy(() -> new TenantName(tooLongName))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("tenantName");
                    });
        }
    }
}
