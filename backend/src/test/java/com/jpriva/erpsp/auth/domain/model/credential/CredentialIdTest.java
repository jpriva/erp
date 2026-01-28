package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CredentialIdTest {

    @Nested
    class HappyPathTests {
        @Test
        void constructor_CreateCredentialId_Success() {
            UUID uuid = UUID.randomUUID();
            CredentialId credentialId = new CredentialId(uuid);
            assertThat(credentialId).isNotNull();
            assertThat(credentialId.value()).isEqualTo(uuid);
        }

        @Test
        void from_CreateCredentialIdFromString_Success() {
            UUID uuid = UUID.randomUUID();
            CredentialId credentialId = CredentialId.from(uuid.toString());
            assertThat(credentialId).isNotNull();
            assertThat(credentialId.value()).isEqualTo(uuid);
        }

        @Test
        void generate_CreateCredentialId_Success() {
            CredentialId credentialId = CredentialId.generate();
            assertThat(credentialId).isNotNull();
            assertThat(credentialId.value()).isNotNull();
        }

        @Test
        void toString_CredentialIdToString_Success() {
            UUID uuid = UUID.randomUUID();
            CredentialId credentialId = new CredentialId(uuid);
            assertThat(credentialId.toString()).isEqualTo(uuid.toString());
        }
    }

    @Nested
    class ValidationTests {

        @Test
        void constructor_CreateCredentialId_FailIfNullValue() {
            assertThatThrownBy(() -> new CredentialId(null))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("credentialId");
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        void from_CreateCredentialIdFromString_FailIfNullOrBlank(String value) {
            assertThatThrownBy(() -> CredentialId.from(value))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("credentialId");
                    });
        }

        @Test
        void from_CreateCredentialIdFromString_FailIfInvalidFormat() {
            assertThatThrownBy(() -> CredentialId.from("invalid_format"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode()).isNotNull();
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("credentialId");
                    });
        }
    }
}
