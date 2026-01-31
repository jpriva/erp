package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpImplementationException;
import com.jpriva.erpsp.shared.domain.exceptions.ValidationErrorCode;

import java.util.ArrayList;
import java.util.List;

public record ValidationError(List<ValidationErrorCode> errors) {

    private static final String VALIDATION_ERROR_CODE = "ValidationErrorCode cannot be empty";

    public ValidationError {
        errors = (errors == null) ? List.of() : List.copyOf(errors);
    }

    public static ValidationError createSingle(ValidationErrorCode error) {
        if (error == null) throw new ErpImplementationException(VALIDATION_ERROR_CODE);
        return new ValidationError(List.of(error));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<ValidationErrorCode> tempErrors = new ArrayList<>();

        public Builder() {
        }

        public Builder(ValidationErrorCode error) {
            if (error == null) throw new ErpImplementationException(VALIDATION_ERROR_CODE);
            tempErrors.add(error);
        }

        public Builder(List<ValidationErrorCode> errors) {
            if (errors != null && !errors.isEmpty())
                tempErrors.addAll(errors);
        }

        public Builder addError(ValidationErrorCode error) {
            if (error == null) throw new ErpImplementationException(VALIDATION_ERROR_CODE);
            this.tempErrors.add(error);
            return this;
        }

        public Builder addValidation(ValidationError prototype) {
            if (prototype != null) {
                this.tempErrors.addAll(prototype.errors());
            }
            return this;
        }

        public boolean hasErrors() {
            return !tempErrors.isEmpty();
        }

        public ValidationError build() {
            return new ValidationError(tempErrors);
        }
    }
}
