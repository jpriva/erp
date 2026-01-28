package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BiometricTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {"FINGERPRINT", "FACE", "IRIS", "VOICE", "fingerprint", "Fingerprint"})
    void of_Success(String typeStr) {
        BiometricType type = BiometricType.of(typeStr);
        assertThat(type).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"INVALID", "RETINA"})
    void of_Fail(String typeStr) {
        assertThatThrownBy(() -> BiometricType.of(typeStr))
                .isInstanceOf(ErpValidationException.class)
                .satisfies(exception -> {
                    ErpValidationException ex = (ErpValidationException) exception;
                    ErpExceptionTestUtils.printExceptionDetails(ex);
                    assertThat(ex.getPlainErrors())
                            .containsKey("biometricType");
                });
    }
}
