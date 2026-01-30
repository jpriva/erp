-- Create tenant_memberships table
-- Represents user membership in a tenant with support for multiple statuses
CREATE TABLE tenant_memberships (
    membership_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'SUSPENDED', 'REMOVED')),
    joined_at TIMESTAMP NOT NULL,
    invited_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tm_user_id FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_tm_invited_by FOREIGN KEY (invited_by) REFERENCES users(user_id),
    CONSTRAINT uk_user_tenant UNIQUE(user_id, tenant_id)
);

CREATE INDEX idx_tm_user_id ON tenant_memberships(user_id);
CREATE INDEX idx_tm_tenant_id ON tenant_memberships(tenant_id);
CREATE INDEX idx_tm_status ON tenant_memberships(status);
CREATE INDEX idx_tm_joined_at ON tenant_memberships(joined_at);

-- Create membership_roles table
-- Denormalized role assignments within a membership
-- Includes role metadata and audit information (assigned_at, assigned_by)
CREATE TABLE membership_roles (
    membership_id UUID NOT NULL,
    role_id UUID NOT NULL,
    role_name VARCHAR(50) NOT NULL,
    assigned_at TIMESTAMP NOT NULL,
    assigned_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_membership_roles PRIMARY KEY (membership_id, role_id),
    CONSTRAINT fk_mr_membership_id FOREIGN KEY (membership_id) REFERENCES tenant_memberships(membership_id) ON DELETE CASCADE,
    CONSTRAINT fk_mr_role_id FOREIGN KEY (role_id) REFERENCES roles(role_id),
    CONSTRAINT fk_mr_assigned_by FOREIGN KEY (assigned_by) REFERENCES users(user_id)
);

CREATE INDEX idx_mr_role_id ON membership_roles(role_id);
CREATE INDEX idx_mr_assigned_by ON membership_roles(assigned_by);
CREATE INDEX idx_mr_assigned_at ON membership_roles(assigned_at);

-- Create invitations table
-- Manages the lifecycle of tenant invitations with secure tokens
CREATE TABLE invitations (
    invitation_id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL,
    invited_by UUID NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED', 'CANCELLED', 'EXPIRED')),
    token VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inv_tenant_id FOREIGN KEY (tenant_id) REFERENCES tenants(tenant_id) ON DELETE CASCADE,
    CONSTRAINT fk_inv_invited_by FOREIGN KEY (invited_by) REFERENCES users(user_id)
);

CREATE UNIQUE INDEX uk_pending_invitation
    ON invitations(tenant_id, email)
    WHERE status = 'PENDING';

CREATE INDEX idx_inv_token ON invitations(token);
CREATE INDEX idx_inv_email ON invitations(email);
CREATE INDEX idx_inv_tenant_status ON invitations(tenant_id, status);
CREATE INDEX idx_inv_created_at ON invitations(created_at);
CREATE INDEX idx_inv_expires_at ON invitations(expires_at);

-- Create invitation_roles table
-- Tracks which roles are assigned by an invitation
CREATE TABLE invitation_roles (
    invitation_id UUID NOT NULL,
    role_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_invitation_roles PRIMARY KEY (invitation_id, role_id),
    CONSTRAINT fk_ir_invitation_id FOREIGN KEY (invitation_id) REFERENCES invitations(invitation_id) ON DELETE CASCADE,
    CONSTRAINT fk_ir_role_id FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

CREATE INDEX idx_ir_role_id ON invitation_roles(role_id);
