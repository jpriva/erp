package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;

class TenantStatusTest {

    @ParameterizedTest
    @ValueSource(strings = {"ACTIVE", "SUSPENDED", "DELETED", "active", "suspended", "deleted"})
    void of_Success(String statusStr) {
        TenantStatus status = TenantStatus.of(statusStr);
        assertThat(status).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"INVALID", "OTHER"})
    void of_Fail(String statusStr) {
        assertThatThrownBy(() -> TenantStatus.of(statusStr))
                .isInstanceOf(ErpValidationException.class)
                .satisfies(exception -> {
                    ErpValidationException ex = (ErpValidationException) exception;
                    ErpExceptionTestUtils.printExceptionDetails(ex);
                    assertHasFieldError(ex, "status");
                });
    }
}
