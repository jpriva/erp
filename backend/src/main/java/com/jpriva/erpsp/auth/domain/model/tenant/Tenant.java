package com.jpriva.erpsp.auth.domain.model.tenant;

import com.jpriva.erpsp.auth.domain.constants.AuthErrorCode;
import com.jpriva.erpsp.auth.domain.model.user.UserId;
import com.jpriva.erpsp.shared.domain.exceptions.ErpPersistenceCompromisedException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;
import com.jpriva.erpsp.shared.domain.model.ValidationError;
import com.jpriva.erpsp.shared.domain.utils.ValidationErrorUtils;

import java.time.Instant;
import java.util.UUID;

public class Tenant {
    private static final String TENANT_ID_NULL_ERROR = "Tenant ID can't be empty";
    private static final String OWNER_ID_NULL_ERROR = "Owner ID can't be empty";
    private static final String NAME_NULL_ERROR = "Name can't be empty";
    private static final String STATUS_NULL_ERROR = "Status can't be empty";
    private static final String CREATED_AT_NULL_ERROR = "Created at can't be empty";
    private static final String FIELD_TENANT_ID = "tenantId";
    private static final String FIELD_OWNER_ID = "ownerId";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_CREATED_AT = "createdAt";

    private final TenantId tenantId;
    private final UserId ownerId;
    private TenantName name;
    private TenantStatus status;
    private final Instant createdAt;

    public Tenant(TenantId tenantId, UserId ownerId, TenantName name, TenantStatus status, Instant createdAt) {
        var val = new ValidationError.Builder();
        if (tenantId == null) {
            val.addError(FIELD_TENANT_ID, TENANT_ID_NULL_ERROR);
        }
        if (ownerId == null) {
            val.addError(FIELD_OWNER_ID, OWNER_ID_NULL_ERROR);
        }
        if (name == null) {
            val.addError(FIELD_NAME, NAME_NULL_ERROR);
        }
        if (status == null) {
            val.addError(FIELD_STATUS, STATUS_NULL_ERROR);
        }
        if (createdAt == null) {
            val.addError(FIELD_CREATED_AT, CREATED_AT_NULL_ERROR);
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
