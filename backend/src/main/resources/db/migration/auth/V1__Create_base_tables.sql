CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE auth.users
(
    user_id    UUID PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name  VARCHAR(100) NOT NULL,
    status     VARCHAR(20)  NOT NULL CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_email ON auth.users (email);
CREATE INDEX idx_user_status ON auth.users (status);

CREATE TABLE auth.tenants
(
    tenant_id  UUID PRIMARY KEY,
    owner_id   UUID         NOT NULL REFERENCES auth.users (user_id),
    name       VARCHAR(100) NOT NULL,
    status     VARCHAR(20)  NOT NULL CHECK (status IN ('ACTIVE', 'SUSPENDED', 'DELETED')),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_tenant_owner_id ON auth.tenants (owner_id);
CREATE INDEX idx_tenant_status ON auth.tenants (status);

CREATE TABLE auth.roles
(
    role_id    UUID PRIMARY KEY,
    tenant_id  UUID        NOT NULL REFERENCES auth.tenants (tenant_id) ON DELETE CASCADE,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_role_tenant_name UNIQUE (tenant_id, name)
);

CREATE INDEX idx_role_tenant_id ON auth.roles (tenant_id);

CREATE TABLE auth.credentials
(
    credential_id UUID PRIMARY KEY,
    user_id       UUID        NOT NULL REFERENCES auth.users (user_id) ON DELETE CASCADE,
    type          VARCHAR(20) NOT NULL CHECK (type IN ('PASSWORD', 'OPENID')),
    status        VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'DISABLED', 'EXPIRED', 'COMPROMISED')),
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_used_at  TIMESTAMP,

    password_hash VARCHAR(255),

    provider      VARCHAR(50),
    subject       VARCHAR(255),

    CONSTRAINT uk_openid_type_user_id_provider_subject UNIQUE (type, user_id, provider, subject)
);

CREATE INDEX idx_credential_user_id ON auth.credentials (user_id);
CREATE INDEX idx_credential_status ON auth.credentials (status);
CREATE INDEX idx_credential_type ON auth.credentials (type);

CREATE TABLE auth.tenant_memberships
(
    membership_id UUID PRIMARY KEY,
    user_id       UUID        NOT NULL,
    tenant_id     UUID        NOT NULL,
    status        VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'SUSPENDED', 'REMOVED')),
    joined_at     TIMESTAMP   NOT NULL,
    invited_by    UUID        NOT NULL,
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tm_user_id FOREIGN KEY (user_id) REFERENCES auth.users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_tenant_id FOREIGN KEY (tenant_id) REFERENCES auth.tenants (tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_invited_by FOREIGN KEY (invited_by) REFERENCES auth.users (user_id),
    CONSTRAINT uk_user_tenant UNIQUE (user_id, tenant_id)
);

CREATE INDEX idx_tm_user_id ON auth.tenant_memberships (user_id);
CREATE INDEX idx_tm_tenant_id ON auth.tenant_memberships (tenant_id);
CREATE INDEX idx_tm_status ON auth.tenant_memberships (status);
CREATE INDEX idx_tm_joined_at ON auth.tenant_memberships (joined_at);

CREATE TABLE auth.membership_roles
(
    membership_id UUID        NOT NULL,
    role_id       UUID        NOT NULL,
    role_name     VARCHAR(50) NOT NULL,
    assigned_at   TIMESTAMP   NOT NULL,
    assigned_by   UUID        NOT NULL,
    created_at    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_membership_roles PRIMARY KEY (membership_id, role_id),
    CONSTRAINT fk_mr_membership_id FOREIGN KEY (membership_id) REFERENCES auth.tenant_memberships (membership_id) ON DELETE CASCADE,
    CONSTRAINT fk_mr_role_id FOREIGN KEY (role_id) REFERENCES auth.roles (role_id),
    CONSTRAINT fk_mr_assigned_by FOREIGN KEY (assigned_by) REFERENCES auth.users (user_id)
);

CREATE INDEX idx_mr_role_id ON auth.membership_roles (role_id);
CREATE INDEX idx_mr_assigned_by ON auth.membership_roles (assigned_by);
CREATE INDEX idx_mr_assigned_at ON auth.membership_roles (assigned_at);

CREATE TABLE auth.invitations
(
    invitation_id UUID PRIMARY KEY,
    tenant_id     UUID         NOT NULL,
    email         VARCHAR(255) NOT NULL,
    invited_by    UUID         NOT NULL,
    status        VARCHAR(20)  NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED', 'EXPIRED')),
    token         VARCHAR(128) NOT NULL UNIQUE,
    created_at    TIMESTAMP    NOT NULL,
    expires_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_tenant_id FOREIGN KEY (tenant_id) REFERENCES auth.tenants (tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_invited_by FOREIGN KEY (invited_by) REFERENCES auth.users (user_id)
);

CREATE UNIQUE INDEX uk_pending_invitation
    ON auth.invitations (tenant_id, email)
    WHERE status = 'PENDING';

CREATE INDEX idx_inv_token ON auth.invitations (token);
CREATE INDEX idx_inv_email ON auth.invitations (email);
CREATE INDEX idx_inv_tenant_status ON auth.invitations (tenant_id, status);
CREATE INDEX idx_inv_created_at ON auth.invitations (created_at);
CREATE INDEX idx_inv_expires_at ON auth.invitations (expires_at);

CREATE TABLE auth.invitation_roles
(
    invitation_id UUID      NOT NULL,
    role_id       UUID      NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_invitation_roles PRIMARY KEY (invitation_id, role_id),
    CONSTRAINT fk_ir_invitation_id FOREIGN KEY (invitation_id) REFERENCES auth.invitations (invitation_id) ON DELETE CASCADE,
    CONSTRAINT fk_ir_role_id FOREIGN KEY (role_id) REFERENCES auth.roles (role_id)
);

CREATE INDEX idx_ir_role_id ON auth.invitation_roles (role_id);
