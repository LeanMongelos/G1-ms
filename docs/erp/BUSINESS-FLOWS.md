# Flujos de negocio

## 1. Venta web (e-commerce)

```mermaid
sequenceDiagram
  participant C as Cliente
  participant FE as Frontend
  participant API as Backend
  participant DB as MySQL

  C->>FE: Navega catálogo
  C->>FE: Agrega al carrito
  C->>FE: Checkout
  FE->>API: POST /ordenes/confirmar
  API->>DB: Pedido + Detalle + Pago + Envío
  API-->>FE: Confirmación
  FE-->>C: Mis pedidos
```

**Estados pedido:** PENDIENTE → (pagos) → PARCIAL/PAGADO → ENVIADO.

**Canales:** WEB (default checkout).

---

## 2. POS mostrador

```mermaid
flowchart LR
  A[Seleccionar productos] --> B[Cliente opcional]
  B --> C[Confirmar venta]
  C --> D[Pedido canal POS]
  D --> E[Pago EFECTIVO/TARJETA]
  E --> F[Factura opcional]
```

Ruta admin: `/admin/pos`. Mismo endpoint `POST /ordenes/confirmar` con `canalOrigen: POS`.

---

## 3. CRM → Pedido

```mermaid
flowchart TB
  IN[Inbox conversación] --> ASIG[Asignar vendedor]
  ASIG --> FICHA[Ficha cliente]
  FICHA --> NP[Nuevo pedido admin]
  NP --> PED[Pedido creado]
  PED --> PAG[Registrar pago]
  PED --> FAC[Facturar]
```

Bandeja: `/admin/crm/inbox`. Notificaciones CRM en header admin.

---

## 4. Presupuesto → Factura → Remito

```mermaid
stateDiagram-v2
  [*] --> BORRADOR
  BORRADOR --> ENVIADO: enviar
  ENVIADO --> ACEPTADO: cliente acepta
  ENVIADO --> RECHAZADO
  ACEPTADO --> FACTURADO: emitir factura
  ACEPTADO --> Remito: generar remito
  FACTURADO --> [*]
```

| Paso | Acción admin | API |
|------|--------------|-----|
| Crear | `/admin/presupuestos/nuevo` | POST `/presupuestos` |
| Aceptar | Detalle → cambiar estado | POST `.../estado/ACEPTADO` |
| Facturar | Detalle → Facturar | POST `/facturas/generar-presupuesto/{id}` |
| Remito | Detalle → Generar remito | POST `/remitos/generar-presupuesto/{id}` |

Alternativa desde **pedido** directo: POST `/facturas/generar` + POST `/remitos/generar-pedido/{id}`.

---

## 5. Cobros y cartera

```mermaid
flowchart LR
  P[Pedido PENDIENTE] --> PG[Registrar pago]
  PG --> APR{Aprobado?}
  APR -->|Transferencia/QR| MAN[Aprobar manual]
  APR -->|Efectivo/Tarjeta| AUTO[Auto APROBADO]
  MAN --> SUM[Recalcular estado pedido]
  AUTO --> SUM
  SUM --> PAGADO[Pedido PAGADO]
```

Dashboard muestra **cartera pendiente** = ventas totales − cobrado.

Pagos pendientes: `/admin/pagos?estado=PENDIENTE`.

---

## 6. Préstamo personal (financiación propia)

1. Al facturar, marcar "Préstamo personal" + cuotas + interés.
2. Backend crea `PlanCuotas` + `Cuota` mensuales (vencimiento día 10).
3. Módulo créditos: cobro cuotas, morosidad, campañas recordatorio.

---

## 7. Envíos

1. Checkout o pedido admin define dirección.
2. Admin actualiza estado en `/admin/envios`.
3. Tracking opcional (`numeroTracking`).

Estados: PREPARANDO → EN_CAMINO → ENTREGADO.

---

## 8. Reposición stock (OC)

1. Dashboard alerta stock bajo.
2. Productos → generar OC automática o por selección.
3. OC agrupada por proveedor.
4. Flujo: BORRADOR → ENVIADA → RECIBIDA (incrementa stock).

---

## 9. Facturación fiscal (estado actual)

```mermaid
flowchart LR
  PED[Pedido/Presupuesto] --> GEN[Generar factura]
  GEN --> NUM[Numeración NV-AAAA-XXXXXX]
  NUM --> IVA[Cálculo IVA 21%]
  IVA --> EMIT[Estado EMITIDA]
  EMIT --> AFIP[AFIP WSFE - FUTURO]
```

**Hoy:** comprobante legal interno listo para impresión; CAE/QR AFIP documentado en ROADMAP.

---

## 10. Auditoría

Cambios sensibles (config, usuarios, emisores) registran en `RegistroAuditoria`. Consulta: Configuración → Auditoría.
