package com.jpriva.erpsp.auth.domain.model.user;

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

class UserIdTest {

    @Nested
    class UserIdHappyPathsTest {
        @Test
        void constructor_CreateUserId_Success() {
            UUID uuid = UUID.randomUUID();
            UserId userId = new UserId(uuid);
            assertThat(userId).isNotNull();
            assertThat(userId.value()).isEqualTo(uuid);
        }

        @Test
        void from_CreateUserIdFromString_Success() {
            UUID uuid = UUID.randomUUID();
            UserId userId = UserId.from(uuid.toString());
            assertThat(userId).isNotNull();
            assertThat(userId.value()).isEqualTo(uuid);
        }

        @Test
        void generate_CreateUserId_Success() {
            UserId userId = UserId.generate();
            assertThat(userId).isNotNull();
            assertThat(userId.value()).isNotNull();
        }

        @Test
        void toString_UserIdToString_Success() {
            UUID uuid = UUID.randomUUID();
            UserId userId = new UserId(uuid);
            assertThat(userId.toString()).isEqualTo(uuid.toString());
        }
    }

    @Nested
    class UserValidationTests {

        @Test
        void constructor_CreateUserId_FailIfNullValue() {
            assertThatThrownBy(() -> new UserId(null))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertHasFieldError(ex, "userId");
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        void from_CreateUserIdFromString_FailIfNullOrBlank(String value) {
            assertThatThrownBy(() -> UserId.from(value))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertHasFieldError(ex, "userId");
                    });
        }

        @Test
        void from_CreateUserIdFromString_FailIfInvalidFormat() {
            assertThatThrownBy(() -> UserId.from("invalid_format"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertHasFieldError(ex, "userId");
                    });
        }
    }

}
