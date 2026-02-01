package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.auth.domain.constants.TenantValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum TenantStatus {
    ACTIVE,
    SUSPENDED,
    DELETED;

    public static TenantStatus of(String status) {
        var val = new ValidationError.Builder();
        if (status == null || status.isBlank()) {
            throw new ErpAuthValidationException(
                    val.addError(TenantValidationError.STATUS_EMPTY).build()
            );
        }
        TenantStatus tenantStatus;
        try {
            tenantStatus = TenantStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    val.addError(TenantValidationError.STATUS_NOT_FOUND).build()
            );
        }
        return tenantStatus;
    }
}
