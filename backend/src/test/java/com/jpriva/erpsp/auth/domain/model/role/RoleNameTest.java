package com.jpriva.erpsp.auth.domain.model.role;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;

class RoleNameTest {

    @Nested
    class RoleNameHappyPathsTest {
        @Test
        void constructor_CreateRoleName_Success() {
            RoleName roleName = new RoleName("Admin");
            assertThat(roleName).isNotNull();
            assertThat(roleName.value()).isEqualTo("Admin");
        }

        @Test
        void constructor_CreateRoleName_TrimsWhitespace() {
            RoleName roleName = new RoleName("  Admin  ");
            assertThat(roleName.value()).isEqualTo("Admin");
        }

        @Test
        void constructor_CreateRoleName_MinLength() {
            RoleName roleName = new RoleName("AB");
            assertThat(roleName.value()).isEqualTo("AB");
        }

        @Test
        void constructor_CreateRoleName_MaxLength() {
            String maxName = "A".repeat(50);
            RoleName roleName = new RoleName(maxName);
            assertThat(roleName.value()).isEqualTo(maxName);
        }

        @Test
        void toString_RoleNameToString_Success() {
            RoleName roleName = new RoleName("Admin");
            assertThat(roleName.toString()).isEqualTo("Admin");
        }
    }

    @Nested
    class RoleNameValidationTests {

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"A", " A "})
        void constructor_CreateRoleName_FailIfTooShort(String value) {
            assertThatThrownBy(() -> new RoleName(value))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertHasFieldError(ex, "roleName");
                    });
        }

        @Test
        void constructor_CreateRoleName_FailIfTooLong() {
            String tooLongName = "A".repeat(51);
            assertThatThrownBy(() -> new RoleName(tooLongName))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertHasFieldError(ex, "roleName");
                    });
        }
    }
}
