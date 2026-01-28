package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum TenantStatus {
    ACTIVE,
    SUSPENDED,
    DELETED;

    private static final String STATUS_NULL_ERROR = "Status can't be empty";
    private static final String STATUS_NOT_FOUND_ERROR = "Status doesn't exist";
    private static final String FIELD_STATUS = "status";

    public static TenantStatus of(String status) {
        var val = new ValidationError.Builder();
        if (status == null || status.isBlank()) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_STATUS, STATUS_NULL_ERROR).build()
            );
        }
        TenantStatus tenantStatus;
        try {
            tenantStatus = TenantStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpValidationException(
                    AuthErrorCode.AUTH_MODULE,
                    val.addError(FIELD_STATUS, STATUS_NOT_FOUND_ERROR).build()
            );
        }
        return tenantStatus;
    }
}
