package com.jpriva.erpsp.auth.domain.model.credential;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;

class OpenIdProviderTest {

    @ParameterizedTest
    @ValueSource(strings = {"GOOGLE", "GITHUB", "MICROSOFT", "APPLE", "google", "Google"})
    void of_Success(String providerStr) {
        OpenIdProvider provider = OpenIdProvider.of(providerStr);
        assertThat(provider).isNotNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"INVALID", "FACEBOOK"})
    void of_Fail(String providerStr) {
        assertThatThrownBy(() -> OpenIdProvider.of(providerStr))
                .isInstanceOf(ErpValidationException.class)
                .satisfies(exception -> {
                    ErpValidationException ex = (ErpValidationException) exception;
                    ErpExceptionTestUtils.printExceptionDetails(ex);
                    assertHasFieldError(ex, "openIdProvider");
                });
    }
}
