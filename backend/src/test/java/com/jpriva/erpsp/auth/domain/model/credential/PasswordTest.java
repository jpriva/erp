package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.auth.domain.model.utils.PasswordTestUtils;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {

    @Nested
    class CreateTests {
        @Test
        void create_CreatePassword_Success() {
            Password password = Password.create("validPassword123", PasswordTestUtils.fakeHasher);
            assertThat(password).isNotNull();
            assertThat(password.hash()).isEqualTo("ENCODED_validPassword123");
        }

        @Test
        void create_CreatePasswordWithMinLength_Success() {
            String minLengthPassword = "a".repeat(8);
            Password password = Password.create(minLengthPassword, PasswordTestUtils.fakeHasher);
            assertThat(password).isNotNull();
        }

        @Test
        void create_CreatePasswordWithMaxLength_Success() {
            String maxLengthPassword = "a".repeat(128);
            Password password = Password.create(maxLengthPassword, PasswordTestUtils.fakeHasher);
            assertThat(password).isNotNull();
        }
    }

    @Nested
    class FromPersistenceTests {
        @Test
        void fromPersistence_CreatePasswordFromHash_Success() {
            Password password = Password.fromPersistence("stored_hash_value");
            assertThat(password).isNotNull();
            assertThat(password.hash()).isEqualTo("stored_hash_value");
        }
    }

    @Nested
    class MatchesTests {
        @Test
        void matches_ReturnsTrue_WhenPasswordMatches() {
            Password password = Password.create("validPassword123", PasswordTestUtils.fakeHasher);
            assertThat(password.matches("validPassword123", PasswordTestUtils.fakeHasher)).isTrue();
        }

        @Test
        void matches_ReturnsFalse_WhenPasswordDoesNotMatch() {
            Password password = Password.create("validPassword123", PasswordTestUtils.fakeHasher);
            assertThat(password.matches("wrongPassword", PasswordTestUtils.fakeHasher)).isFalse();
        }

        @ParameterizedTest
        @NullAndEmptySource
        void matches_ReturnsFalse_WhenRawPasswordNullOrBlank(String rawPassword) {
            Password password = Password.create("validPassword123", PasswordTestUtils.fakeHasher);
            assertThat(password.matches(rawPassword, PasswordTestUtils.fakeHasher)).isFalse();
        }
    }

    @Nested
    class ValidationTests {
        @ParameterizedTest
        @NullAndEmptySource
        void create_FailIfNullOrBlank(String rawPassword) {
            assertThatThrownBy(() -> Password.create(rawPassword, PasswordTestUtils.fakeHasher))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("password");
                    });
        }

        @ParameterizedTest
        @ValueSource(strings = {"short", "1234567"})
        void create_FailIfTooShort(String rawPassword) {
            assertThatThrownBy(() -> Password.create(rawPassword, PasswordTestUtils.fakeHasher))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("password");
                    });
        }

        @Test
        void create_FailIfTooLong() {
            String tooLongPassword = "a".repeat(129);
            assertThatThrownBy(() -> Password.create(tooLongPassword, PasswordTestUtils.fakeHasher))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("password");
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        void constructor_FailIfHashNullOrBlank(String hash) {
            assertThatThrownBy(() -> new Password(hash))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("password");
                    });
        }
    }
}
