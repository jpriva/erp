package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeviceIdTest {

    @Nested
    class HappyPathTests {
        @Test
        void constructor_CreateDeviceId_Success() {
            DeviceId deviceId = new DeviceId("device-abc-123");
            assertThat(deviceId).isNotNull();
            assertThat(deviceId.value()).isEqualTo("device-abc-123");
        }

        @Test
        void toString_DeviceIdToString_Success() {
            DeviceId deviceId = new DeviceId("device-abc-123");
            assertThat(deviceId.toString()).isEqualTo("device-abc-123");
        }

        @Test
        void constructor_CreateDeviceIdAtMaxLength_Success() {
            String maxLengthValue = "a".repeat(255);
            DeviceId deviceId = new DeviceId(maxLengthValue);
            assertThat(deviceId).isNotNull();
            assertThat(deviceId.value()).isEqualTo(maxLengthValue);
        }
    }

    @Nested
    class ValidationTests {
        @ParameterizedTest
        @NullAndEmptySource
        void constructor_CreateDeviceId_FailIfNullOrBlank(String value) {
            assertThatThrownBy(() -> new DeviceId(value))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("deviceId");
                    });
        }

        @Test
        void constructor_CreateDeviceId_FailIfExceedsMaxLength() {
            String tooLongValue = "a".repeat(256);
            assertThatThrownBy(() -> new DeviceId(tooLongValue))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertThat(ex.getModule()).isEqualTo("AUTH");
                        assertThat(ex.getCode().getCode()).isEqualTo("VALIDATION_ERROR");
                        assertThat(ex.getPlainErrors())
                                .containsKey("deviceId");
                    });
        }
    }
}
