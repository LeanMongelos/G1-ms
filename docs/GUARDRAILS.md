# NovaTech ERP — Guardrails (no romper en cada iteración)

**Leer esto antes de tocar backend, frontend o auth.** Documento orientado a humanos y agentes de IA.

> Copia canónica en monorepo: `notech/docs/GUARDRAILS.md`. Mantener sincronizado al cambiar reglas.

## Objetivo

Evitar regresiones que ya ocurrieron: pantalla blanca, JSON inválido, tablas vacías, 500 silenciosos, logout roto, KPIs incoherentes.

## Checklist obligatorio antes de merge

### Backend (este repo — G1-ms)

```powershell
.\mvnw.cmd test -Dtest="com.novatech.store.smoke.*"
.\mvnw.cmd compile -DskipTests
```

- [ ] **124+ smoke tests** en verde (H2, perfil `test`)
- [ ] Si tocaste entidades JPA → revisar [Serialización JSON](#serialización-json-jackson)
- [ ] Si tocaste un `*Service` → no llamar métodos que se invocan mutuamente sin extraer helper
- [ ] Si agregaste endpoint → registrar en `SmokeEndpointCatalog.java`

### Frontend (G1-ui)

Ver `docs/GUARDRAILS.md` en el repo frontend (proxy, DI, smoke manual UI).

---

## Serialización JSON (Jackson)

### Problema

Devolver entidades JPA completas en controllers provoca referencias circulares, payloads gigantes y `@JsonIgnore` mal aplicado.

### Reglas

1. **Back-references en `Detalle*`:** `@JsonIgnore` en el padre (`presupuesto`, `remito`, `pedido`, `factura`, …).
2. **Relaciones ManyToOne que la UI lista:** NO `@JsonIgnore` a ciegas. Usar `@JsonIgnoreProperties({"notas", "lineas", ...})` (ver `Envio.pedido`, `Remito`, `Factura`).
3. **Producto anidado:** omitir `imagen` y `descripcion` en detalles.
4. **Preferencia:** DTOs para listados a largo plazo.

### Tests de regresión

`src/test/java/com/novatech/store/smoke/RegressionSmokeTest.java` — presupuestos, remitos, facturas, contabilidad, envíos, cliente portal, delete producto.

Detalle: [SMOKE.md](./SMOKE.md)

---

## Servicios — recursión

`ContabilidadConfigService`: `resumen()` ↔ `ensureContabilidadArgentina()` causó StackOverflow.

**Regla:** métodos `ensure*` no re-entran al orquestador; usar `buildResumen()` privado.

---

## Borrado de productos

Productos demo referenciados → DELETE debe ser 400/409, no 500. Ver `ProductoService.eliminar()`.

---

## Auth backend

- JWT cookie HttpOnly; `/config/**` staff-only.
- `GlobalExceptionHandler` para JSON no serializable y violaciones FK.

---

## Documentación

| Doc | Contenido |
|-----|-----------|
| [SMOKE.md](./SMOKE.md) | Suite smoke |
| [README.md](./README.md) | Setup API |
| [AGENTS.md](./AGENTS.md) | Guía para agentes |

*Junio 2026*
