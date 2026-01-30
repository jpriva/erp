# Tenant Membership & Invitations API Endpoints

## Base URL
```
/api/v1
```

## Authentication
Use header: `X-Current-User-ID: {user-uuid}`

---

## Tenant Members Management

### Get all members of a tenant
```
GET /tenants/{tenantId}/members
```

**Response:** Array of TenantMembershipDto

---

### Get active members of a tenant
```
GET /tenants/{tenantId}/members/active
```

**Response:** Array of TenantMembershipDto (with status=ACTIVE only)

---

### Get specific member details
```
GET /tenants/{tenantId}/members/{userId}
```

**Response:** TenantMembershipDto

**Example:**
```json
{
  "membership_id": "550e8400-e29b-41d4-a716-446655440000",
  "user_id": "550e8400-e29b-41d4-a716-446655440001",
  "tenant_id": "550e8400-e29b-41d4-a716-446655440002",
  "status": "ACTIVE",
  "joined_at": "2026-01-29T10:00:00Z",
  "roles": [
    {
      "role_id": "550e8400-e29b-41d4-a716-446655440003",
      "role_name": "ADMIN",
      "assigned_at": "2026-01-29T10:00:00Z",
      "assigned_by": "550e8400-e29b-41d4-a716-446655440001"
    }
  ]
}
```

---

### Assign role to member
```
POST /tenants/{tenantId}/members/{userId}/roles
Headers: X-Current-User-ID: {assignedBy}
```

**Request Body:**
```json
{
  "role_id": "550e8400-e29b-41d4-a716-446655440003"
}
```

**Response:** TenantMembershipDto (updated)

---

### Revoke role from member
```
DELETE /tenants/{tenantId}/members/{userId}/roles/{roleId}
```

**Note:** Cannot revoke last role of active membership. Use Remove Member instead.

**Response:** 204 No Content

---

### Suspend member
```
PATCH /tenants/{tenantId}/members/{userId}/suspend
```

**Response:** 204 No Content

---

### Activate suspended member
```
PATCH /tenants/{tenantId}/members/{userId}/activate
```

**Response:** 204 No Content

---

### Remove member from tenant
```
DELETE /tenants/{tenantId}/members/{userId}
Headers: X-Current-User-ID: {removedBy}
```

**Request Body (optional):**
```json
{
  "reason": "Left the company"
}
```

**Note:** Owner cannot be removed. Transfer ownership first.

**Response:** 204 No Content

---

## Invitations Management

### Invite user to tenant
```
POST /invitations/tenants/{tenantId}
Headers: X-Current-User-ID: {invitedBy}
```

**Request Body:**
```json
{
  "email": "user@example.com",
  "role_ids": [
    "550e8400-e29b-41d4-a716-446655440003",
    "550e8400-e29b-41d4-a716-446655440004"
  ],
  "valid_days": 7
}
```

**Response:** 201 Created with InvitationDto

**Example:**
```json
{
  "invitation_id": "550e8400-e29b-41d4-a716-446655440005",
  "tenant_id": "550e8400-e29b-41d4-a716-446655440002",
  "email": "user@example.com",
  "status": "PENDING",
  "token": "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwXxYyZz12345678910111213",
  "created_at": "2026-01-29T10:00:00Z",
  "expires_at": "2026-02-05T10:00:00Z",
  "role_ids": [
    "550e8400-e29b-41d4-a716-446655440003",
    "550e8400-e29b-41d4-a716-446655440004"
  ],
  "is_valid": true,
  "is_expired": false
}
```

---

### Get all invitations for a tenant
```
GET /invitations/tenants/{tenantId}
```

**Response:** Array of InvitationDto

---

### Get pending invitations for a tenant
```
GET /invitations/tenants/{tenantId}/pending
```

**Response:** Array of InvitationDto (with status=PENDING only)

---

### Get pending invitation by email and tenant
```
GET /invitations/tenants/{tenantId}/pending-by-email?email=user@example.com
```

**Response:** InvitationDto

---

### Get all pending invitations for an email
```
GET /invitations/pending?email=user@example.com
```

**Response:** Array of InvitationDto across all tenants

---

### Get all invitations sent by a user
```
GET /invitations/sent-by/{userId}
```

**Response:** Array of InvitationDto

---

### Accept invitation
```
POST /invitations/{token}/accept
Headers: X-Current-User-ID: {userId}
```

**Validations:**
- Invitation must be PENDING
- Invitation must not be expired
- User's email must match invitation email
- User must not already be member of tenant

**Response:** 204 No Content

**Side Effect:** Creates TenantMembership with roles from invitation

---

### Reject invitation
```
POST /invitations/{token}/reject
Headers: X-Current-User-ID: {userId}
```

**Response:** 204 No Content

---

### Cancel invitation
```
DELETE /invitations/{invitationId}
Headers: X-Current-User-ID: {cancelledBy}
```

**Note:** Only PENDING invitations can be cancelled.

**Response:** 204 No Content

---

## Ownership Transfer

### Transfer tenant ownership
```
POST /tenants/{tenantId}/ownership/transfer
Headers: X-Current-User-ID: {currentOwnerId}
```

**Request Body:**
```json
{
  "new_owner_id": "550e8400-e29b-41d4-a716-446655440006"
}
```

**Validations:**
- Current owner ID must match actual tenant owner
- New owner must be active member
- New owner must have ADMIN role

**Response:** 204 No Content

---

## User Memberships (from user perspective)

### Get user's membership in specific tenant
```
GET /tenants/{tenantId}/members/{userId}
```

---

## Common Status Values

- `ACTIVE` - Membership is active
- `SUSPENDED` - Membership is suspended (user cannot access)
- `REMOVED` - Membership is removed (historical record)

## Invitation Status Values

- `PENDING` - Invitation awaiting acceptance
- `ACCEPTED` - Invitation has been accepted, membership created
- `REJECTED` - Invitation was rejected by user
- `CANCELLED` - Invitation was cancelled by inviter
- `EXPIRED` - Invitation expired without acceptance

---

## Error Handling

All endpoints return appropriate HTTP status codes:
- `200 OK` - Successful GET
- `201 Created` - Successful POST (resource created)
- `204 No Content` - Successful PATCH/DELETE
- `400 Bad Request` - Invalid input
- `404 Not Found` - Resource not found
- `409 Conflict` - Business rule violation (e.g., duplicate invitation)

Error response body:
```json
{
  "module": "AUTH",
  "errors": {
    "field": ["Error message"]
  }
}
```
