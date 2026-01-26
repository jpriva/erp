package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpImplementationException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidationErrorTest {

    @Nested
    class ConstructorTests {
        @Test
        void constructor_WithNullErrors_ShouldInitializeEmpty() {
            ValidationError validationError = new ValidationError(null);
            assertThat(validationError.errors()).isNotNull();
            assertThat(validationError.errors()).isEmpty();
        }

        @Test
        void error_ShouldBeImmutable() {
            List<String> fieldErrors = new ArrayList<>();
            fieldErrors.add("error1");
            fieldErrors.add("error2");
            Map<String, List<String>> errors = new HashMap<>();
            errors.put("field1", fieldErrors);
            ValidationError validationError = new ValidationError(errors);
            assertThatThrownBy(() ->
                    validationError.errors()
                            .put("field2", fieldErrors)
            ).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() ->
                    validationError.errors()
                            .get("field1").add("error3")
            ).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    class BuilderTests {
        @Test
        void addError_ShouldAccumulateErrorsWithSameField() {
            var validateBuilder = ValidationError.builder();
            validateBuilder.addError("field1", "error1");
            validateBuilder.addError("field1", "error2");
            ValidationError validationError = validateBuilder.build();
            assertThat(validationError.errors()).hasSize(1);
            assertThat(validationError.errors()).containsKey("field1");
            assertThat(validationError.errors().get("field1")).hasSize(2);
            assertThat(validationError.errors().get("field1")).containsExactly("error1", "error2");
        }

        @Test
        void addError_ShouldAccumulateErrorsWithDifferentFields() {
            var validateBuilder = ValidationError.builder();
            validateBuilder.addError("field1", "error1");
            validateBuilder.addError("field2", "error2");
            ValidationError validationError = validateBuilder.build();
            assertThat(validationError.errors()).hasSize(2);
            assertThat(validationError.errors()).containsKey("field1");
            assertThat(validationError.errors()).containsKey("field2");
            assertThat(validationError.errors().get("field1")).hasSize(1);
            assertThat(validationError.errors().get("field2")).hasSize(1);

        }

        @ParameterizedTest
        @NullAndEmptySource
        void addError_ShouldNotAllowEmptyValues(String value) {
            var validateBuilder = ValidationError.builder();
            assertThatThrownBy(() ->
                    validateBuilder.addError(value, "error1")
            ).isInstanceOf(ErpImplementationException.class);
            assertThatThrownBy(() ->
                    validateBuilder.addError("field1", value)
            ).isInstanceOf(ErpImplementationException.class);
        }

        @Test
        void hasErrors_ShouldBeTrueWhenHasErrors(){
            var validateBuilder = ValidationError.builder();
            validateBuilder.addError("field1", "error1");
            assertThat(validateBuilder.hasErrors()).isTrue();
        }

        @Test
        void hasErrors_ShouldBeFalseWhenErrorsIsEmpty(){
            var validateBuilder = ValidationError.builder();
            assertThat(validateBuilder.hasErrors()).isFalse();
        }

        @Test
        void build_ShouldReturnValidationError(){
            var validateBuilder = ValidationError.builder();
            validateBuilder.addError("field1", "error1");
            ValidationError validationError = validateBuilder.build();
            assertThat(validationError).isNotNull();
            assertThat(validationError).isInstanceOf(ValidationError.class);
        }

        @Test
        void build_ShouldReturnValidationErrorWithEmptyMapWhenBuildWithoutErrors(){
            ValidationError validationError = ValidationError.builder().build();
            assertThat(validationError).isNotNull();
            assertThat(validationError.errors()).isNotNull();
            assertThat(validationError.errors().isEmpty()).isTrue();
        }
    }

    @Nested
    class BuilderPrototypeTests {
        @Test
        void builderWithPrototype_ShouldCopyExistingErrors(){
            ValidationError validation = ValidationError.builder()
                    .addError("field1", "error1")
                    .addError("field2", "error2")
                    .build();
            ValidationError validationError = ValidationError.builder(validation)
                    .addError("field3", "error3")
                    .build();
            assertThat(validationError).isNotNull();
            assertThat(validationError.errors()).hasSize(3);
            assertThat(validationError.errors()).containsEntry("field1", List.of("error1"));
            assertThat(validationError.errors()).containsEntry("field2", List.of("error2"));
            assertThat(validationError.errors()).containsEntry("field3", List.of("error3"));
        }

        @Test
        void buildWithPrototype_ShouldHandleNullPrototype(){
            ValidationError validationError = ValidationError.builder(null).build();
            assertThat(validationError).isNotNull();
            assertThat(validationError.errors()).isNotNull();
            assertThat(validationError.errors().isEmpty()).isTrue();
        }
    }
}
