package com.jpriva.erpsp.auth.domain.model.invitation;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum InvitationStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED,
    EXPIRED;

    private static final String STATUS_NULL_ERROR = "Status can't be empty";
    private static final String STATUS_NOT_FOUND_ERROR = "Status doesn't exist";
    private static final String FIELD_STATUS = "invitationStatus";

    public static InvitationStatus of(String status) {
        var val = new ValidationError.Builder();
        if (status == null || status.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_STATUS, STATUS_NULL_ERROR).build()
            );
        }
        InvitationStatus invitationStatus;
        try {
            invitationStatus = InvitationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_STATUS, STATUS_NOT_FOUND_ERROR).build()
            );
        }
        return invitationStatus;
    }
}
