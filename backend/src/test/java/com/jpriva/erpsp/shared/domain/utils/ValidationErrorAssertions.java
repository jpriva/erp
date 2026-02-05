package com.jpriva.erpsp.shared.domain.utils;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Assertion utilities for validation errors in tests.
 * Provides fluent assertions for the new ValidationError system.
 */
public class ValidationErrorAssertions {

    private ValidationErrorAssertions() {
    }

    /**
     * Asserts that the exception contains at least one error for the given field.
     *
     * @param ex        the ErpValidationException to check
     * @param fieldName the field name to look for
     */
    public static void assertHasFieldError(ErpValidationException ex, String fieldName) {
        assertThat(ex.getValidationErrors().errors())
                .extracting(ValidationErrorCode::getField)
                .contains(fieldName);
    }

    /**
     * Asserts that the exception contains the specific validation error code.
     *
     * @param ex            the ErpValidationException to check
     * @param expectedError the expected ValidationErrorCode
     */
    public static void assertHasError(ErpValidationException ex, ValidationErrorCode expectedError) {
        assertThat(ex.getValidationErrors().errors())
                .contains(expectedError);
    }

    /**
     * Asserts that the exception contains exactly one error for the given field.
     *
     * @param ex        the ErpValidationException to check
     * @param fieldName the field name to look for
     */
    public static void assertHasExactlyOneFieldError(ErpValidationException ex, String fieldName) {
        assertThat(ex.getValidationErrors().errors())
                .filteredOn(error -> error.getField().equals(fieldName))
                .hasSize(1);
    }

    /**
     * Asserts that the exception contains multiple specific errors.
     *
     * @param ex             the ErpValidationException to check
     * @param expectedErrors the expected ValidationErrorCodes
     */
    public static void assertHasErrors(ErpValidationException ex, ValidationErrorCode... expectedErrors) {
        assertThat(ex.getValidationErrors().errors())
                .contains(expectedErrors);
    }
}
