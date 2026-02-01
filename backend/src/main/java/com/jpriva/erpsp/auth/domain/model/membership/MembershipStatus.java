package com.jpriva.erpsp.auth.domain.model.membership;

import com.jpriva.erpsp.auth.domain.constants.TenantMembershipValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;

public enum MembershipStatus {
    ACTIVE,
    SUSPENDED,
    REMOVED;


    public static MembershipStatus of(String status) {
        if (status == null || status.isBlank()) {
            throw new ErpAuthValidationException(
                    ValidationError.createSingle(TenantMembershipValidationError.STATUS_EMPTY)
            );
        }
        MembershipStatus membershipStatus;
        try {
            membershipStatus = MembershipStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ErpAuthValidationException(
                    ValidationError.createSingle(TenantMembershipValidationError.STATUS_NOT_FOUND)
            );
        }
        return membershipStatus;
    }
}
