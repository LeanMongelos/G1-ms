# NovaTech ERP — Smoke Test Suite

Automated integration smoke tests that catch runtime failures before they reach production: HTTP 500s, Jackson circular-reference serialization, and auth boundary regressions.

## Run

From `D:\notech\backend`:

```powershell
.\mvnw.cmd test
```

Only smoke tests:

```powershell
.\mvnw.cmd test -Dtest="com.novatech.store.smoke.*"
```

Uses the **`test`** profile with **H2 in-memory** (no MySQL required). `SmokeTestDataConfig` seeds demo users and core entities; CommandLineRunners populate billing, CRM, RBAC, and config demos.

## Demo credentials (same as production demo)

| Role | Email | Password |
|------|-------|----------|
| Staff (SUPERADMIN) | `superadmin@novatech.com` | `admin123` |
| Cliente | `cliente@novatech.com` | `cliente123` |

## What is covered

| Test class | Purpose |
|------------|---------|
| `ApiSmokeTest` | Every GET endpoint (~100 cases): status ≠ 500, JSON parseable when `application/json` |
| `RegressionSmokeTest` | `/presupuestos`, `/remitos`, `/config/contabilidad/resumen` — known past bugs |
| `SecuritySmokeTest` | Staff vs cliente vs anonymous access boundaries |
| `AuthSmokeTest` | Login, invalid credentials, `/auth/me` session |

### Assertions per endpoint

- HTTP status is **not 500**
- Status is one of the documented allowed codes (200, 401, 403, 404, 204 as appropriate)
- JSON responses parse with Jackson (no circular-ref garbage like excessive `}}}}`)
- Path placeholders (`{productId}`, `{presupuestoId}`, …) resolved from seeded data or list endpoints

### Domains exercised

- Health & ping
- Auth (login / me)
- Dashboard KPIs & admin search/notifications
- Billing: presupuestos, remitos, facturas, pedidos, pagos, órdenes de compra
- Inventario: productos, categorías, listas de precios, stock bajo
- CRM: resumen, conversaciones, integraciones, interacciones, campañas, promociones
- Config: contabilidad, RBAC, emisores, plantillas, catálogos, auditoría, logs
- Cuotas / planes
- Cliente portal (pedidos, facturas, perfil, tickets, devoluciones, cuotas, préstamos)
- Carrito, envíos, reseñas (authenticated)

## Structure

```
src/test/java/com/novatech/store/smoke/
  SmokeTestBase.java          — login helpers, JSON validation
  SmokeContext.java           — ID discovery for path params
  SmokeEndpointCatalog.java   — all GET endpoints
  ApiSmokeTest.java
  RegressionSmokeTest.java
  SecuritySmokeTest.java
  AuthSmokeTest.java
src/test/resources/
  application-test.properties — H2 + test JWT secret
  SmokeTestDataConfig.java      — programmatic H2 seed
```

## Frontend

No Playwright/Cypress suite in this repo. **Backend smoke covers the API layer** that backs Angular admin and cliente portal pages. Run frontend E2E separately if added later.

## CI recommendation

Add to pipeline before deploy:

```powershell
.\mvnw.cmd test -Dtest="com.novatech.store.smoke.*"
```
