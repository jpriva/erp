package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CredentialTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {"PASSWORD", "OPENID", "password", "Password"})
    void of_Success(String typeStr) {
        CredentialType type = CredentialType.of(typeStr);
        assertThat(type).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"INVALID", "OTHER"})
    void of_Fail(String typeStr) {
        assertThatThrownBy(() -> CredentialType.of(typeStr))
                .isInstanceOf(ErpValidationException.class)
                .satisfies(exception -> {
                    ErpValidationException ex = (ErpValidationException) exception;
                    ErpExceptionTestUtils.printExceptionDetails(ex);
                    assertHasFieldError(ex, "type");
                });
    }
}
