package com.jpriva.erpsp.shared.domain.model;

import com.jpriva.erpsp.shared.domain.exceptions.ErpErrorCodes;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

public record PersonName(
        String firstName,
        String lastName
) {

    public PersonName {
        var val = ValidationError.builder();
        if (firstName == null) firstName = "";
        if (lastName == null) lastName = "";
        firstName = firstName.trim();
        lastName = lastName.trim();
        if (firstName.length() < 2) {
            val.addError(ErpValidationError.FIRST_NAME_MIN_LENGTH);
        }
        if (firstName.length() > 100) {
            val.addError(ErpValidationError.FIRST_NAME_MAX_LENGTH);
        }
        if (lastName.length() < 2) {
            val.addError(ErpValidationError.LAST_NAME_MIN_LENGTH);
        }
        if (lastName.length() > 100) {
            val.addError(ErpValidationError.LAST_NAME_MAX_LENGTH);
        }
        ValidationErrorUtils.validate(ErpErrorCodes.SHARED_MODULE, val);
    }

    public String fullName() {
        return String.format("%s %s", firstName, lastName).trim();
    }

}
