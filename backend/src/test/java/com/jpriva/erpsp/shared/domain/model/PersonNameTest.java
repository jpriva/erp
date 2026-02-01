package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.utils.ErpExceptionTestUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.stream.Stream;

import static com.jpriva.erpsp.shared.domain.utils.ValidationErrorAssertions.assertHasFieldError;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PersonNameTest {

    static Stream<Arguments> provideNameCombinations() {
        String[] firstNames = {" John ", "John ", "\tJohn\n"};
        String[] lastNames = {" Doe ", "Doe ", "\tDoe\n"};

        return Stream.of(firstNames).flatMap(f ->
                Stream.of(lastNames).map(l -> Arguments.of(f, l))
        );
    }

    @Test
    void constructor_SuccessForNormalName() {
        PersonName name = new PersonName("John", "Doe");
        assertThat(name).isNotNull();
        assertThat(name.firstName()).isNotNull();
        assertThat(name.lastName()).isNotNull();
        assertThat(name.firstName()).isEqualTo("John");
        assertThat(name.lastName()).isEqualTo("Doe");
    }

    @ParameterizedTest
    @MethodSource("provideNameCombinations")
    void constructor_SuccessAndTrimName(String firstName, String lastName) {
        PersonName name = new PersonName(firstName, lastName);
        assertThat(name).isNotNull();
        assertThat(name.firstName()).isEqualTo("John");
        assertThat(name.lastName()).isEqualTo("Doe");
    }

    @Test
    void fullName_ShouldReturnFullName() {
        PersonName name = new PersonName("John", "Doe");
        assertThat(name).isNotNull();
        assertThat(name.fullName()).isEqualTo("John Doe");
    }

    @Nested
    class EmptyFieldsTest {

        @ParameterizedTest
        @NullAndEmptySource
        void constructor_ShouldThrowIfEmptyFirstName(String firstName) {
            assertThrows(ErpValidationException.class, () -> new PersonName(firstName, "Doe"));
            assertThatThrownBy(() -> new PersonName(firstName, "Doe"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "firstName");
                    });
        }

        @ParameterizedTest
        @NullAndEmptySource
        void constructor_ShouldThrowIfEmptyLastName(String lastName) {
            assertThatThrownBy(() -> new PersonName("John", lastName))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "lastName");
                    });
        }
    }

    @Nested
    class NameLengthTest {
        @Test
        void constructor_ShouldThrowIfFirstNameExceedsLength() {
            assertThatThrownBy(() -> new PersonName("a".repeat(101), "Doe"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "firstName");
                    });
        }

        @Test
        void constructor_ShouldThrowIfLastNameExceedsLength() {
            assertThatThrownBy(() -> new PersonName("John", "a".repeat(101)))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "lastName");
                    });
        }

        @Test
        void constructor_ShouldThrowIfFirstNameToShort() {
            assertThatThrownBy(() -> new PersonName("a", "Doe"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "firstName");
                    });
        }

        @Test
        void constructor_ShouldThrowIfLastNameToShort() {
            assertThatThrownBy(() -> new PersonName("John", "a"))
                    .isInstanceOf(ErpValidationException.class)
                    .satisfies(exception -> {
                        ErpValidationException ex = (ErpValidationException) exception;
                        ErpExceptionTestUtils.printExceptionDetails(ex);
                        assertHasFieldError(ex, "lastName");
                    });
        }
    }
}
