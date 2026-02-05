package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.constants.TenantValidationError;
import com.jpriva.erpsp.auth.domain.exceptions.ErpAuthValidationException;
import com.jpriva.erpsp.shared.domain.constants.AuthValidationError;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.TenantId;
import com.jpriva.erpsp.shared.domain.model.UserId;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Instant;
import java.util.UUID;

public class Tenant {

    private final TenantId tenantId;
    private final Instant createdAt;
    private UserId ownerId;
    private TenantName name;
    private TenantStatus status;

    public Tenant(TenantId tenantId, UserId ownerId, TenantName name, TenantStatus status, Instant createdAt) {
        var val = new ValidationError.Builder();
        if (tenantId == null) {
            val.addError(AuthValidationError.TENANT_ID_EMPTY);
        }
        if (ownerId == null) {
            val.addError(TenantValidationError.OWNER_ID_EMPTY);
        }
        if (name == null) {
            val.addError(TenantValidationError.NAME_EMPTY);
        }
        if (status == null) {
            val.addError(TenantValidationError.STATUS_EMPTY);
        }
        if (createdAt == null) {
            val.addError(TenantValidationError.CREATED_AT_EMPTY);
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        this.tenantId = tenantId;
        this.ownerId = ownerId;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Tenant create(UserId ownerId, String name) {
        var val = new ValidationError.Builder();
        TenantId tenantId = TenantId.generate();
        TenantName tenantName = null;
        try {
            tenantName = new TenantName(name);
        } catch (ErpValidationException ex) {
            val.addValidation(ex.getValidationErrors());
        }
        ValidationErrorUtils.validate(AuthErrorCode.AUTH_MODULE, val);
        return new Tenant(tenantId, ownerId, tenantName, TenantStatus.ACTIVE, Instant.now());
    }

    public static Tenant fromPersistence(UUID tenantId, UUID ownerId, String name, String status, Instant createdAt) {
        Tenant tenant;
        try {
            TenantStatus tenantStatus = TenantStatus.of(status);
            tenant = new Tenant(
                    new TenantId(tenantId),
                    new UserId(ownerId),
                    new TenantName(name),
                    tenantStatus,
                    createdAt
            );
        } catch (ErpValidationException ex) {
            throw new ErpPersistenceCompromisedException(AuthErrorCode.AUTH_MODULE, ex);
        }
        return tenant;
    }

    public void changeName(String name) {
        this.name = new TenantName(name);
    }

    public void suspend() {
        this.status = TenantStatus.SUSPENDED;
    }

    public void activate() {
        this.status = TenantStatus.ACTIVE;
    }

    public void markAsDeleted() {
        this.status = TenantStatus.DELETED;
    }

    public void transferOwnership(UserId newOwnerId) {
        var val = new ValidationError.Builder();
        if (newOwnerId == null) {
            throw new ErpAuthValidationException(val.addError(AuthValidationError.TENANT_ID_EMPTY).build());
        }
        this.ownerId = newOwnerId;
    }

    public boolean isActive() {
        return this.status == TenantStatus.ACTIVE;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public UserId getOwnerId() {
        return ownerId;
    }

    public TenantName getName() {
        return name;
    }

    public TenantStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
