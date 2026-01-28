package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BiometricTemplateIdTest {

    @Nested
    class HappyPathTests {
        @Test
        void constructor_CreateBiometricTemplateId_Success() {
            BiometricTemplateId templateId = new BiometricTemplateId("template-123456");
            assertThat(templateId).isNotNull();
            assertThat(templateId.value()).isEqualTo("template-123456");
        }

        @Test
        void toString_BiometricTemplateIdToString_Success() {
            BiometricTemplateId templateId = new BiometricTemplateId("template-123456");
            assertThat(templateId.toString()).isEqualTo("template-123456");
        }

        @Test
        void constructor_CreateBiometricTemplateIdAtMaxLength_Success() {
            String maxLengthValue = "a".repeat(255);
            BiometricTemplateId templateId = new BiometricTemplateId(maxLengthValue);
            assertThat(templateId).isNotNull();
            assertThat(templateId.value()).isEqualTo(maxLengthValue);
        }
    }

    @Nested
    class ValidationTests {
        @ParameterizedTest
        @NullAndEmptySource
        void constructor_CreateBiometricTemplateId_FailIfNullOrBlank(String value) {
            assertThatThrownBy(() -> new BiometricTemplateId(value))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("biometricTemplateId");
                    });
        }

        @Test
        void constructor_CreateBiometricTemplateId_FailIfExceedsMaxLength() {
            String tooLongValue = "a".repeat(256);
            assertThatThrownBy(() -> new BiometricTemplateId(tooLongValue))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("biometricTemplateId");
                    });
        }
    }
}
