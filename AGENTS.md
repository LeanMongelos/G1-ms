# AGENTS.md — NovaTech ERP (Backend / G1-ms)

Guía para **agentes de IA y desarrolladores** en el repo **LeanMongelos/G1-ms**.

## Antes de cualquier cambio

Leer **[docs/GUARDRAILS.md](./docs/GUARDRAILS.md)** — reglas anti-regresión obligatorias.

## Repositorios

| Componente | GitHub | Puerto |
|------------|--------|--------|
| **Backend (este repo)** | LeanMongelos/G1-ms | 8080 |
| **Frontend** | EricWithC04/G1-ui | 4200 |

Integración: JSON REST; CORS en prod; en dev el frontend usa proxy hacia `:8080`.

## Arranque local

```powershell
$env:DB_PASSWORD="tu_clave_mysql"
$env:JWT_SECRET="dev-only-cambiar-en-produccion-novatech-2026-secreto-largo"
cd D:\notech\backend
.\mvnw.cmd spring-boot:run -DskipTests
```

MySQL 8 en `localhost:3306`, base `novatech_store` (se crea sola con `ddl-auto=update`).

## Tests obligatorios antes de PR

```powershell
.\mvnw.cmd test -Dtest="com.novatech.store.smoke.*"
```

~124 tests, H2 in-memory, sin MySQL. Ver [SMOKE.md](./SMOKE.md).

Si falla un test: **arreglar o actualizar el catálogo**, no desactivar la suite.

## Arquitectura

```
controller/  → REST, validación @Valid
service/     → negocio, @Transactional en escrituras
repository/  → JPA
entity/      → tablas; cuidado con Jackson al serializar
dto/         → respuestas compuestas (preferir en listados)
config/      → Security, seeders, CORS
```

## Reglas que NO romper

### Jackson / entidades

- `@JsonIgnore` solo en back-references de `Detalle*` (hijo → padre).
- **No** ignorar `pedido` en `Envio` ni relaciones que alimentan tablas admin.
- `@JsonIgnoreProperties` para recortar grafos (`notas`, `lineas`, `imagen`).

### Servicios

- Sin recursión mutua entre métodos públicos (ver `ContabilidadConfigService`).
- KPIs unificados: `CrmMetricsService`, `StockInventarioUtil`, `PagoUtil`.

### Productos

- DELETE con referencias → 400/409 via `ProductoService` + handler FK.

### Seguridad

- `/config/**`, métricas staff: solo roles panel admin.
- `/cliente/**`: solo rol CLIENTE.

### Smoke

- Nuevo `@GetMapping` público o autenticado → entrada en `SmokeEndpointCatalog.java`.
- Bug de producción → test en `RegressionSmokeTest.java`.

## Credenciales demo

| Email | Rol | Password |
|-------|-----|----------|
| superadmin@novatech.com | SUPERADMIN | admin123 |
| cliente@novatech.com | CLIENTE | cliente123 |

## Commits

- Mensajes en **español**, imperativo.
- No commitear `.env`, passwords, `target/`.
- PR a `main`; smoke en verde.

## Documentación

- [README.md](./README.md) — API completa
- [SMOKE.md](./SMOKE.md) — tests integración
- [docs/GUARDRAILS.md](./docs/GUARDRAILS.md) — anti-regresiones
- Monorepo local: `D:\notech\docs\` (ARCHITECTURE, API, RBAC, …)
