package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CredentialStatusTest {

    @ParameterizedTest
    @ValueSource(strings = {"ACTIVE", "DISABLED", "EXPIRED", "COMPROMISED", "active", "Active"})
    void of_Success(String statusStr) {
        CredentialStatus status = CredentialStatus.of(statusStr);
        assertThat(status).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"INVALID", "OTHER"})
    void of_Fail(String statusStr) {
        assertThatThrownBy(() -> CredentialStatus.of(statusStr))
                .isInstanceOf(ErpValidationException.class)
                .satisfies(exception -> {
                    ErpValidationException ex = (ErpValidationException) exception;
                    ErpExceptionTestUtils.printExceptionDetails(ex);
                    assertThat(ex.getPlainErrors())
                            .containsKey("credentialStatus");
                });
    }
}
