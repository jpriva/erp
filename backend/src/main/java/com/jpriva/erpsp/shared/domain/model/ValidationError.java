package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpImplementationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record ValidationError(Map<String, List<String>> errors) {

    private static final String EMPTY_FIELD = "Field can't be null or empty";
    private static final String EMPTY_ERROR = "Error can't be null or empty";

    public ValidationError {
        if (errors == null) {
            errors = Map.of();
        } else {
            errors = errors.entrySet().stream()
                    .collect(Collectors.toUnmodifiableMap(
                            Map.Entry::getKey,
                            entry -> List.copyOf(entry.getValue())
                    ));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ValidationError prototype) {
        return new Builder(prototype);
    }

    public static class Builder {
        private final Map<String, List<String>> tempErrors = new HashMap<>();

        public Builder() {
        }

        public Builder(ValidationError prototype) {
            addValidation(prototype);
        }

        public Builder addValidation(ValidationError prototype){
            if (prototype != null) {
                prototype.errors().forEach((field, messages) ->
                        this.tempErrors.put(field, new ArrayList<>(messages))
                );
            }
            return this;
        }

        public Builder addError(String field, String error) {
            if (field == null || field.isBlank()) {
                throw new ErpImplementationException(EMPTY_FIELD);
            }
            if (error == null || error.isBlank()) {
                throw new ErpImplementationException(EMPTY_ERROR);
            }
            tempErrors.computeIfAbsent(field, _ ->
                    new ArrayList<>()
            ).add(error);
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
