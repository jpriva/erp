package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpErrorCodes;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

public record PersonName(
        String firstName,
        String lastName
) {
    public static final String FIELD_FIRSTNAME = "firstName";
    public static final String FIELD_LASTNAME = "lastName";
    public PersonName {
        var val = ValidationError.builder();
        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";
        firstName = firstName.trim();
        lastName = lastName.trim();
        if (firstName.length()<2) {
            val.addError(
                    FIELD_FIRSTNAME,
                    ValidationErrorUtils.errorGreaterOrEqualThan("First name", 2, "characters"))
            ;
        }
        if (firstName.length()>100) {
            val.addError(
                    FIELD_FIRSTNAME,
                    ValidationErrorUtils.errorLessOrEqualThan("First name", 100, "characters")
            );
        }
        if (lastName.length()<2) {
            val.addError(
                    FIELD_LASTNAME,
                    ValidationErrorUtils.errorGreaterOrEqualThan("Last name", 2, "characters")
            );
        }
        if (lastName.length()>100) {
            val.addError(
                    FIELD_LASTNAME,
                    ValidationErrorUtils.errorLessOrEqualThan("Last name", 100, "characters")
            );
        }
        ValidationErrorUtils.validate(ErpErrorCodes.SHARED_MODULE, val);
    }

    public String fullName() {
        return String.format("%s %s", firstName, lastName);
    }

}
