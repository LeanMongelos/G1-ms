# Referencia API REST

Base URL: `http://localhost:8080` (desarrollo)

Formato: JSON, camelCase. Errores: `{ "message": "..." }` con código HTTP apropiado.

## Autenticación

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/auth/login` | Login email + contraseña |
| POST | `/auth/register` | Registro cliente |

## Sistema

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/ping` | Liveness |
| GET | `/actuator/health` | Health + DB |
| GET | `/dashboard/kpis` | KPIs dashboard admin |
| GET | `/admin/buscar?q=` | Búsqueda global admin |
| GET | `/admin/notificaciones` | Alertas pendientes |

## Catálogo

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST | `/categorias` | CRUD categorías |
| GET/POST/PUT/DELETE | `/productos` | CRUD productos |
| GET | `/productos/stock-bajo` | Productos bajo umbral |

## Usuarios y clientes

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST/PUT/DELETE | `/usuarios` | Usuarios sistema |
| GET/POST/PUT/DELETE | `/perfiles-cliente` | Perfiles CRM |
| GET | `/perfiles-cliente/{id}/metricas` | KPIs cliente |
| GET | `/perfiles-cliente/{id}/historial` | Historial compras |

## Carrito y checkout

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST | `/carritos` | Carritos |
| GET/POST | `/detalles-carrito` | Líneas carrito |
| POST | `/ordenes/confirmar` | Checkout atómico |

## Pedidos y ventas

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/pedidos` | Listado (filtros canal, estado) |
| GET | `/pedidos/{id}/detalle` | Detalle enriquecido |
| POST/PUT/DELETE | `/pedidos` | CRUD |
| GET/POST | `/detalles-pedido` | Líneas pedido |
| POST | `/ordenes/confirmar` | Confirmar venta web/POS |

## Pagos

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST | `/pagos` | CRUD pagos |
| POST | `/pagos/{id}/aprobar` | Aprobar pago pendiente |
| GET | `/pagos/qr` | Datos QR simulado |

## Envíos

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST/PUT/DELETE | `/envios` | CRUD envíos |

## Finanzas

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST/PUT | `/presupuestos` | Presupuestos |
| POST | `/presupuestos/{id}/estado/{estado}` | Cambiar estado |
| GET/POST | `/facturas` | Facturas |
| POST | `/facturas/generar` | Desde pedido |
| POST | `/facturas/generar-presupuesto/{id}` | Desde presupuesto |
| POST | `/facturas/{id}/emitir` | Emitir borrador |
| GET/POST | `/remitos` | Remitos |
| POST | `/remitos/generar-pedido/{id}` | Remito desde pedido |
| POST | `/remitos/generar-presupuesto/{id}` | Remito desde presupuesto |

## Crédito

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST | `/planes-cuotas` | Planes |
| GET | `/cuotas` | Listado cuotas |
| GET | `/cuotas/vencidas` | Vencidas |
| POST | `/cuotas/{id}/pagar` | Registrar pago cuota |

## Compras

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST | `/ordenes-compra` | OC proveedores |
| POST | `/ordenes-compra/generar-stock-bajo` | Auto OC |
| POST | `/ordenes-compra/generar` | OC productos seleccionados |

## CRM

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/crm/resumen` | Resumen bandeja |
| GET/PUT | `/conversaciones` | Conversaciones |
| GET/POST | `/conversaciones/{id}/mensajes` | Mensajes |
| GET/POST | `/interacciones-crm` | Interacciones |

## Marketing

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/POST/PUT | `/promociones` | Promociones |
| POST | `/promociones/{id}/activar` | Activar |
| GET/POST | `/campanas` | Campañas |
| POST | `/campanas/{id}/enviar` | Enviar campaña |

## Configuración

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET/PUT | `/configuracion/{grupo}` | Config por grupo |
| GET/PUT | `/configuracion/catalogos` | Maestros |
| GET/POST/PUT | `/emisores` | Emisores fiscales |
| GET/POST/PUT | `/plantillas` | Plantillas impresión |
| GET/PUT | `/integraciones-canales` | Canales externos |
| GET | `/configuracion/contabilidad/resumen` | Config fiscal |
| GET | `/configuracion/rbac/matriz` | Matriz RBAC |
| GET | `/configuracion/auditoria/registros` | Auditoría |
| GET | `/configuracion/logs/registros` | Logs técnicos |

## Códigos HTTP habituales

| Código | Uso |
|--------|-----|
| 200 | OK |
| 201 | Creado |
| 400 | Validación / regla negocio |
| 404 | No encontrado |
| 409 | Conflicto (ej. rol en uso) |
| 500 | Error interno |
| 503 | Health DOWN (DB) |

## Ejemplo: KPIs dashboard

```http
GET /dashboard/kpis
```

```json
{
  "ventasHoy": 125000,
  "pedidosHoy": 3,
  "pagosPendientesAprobar": 2,
  "crmPendientes": 5,
  "productosBajoStock": 8,
  "ventasMes": 890000,
  "carteraPendiente": 45000
}
```
