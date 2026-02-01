# Sistema Multi-Tenant con Roles - Guía de Implementación

## Descripción General

Se ha implementado un sistema completo de multi-tenancy con roles en el backend. El sistema permite:

1. **Usuarios globales** - Credenciales únicas compartidas entre todos los tenants
2. **Tenants** - Espacios de trabajo aislados
3. **Memberships** - Relación usuario-tenant con roles específicos
4. **Invitaciones** - Sistema seguro para invitar usuarios a tenants
5. **Ownership** - Gestión de propiedad con transferencia

---

## Arquitectura

### Capas

```
REST Controllers
    ↓
Application Services (Transactional)
    ↓
Domain Services (Business Rules)
    ↓
Domain Models (Aggregates, Value Objects)
    ↓
Repository Ports
    ↓
JPA Repositories & Adapters
    ↓
Database (PostgreSQL)
```

### Componentes Principales

**Modelos de Dominio:**
- `User` - Usuario global
- `Tenant` - Espacio de trabajo
- `Role` - Rol con miembros
- `TenantMembership` - Relación usuario-tenant-roles
- `Invitation` - Invitación con token seguro

**Servicios de Dominio:**
- `TenantMembershipManager` - Gestiona memberships
- `InvitationManager` - Gestiona invitaciones
- `TenantOwnershipManager` - Gestiona transferencia de ownership
- `TenantCreator` - Crea nuevos tenants

**Servicios de Aplicación:**
- `TenantMembershipApplicationService` - API de memberships
- `InvitationApplicationService` - API de invitaciones
- `TenantOwnershipApplicationService` - API de ownership

**Controladores REST:**
- `TenantMembershipController` - Endpoints `/tenants/{id}/members`
- `InvitationController` - Endpoints `/invitations`
- `TenantOwnershipController` - Endpoints `/tenants/{id}/ownership`

---

## Flujos de Uso

### Flujo 1: Crear Tenant

**Paso 1:** Usuario A se registra
```
POST /auth/register
Body: { email, password, name }
Response: User { id: A }
```

**Paso 2:** Usuario A crea tenant
```
POST /tenants
Header: X-Current-User-ID: A
Body: { name: "ACME Corp" }
Response: Tenant { id: T1, ownerId: A }

Resultado:
- Tenant T1 creado
- Role ADMIN para T1 creado
- TenantMembership creado: User A + Role ADMIN en T1
- Role.members actualizado: A añadido a ADMIN
```

### Flujo 2: Invitar Usuario

**Paso 1:** Usuario A invita a Usuario B
```
POST /invitations/tenants/T1
Header: X-Current-User-ID: A
Body: {
  email: "b@example.com",
  role_ids: [ADMIN_ROLE_ID, SALES_ROLE_ID],
  valid_days: 7
}
Response: Invitation {
  token: "secure64chartoken",
  status: "PENDING",
  expiresAt: "+7 days"
}
```

**Paso 2:** Email enviado a B con enlace (backend no envía, es responsabilidad de la app)
```
https://app.com/invitations/accept?token=secure64chartoken
```

**Paso 3:** Usuario B acepta invitación
```
POST /invitations/secure64chartoken/accept
Header: X-Current-User-ID: B
```

**Resultado:**
- Invitation.status = ACCEPTED
- TenantMembership creado: User B + Roles [ADMIN, SALES] en T1
- Role.members actualizado para ambos roles
- User B ahora puede acceder a T1

### Flujo 3: Transferir Ownership

**Paso 1:** Usuario A transfiere ownership a B
```
POST /tenants/T1/ownership/transfer
Header: X-Current-User-ID: A
Body: { new_owner_id: B }
```

**Validaciones:**
- A es el owner actual de T1
- B es miembro activo de T1
- B tiene rol ADMIN

**Resultado:**
- Tenant.ownerId = B
- User A puede ser removido sin restricciones
- User B es el nuevo owner

**Paso 2:** Usuario A se auto-remove
```
DELETE /tenants/T1/members/A
Header: X-Current-User-ID: A
```

