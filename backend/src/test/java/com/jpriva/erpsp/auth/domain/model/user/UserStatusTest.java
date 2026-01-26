package com.jpriva.erpsp.auth.domain.model.user;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserStatusTest {
    @ParameterizedTest
    @ValueSource(strings = {"ACTIVE", "BLOCKED", "EMAIL_NOT_VERIFIED"})
    void of_Success(String statusStr) {
        UserStatus status = UserStatus.of(statusStr);
        assertThat(status).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"OTRO"})
    void of_Fail(String statusStr){
        assertThatThrownBy(() -> UserStatus.of(statusStr))
                .isInstanceOf(ErpValidationException.class)
                .satisfies(exception -> {
                    ErpValidationException ex = (ErpValidationException) exception;
                    ErpExceptionTestUtils.printExceptionDetails(ex);
                    assertThat(ex.getPlainErrors())
                            .containsKey("status");
                });
    }
}
