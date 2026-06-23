# NovaTech ERP — Documentación

Documentación técnica y funcional del sistema **NovaTech ERP**: e-commerce B2C + panel administrativo enterprise para retail tecnológico en Argentina.

## ⭐ Empezar aquí (anti-regresiones)

| Documento | Descripción |
|-----------|-------------|
| **[GUARDRAILS.md](./GUARDRAILS.md)** | **Obligatorio antes de cada iteración** — checklist, bugs históricos, reglas Jackson/proxy/auth/DI |

## Índice

| Documento | Descripción |
|-----------|-------------|
| [ARCHITECTURE.md](./ARCHITECTURE.md) | Arquitectura de sistema, capas, stack y topología de despliegue |
| [ADMIN-ERP.md](./ADMIN-ERP.md) | Módulos del panel admin: rutas, permisos y flujos de usuario |
| [API.md](./API.md) | Referencia REST agrupada por dominio |
| [DATABASE.md](./DATABASE.md) | Modelo de datos, entidades y relaciones |
| [BUSINESS-FLOWS.md](./BUSINESS-FLOWS.md) | Flujos de negocio end-to-end |
| [LISTAS-PRECIOS.md](./LISTAS-PRECIOS.md) | Listas multicanal, descuentos y validaciones |
| [RBAC.md](./RBAC.md) | Roles, permisos y extensión del modelo de acceso |
| [DEPLOYMENT.md](./DEPLOYMENT.md) | Despliegue en producción (MySQL, env, Docker) |
| [DEVELOPMENT.md](./DEVELOPMENT.md) | Setup local, comandos y estructura del repo |
| [ROADMAP.md](./ROADMAP.md) | Estado actual, prioridades y trabajo futuro |

## Guías para agentes / IA

| Repo | Archivo |
|------|---------|
| Backend | `backend/AGENTS.md` + `backend/docs/GUARDRAILS.md` |
| Frontend | `frontend/AGENTS.md` + `frontend/docs/GUARDRAILS.md` |

## Enlaces rápidos

- Backend: `backend/README.md` · Smoke: `backend/SMOKE.md`
- Frontend: `frontend/README.md`
- Health check: `GET http://localhost:8080/actuator/health`
- Ping: `GET http://localhost:8080/ping`

## Convenciones

- Idioma de la UI admin: **español (Argentina)**
- API: JSON camelCase, prefijo base `/` en puerto 8080
- Frontend dev: `http://localhost:4200` con **proxy** (`apiUrl` vacío)
- Moneda: ARS, formato `$` en pantalla

## Versión documentada

Junio 2026 — Angular 22 + Spring Boot 4.1 + MySQL 8.
