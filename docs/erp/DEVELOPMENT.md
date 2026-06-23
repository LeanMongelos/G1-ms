# Guía de desarrollo

> **Antes de codear:** leer [GUARDRAILS.md](./GUARDRAILS.md) y correr smoke tests backend.

## Prerrequisitos

- JDK 25
- MySQL 8 (localhost:3306)
- Node.js 20+ y npm
- Git

## Clonar e instalar

```powershell
cd D:\notech

cd backend
.\mvnw.cmd compile -DskipTests

cd ..\frontend
npm install
```

## Ejecutar en desarrollo

Terminal 1 — Backend:

```powershell
$env:DB_PASSWORD="tu_clave_mysql"
$env:JWT_SECRET="dev-only-cambiar-en-produccion-novatech-2026-secreto-largo"
cd D:\notech\backend
.\mvnw.cmd spring-boot:run -DskipTests
```

Terminal 2 — Frontend:

```powershell
cd D:\notech\frontend
npm start
```

URLs:
- http://localhost:4200 — tienda
- http://localhost:4200/admin — panel ERP
- http://localhost:8080 — API

**Importante:** en dev, `environment.ts` tiene `apiUrl: ''` y el proxy (`proxy.conf.json`) reenvía al backend. No usar URL absoluta `:8080` en dev salvo pruebas puntuales.

## Usuarios demo

| Email | Rol | Password |
|-------|-----|----------|
| superadmin@novatech.com | SUPERADMIN | admin123 |
| cliente@novatech.com | CLIENTE | cliente123 |

## Tests obligatorios antes de PR

```powershell
cd D:\notech\backend
.\mvnw.cmd test -Dtest="com.novatech.store.smoke.*"
```

Ver [../backend/SMOKE.md](../backend/SMOKE.md). Smoke manual UI: tabla en [GUARDRAILS.md](./GUARDRAILS.md).

## Estructura del proyecto

```
notech/
├── backend/          # G1-ms — Spring Boot
├── frontend/         # G1-ui — copia de trabajo Angular
├── _g1-ui-clone/     # G1-ui — git remote para PRs
└── docs/             # Documentación monorepo (+ GUARDRAILS.md)
```

## Comandos útiles

| Comando | Ubicación | Propósito |
|---------|-----------|-----------|
| `.\mvnw.cmd test -Dtest="com.novatech.store.smoke.*"` | backend | **Smoke obligatorio** |
| `.\mvnw.cmd compile -DskipTests` | backend | Compilar |
| `npm start` | frontend | Dev con proxy |
| `npm run build` | frontend | Build prod (budgets) |

## Configuración local

### Backend (`application.properties`)

- DB: `DB_URL`, `DB_USER`, `DB_PASSWORD`
- JWT: `JWT_SECRET` (obligatorio en prod)
- Puerto: 8080

### Frontend (`environment.ts`)

```typescript
export const environment = {
  production: false,
  apiUrl: '', // vacío en dev → proxy.conf.json
};
```

## Convenciones de código

Ver [GUARDRAILS.md](./GUARDRAILS.md) para reglas que evitaron regresiones (Jackson, DI, proxy, auth).

### Backend
- Entidades camelCase (`idPedido`)
- `@JsonIgnore` solo en back-references de `Detalle*`
- Services sin recursión mutua

### Frontend
- Standalone + signals
- `PermisoService` / guards para RBAC
- Nuevas rutas API → `proxy.conf.json`

## Documentación relacionada

- [GUARDRAILS.md](./GUARDRAILS.md)
- [ARCHITECTURE.md](./ARCHITECTURE.md)
- [API.md](./API.md)
- [RBAC.md](./RBAC.md)
- [backend/AGENTS.md](../backend/AGENTS.md)
- [frontend/AGENTS.md](../frontend/AGENTS.md)
