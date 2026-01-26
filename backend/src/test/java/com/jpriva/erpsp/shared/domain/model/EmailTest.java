package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "test@example.com", "test@example.com.co",
            "test_1@example.com", "test-1@example.com"
    })
    void constructor_SuccessForNormalEmail(String value) {
        Email email = new Email(value);
        assertNotNull(email);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "  test@example.com", "test@example.com  ",
            "test@example.com\n", "  test@example.com  "
    })
    void constructor_SuccessShouldTrimEmail(String value) {
        Email email = new Email(value);
        assertNotNull(email);
        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {
            "test@example", "@example.com",
            "test@.com", "test@a.", "test@a.com."
    })
    void constructor_FailureForErrorEmail(String value) {
        assertThrows(ErpValidationException.class, () -> new Email(value));
    }

    @Test
    void constructor_FailureForLongString() {
        assertThrows(ErpValidationException.class, () -> new Email("a".repeat(243) + "@example.com"));
    }
}
