package com.jpriva.erpsp.auth.domain.model.role;

import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;

class RoleIdTest {

    @Nested
    class HappyPathTests {
        @Test
        void constructor_CreateRoleId_Success() {
            UUID uuid = UUID.randomUUID();
            RoleId roleId = new RoleId(uuid);
            assertThat(roleId).isNotNull();
            assertThat(roleId.value()).isEqualTo(uuid);
        }

        @Test
        void from_CreateRoleIdFromString_Success() {
            UUID uuid = UUID.randomUUID();
            RoleId roleId = RoleId.from(uuid.toString());
            assertThat(roleId).isNotNull();
            assertThat(roleId.value()).isEqualTo(uuid);
        }

        @Test
        void generate_CreateRoleId_Success() {
            RoleId roleId = RoleId.generate();
            assertThat(roleId).isNotNull();
            assertThat(roleId.value()).isNotNull();
        }

        @Test
        void toString_RoleIdToString_Success() {
            UUID uuid = UUID.randomUUID();
            RoleId roleId = new RoleId(uuid);
            assertThat(roleId.toString()).isEqualTo(uuid.toString());
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void constructor_CreateRoleId_FailIfNullValue() {
            assertThatThrownBy(() -> new RoleId(null))
                    .isInstanceOf(ErpAuthValidationException.class)
                    .satisfies(exception -> {
                        ErpAuthValidationException ex = (ErpAuthValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "roleId");
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        void from_CreateRoleIdFromString_FailIfNullOrBlank(String value) {
            assertThatThrownBy(() -> RoleId.from(value))
                    .isInstanceOf(ErpAuthValidationException.class)
                    .satisfies(exception -> {
                        ErpAuthValidationException ex = (ErpAuthValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "roleId");
                    });
        }

        @Test
        void from_CreateRoleIdFromString_FailIfInvalidFormat() {
            assertThatThrownBy(() -> RoleId.from("invalid_format"))
                    .isInstanceOf(ErpAuthValidationException.class)
                    .satisfies(exception -> {
                        ErpAuthValidationException ex = (ErpAuthValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "roleId");
                    });
        }
    }
}
