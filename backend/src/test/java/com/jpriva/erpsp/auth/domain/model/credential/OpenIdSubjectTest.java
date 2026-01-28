package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenIdSubjectTest {

    @Nested
    class HappyPathTests {
        @Test
        void constructor_CreateOpenIdSubject_Success() {
            OpenIdSubject subject = new OpenIdSubject("google-user-123456");
            assertThat(subject).isNotNull();
            assertThat(subject.value()).isEqualTo("google-user-123456");
        }

        @Test
        void toString_OpenIdSubjectToString_Success() {
            OpenIdSubject subject = new OpenIdSubject("google-user-123456");
            assertThat(subject.toString()).isEqualTo("google-user-123456");
        }

        @Test
        void constructor_CreateOpenIdSubjectAtMaxLength_Success() {
            String maxLengthValue = "a".repeat(255);
            OpenIdSubject subject = new OpenIdSubject(maxLengthValue);
            assertThat(subject).isNotNull();
            assertThat(subject.value()).isEqualTo(maxLengthValue);
        }
    }

    @Nested
    class ValidationTests {
        @ParameterizedTest
        @NullAndEmptySource
        void constructor_CreateOpenIdSubject_FailIfNullOrBlank(String value) {
            assertThatThrownBy(() -> new OpenIdSubject(value))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("openIdSubject");
                    });
        }

        @Test
        void constructor_CreateOpenIdSubject_FailIfExceedsMaxLength() {
            String tooLongValue = "a".repeat(256);
            assertThatThrownBy(() -> new OpenIdSubject(tooLongValue))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("openIdSubject");
                    });
        }
    }
}