**Resultado:**
- TenantMembership de A marcado como REMOVED
- Roles de A sincronizados (removidos de Role.members)

---

## Persistencia

### Tablas de Base de Datos

```
tenant_memberships
├── membership_id (PK)
├── user_id (FK)
├── tenant_id (FK)
├── status (ACTIVE/SUSPENDED/REMOVED)
├── joined_at
├── invited_by (FK)
└── timestamps

membership_roles
├── membership_id (PK, FK)
├── role_id (PK, FK)
├── role_name (denormalizado)
├── assigned_at
├── assigned_by (FK)
└── created_at

invitations
├── invitation_id (PK)
├── tenant_id (FK)
├── email
├── invited_by (FK)
├── status (PENDING/ACCEPTED/REJECTED/CANCELLED/EXPIRED)
├── token (UNIQUE)
├── created_at
├── expires_at
└── updated_at

invitation_roles
├── invitation_id (PK, FK)
├── role_id (PK, FK)
└── created_at
```

### Migraciones

Se proporciona `V1__Create_tenant_memberships_and_invitations_tables.sql` con:
- CREATE TABLE statements
- Índices para performance
- Constraints de integridad referencial
- Unique indexes para prevenir duplicados

---

## Sincronización de Datos

### Problema
`Role.members` y `TenantMembership.roles` representan la misma información desde diferentes perspectivas.

### Solución
**Todas las operaciones** deben usar `TenantMembershipManager` que sincroniza automáticamente:

```java
// ✅ CORRECTO - Sincroniza ambos lados
membershipManager.assignRoleToMember(userId, tenantId, roleId, assignedBy);
// Actualiza: TenantMembership.roles AND Role.members

// ❌ INCORRECTO - Desincroniza
role.assignUser(userId);
roleRepository.save(role);
// Solo actualiza Role.members, no TenantMembership
```

**Garantía:** Dentro de una transacción, ambas actualizaciones ocurren o ninguna.

---

## Transacciones

Todos los cambios están marcados con `@Transactional`:

```java
@Service
public class TenantMembershipApplicationService {
    @Transactional
    public void assignRole(...) {
        // Múltiples operaciones atómicas
        membershipManager.assignRoleToMember(...);  // Actualiza membership
        // Aquí se sincroniza Role.members en el mismo manager
    }
}
```

**Comportamiento:**
- Si alguna operación falla → **ROLLBACK** completo
- Si todas tienen éxito → **COMMIT** atómico

---

## Autorización (A implementar)

El sistema de persistencia permite fácilmente agregar autorización con decoradores:

```java
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
@PostMapping("/{userId}/roles")
public TenantMembershipDto assignRole(
    @PathVariable UUID userId,
    @RequestBody AssignRoleRequest request,
    @AuthenticationPrincipal UserId currentUser
) {
    // Solo permite si currentUser es ADMIN o es el mismo usuario
    return applicationService.assignRole(userId, tenantId, request, currentUser.value());
}
```

---

## Manejo de Errores

### Excepciones de Dominio

Todas las violaciones de reglas de negocio lanzan `ErpValidationException`:

```java
try {
    membershipManager.addMemberToTenant(userId, tenantId, roleIds, addedBy);
} catch (ErpValidationException ex) {
    // ex.getValidationErrors() contiene detalles
    // Campo: error message
}
```

### Excepciones Específicas

Se proporcionan excepciones específicas (aunque no todas se lanzan explícitamente, pueden ser lanzadas en los adapters):

- `MembershipNotFoundException`
- `UserAlreadyMemberException`
- `InvitationExpiredException`
- `TenantNotActiveException`
- etc.

---

## Testing

### Test Unitarios

Proporcionados para modelos de dominio:
- `TenantMembershipTest` - 13 casos
- `InvitationTest` - 14 casos

Ejecutar:
```bash
./gradlew test -k TenantMembership
./gradlew test -k Invitation
```

### Test de Integración

Proporcionado:
- `TenantMembershipFlowTest` - Flujo completo

Características:
- Usa Mockito para repositorios
- Prueba sincronización domain service
- Verifica transacciones

