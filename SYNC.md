# Sincronización de equipo — NovaTech ERP

Este archivo marca el **estado validado en local** (Jun 2026). Tras merge, todo el equipo debe quedar en el mismo commit.

## Commits de referencia

| Repo | Remote | Commit `main` |
|------|--------|---------------|
| Backend | [LeanMongelos/G1-ms](https://github.com/LeanMongelos/G1-ms) | Ver `git log -1` tras pull |
| Frontend | [EricWithC04/G1-ui](https://github.com/EricWithC04/G1-ui) | Ver `git log -1` tras pull |

## Alinear tu PC (obligatorio)

```powershell
# Backend
cd D:\notech\backend   # o tu carpeta G1-ms
git checkout main
git pull origin main

# Frontend
cd D:\notech\_g1-ui-clone   # o tu clone G1-ui
git checkout main
git pull origin main
```

Si usás carpeta `frontend/` sin git (solo copia de trabajo), sincronizá desde el clone:

```powershell
robocopy D:\notech\_g1-ui-clone\src D:\notech\frontend\src /MIR /XD node_modules .angular
copy D:\notech\_g1-ui-clone\README.md D:\notech\frontend\
```

## Verificación

```powershell
cd D:\notech\backend
.\mvnw.cmd test -Dtest="*Smoke*"
```

Credenciales demo: `superadmin@novatech.com` / `admin123`, `cliente@novatech.com` / `cliente123`.

## Documentación

- Anti-regresiones: [docs/GUARDRAILS.md](./docs/GUARDRAILS.md)
- Monorepo completo (local): `notech/docs/` → copia en [docs/erp/](./docs/erp/)
- Agentes IA: [AGENTS.md](./AGENTS.md)
