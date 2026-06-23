# Módulo Configuración — NovaTech Store

Centro administrativo del sistema, replicando la estructura y UX del ERP iBiomédica adaptado a Angular + Spring Boot con tema admin oscuro NovaTech.

## Hub `/admin/configuracion`

- Grid 3 columnas con tarjetas filtradas por permiso RBAC (`PermisoService`).
- Badge **PRONTO** para features con `disponible: false`.
- Búsqueda de módulos.

## Sub-secciones (10 rutas)

| Ruta | Permiso | Componente |
|------|---------|------------|
| `/admin/configuracion/usuarios` | `usuarios.read` | `ConfigUsuarios` |
| `/admin/configuracion/contabilidad` | `config.manage_accounting` | `ConfigContabilidad` |
| `/admin/configuracion/emisores` | `emisores.read` | `ConfigEmisores` |
| `/admin/configuracion/plantillas` | `config.manage_billing_templates` | `ConfigPlantillas` |
| `/admin/configuracion/integraciones` | `config.manage_integrations` | `ConfigIntegraciones` |
| `/admin/configuracion/catalogos` | `config.update` | `ConfigCatalogos` |
| `/admin/configuracion/notificaciones` | `config.update` | `ConfigNotificaciones` |
| `/admin/configuracion/seguridad` | `config.update` | `ConfigSeguridad` |
| `/admin/configuracion/auditoria` | `auditoria.read` | `ConfigAuditoria` |
| `/admin/configuracion/logs` | `logs.read` | `ConfigLogs` |

Todas usan `ConfigPageShell` con enlace **← Volver a Configuración**.

## Seguridad (3 capas)

1. **Ruta admin:** `adminGuard` en `/admin/*`
2. **Sub-sección:** `permisoGuard('permiso.clave')` por ruta
3. **UI:** `PermisoService.puede()` oculta acciones (ADMIN tiene wildcard `*`)

## Backend APIs

- `GET/PATCH /config/rbac/matriz`, `/config/rbac/roles/{rol}`
- `GET/POST /config/contabilidad/resumen`
- CRUD `/config/emisores`, `/config/plantillas`, `/config/catalogos`
- `GET /configuracion/auditoria/registros` (filtros q, entidad, usuario)
- `GET /configuracion/logs/registros` (filtros nivel, origen, q; retención 15 días)
- Grupos existentes: `GET/PUT /configuracion/{grupo}` (notificaciones, seguridad)

## Entidades Prisma-equivalentes (JPA)

- `Permiso`, `RolPermiso` — RBAC seed
- `Emisor`, `PlantillaImpresion`, `CatalogoMaestro`, `AlicuotaIva`
- `RegistroAuditoria` (negocio) — ampliado con entidad, JSON antes/después
- `LogSistema` (técnico) — ampliado con stackTrace, metadataJson

## Navegación del panel admin

El sidebar sigue el mismo criterio que el hub de Configuración: **un link por área**, sin duplicar sub-módulos.

| Antes (sidebar) | Ahora |
|-----------------|-------|
| Usuarios | Configuración → Usuarios y Roles |
| Categorías | Configuración → Catálogos (tab Categorías de tienda) |
| Perfiles de cliente | CRM → Clientes |
| Bandeja / Clientes / Embudo (3 links) | CRM (tabs internas) |
| Planes de cuotas (sidebar) | Créditos y cuotas → botón Planes |

Redirecciones: `/admin/usuarios`, `/admin/categorias`, `/admin/perfiles` → rutas nuevas.


- `src/app/config/config-rbac.ts`
- `src/app/services/permiso.service.ts`
- `src/app/services/config-modulo.service.ts`
- `src/app/components/config-page-shell/`
- `src/app/pages/configuracion/sections/*`