Ejecutar:
```bash
./gradlew test -k TenantMembershipFlow
```

### Test End-to-End (Manual)

Flujo completo recomendado:
1. Usuario A se registra → Crea Tenant → Invita Usuario B
2. Usuario B acepta invitación → Verifica memberships
3. Usuario A transfiere ownership → Verifica cambio
4. Usuario B puede remover A → Verifica memberships actualizado

---

## Próximos Pasos

### 1. Integración con Sistema de Autenticación
```java
@Configuration
public class SecurityConfig {
    // Agregar X-Current-User-ID header extraction
    // Validar tokens JWT o sesiones
}
```

### 2. Endpoint de Verificación de Invitación
```
GET /invitations/{token}/verify
Response: { isValid, expiresAt, email, tenantName, roles }
```

### 3. Email Notifications
```java
@Service
public class InvitationEmailService {
    public void sendInvitationEmail(Invitation invitation, User user) {
        // Enviar email con enlace de aceptación
    }
}
```

### 4. Auditoría Completa
```java
// Agregar campos a entities
private UUID createdBy;
private UUID updatedBy;

// Usar Spring Data JPA @CreatedBy, @LastModifiedBy
```

### 5. Soft Delete y Recuperación
```java
// Agregar deletedAt para registro histórico
private Instant deletedAt;

// Recuperar membershi ps removidas (si aplica)
```

---

## Performance Considerations

### Índices de Base de Datos

Proporcionados en la migración V1:
- `idx_tm_user_id` - Queries por usuario
- `idx_tm_tenant_id` - Queries por tenant
- `idx_tm_status` - Queries por estado
- `uk_pending_invitation` - UNIQUE para invitaciones pendientes duplicadas

### Query Optimization

**Membership roles son EAGER loaded:**
```java
@OneToMany(fetch = FetchType.EAGER)
private Set<MembershipRoleEntity> roles;
```

**Motivo:** Casi siempre se necesitan roles con membership.

**Si hay problemas de performance:** Cambiar a LAZY y usar custom queries:
```java
@Query("""
    SELECT DISTINCT tm FROM TenantMembershipEntity tm
    LEFT JOIN FETCH tm.roles
    WHERE tm.userId = :userId
""")
```

---

## Operaciones Administrativas

### Cleanup de Invitaciones Expiradas

```java
@Scheduled(cron = "0 0 3 * * *")  // 3 AM daily
@Transactional
public void cleanupExpiredInvitations() {
    invitationRepository.deleteExpiredBefore(Instant.now());
}
```

### Reportes de Memberships

```java
public List<MembershipReport> getMembershipsByTenant(UUID tenantId) {
    return membershipRepository.findByTenantId(tenantId)
        .stream()
        .map(this::toReport)
        .collect(Collectors.toList());
}
```

---

## Referencias

### Archivos Clave

**Domain:**
- `/domain/model/membership/TenantMembership.java` - Agregado
- `/domain/model/invitation/Invitation.java` - Agregado
- `/domain/services/TenantMembershipManager.java` - Orquestador
- `/domain/services/InvitationManager.java` - Orquestador

**Application:**
- `/application/services/TenantMembershipApplicationService.java`
- `/application/services/InvitationApplicationService.java`
- `/application/dto/*.java` - DTOs

**Infrastructure:**
- `/infra/entities/*.java` - JPA Entities
- `/infra/adapters/TenantMembershipRepositoryAdapter.java`
- `/infra/adapters/InvitationRepositoryAdapter.java`
- `/infra/controllers/*.java` - REST Controllers

**Database:**
- `/resources/db/migration/V1__Create_tenant_memberships_and_invitations_tables.sql`

**Documentation:**
- `/infra/controllers/API_ENDPOINTS.md` - Especificación de API

---

## Conclusión

El sistema está completamente implementado y listo para:
- ✅ Integración con controladores de autenticación
- ✅ Agregar autorización con @PreAuthorize
- ✅ Configurar notificaciones por email
- ✅ Agregar auditoría y logging
- ✅ Tests end-to-end

La arquitectura Clean permite fácil testabilidad y mantenimiento a futuro.
