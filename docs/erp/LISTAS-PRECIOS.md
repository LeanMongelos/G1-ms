# Listas de precios — NovaTech ERP

Sistema de **listas de precios multicanal** con descuentos globales y unitarios, validaciones de jerarquía comercial y resolución automática por canal de venta.

## Listas disponibles

| Código | Uso | Descuento global default |
|--------|-----|--------------------------|
| `MAYORISTA` | Revendedores, tipo cliente mayorista | 25% |
| `B2B` | Empresas, CRM, ventas asistidas | 18% |
| `ECOMMERCE` | Tienda web | 10% |
| `LOCAL` | POS / mostrador | 5% |

## Cálculo de precio

1. **Precio base** = `producto.precioLista` (PVP) si existe, sino `producto.precio`.
2. **Descuento global** de la lista (% sobre base).
3. **Override unitario** (opcional por producto):
   - `descuentoPorcentaje` — reemplaza el global para ese ítem.
   - `precioFijo` — precio final fijo (prioridad sobre descuentos).

## Validaciones de negocio

### Descuento global

- **Mayorista / B2B** deben tener descuento global **≥** e-commerce y local.
- **E-commerce / Local** no pueden tener descuento global **>** mayorista ni B2B.

> Un retail con más descuento que un mayorista destruye el margen del canal B2B.

### Precio efectivo por producto

Para el mismo producto, el precio final debe cumplir:

```
precio(MAYORISTA) ≤ precio(B2B) ≤ precio(ECOMMERCE) ≤ precio(LOCAL)
```

Al guardar un override unitario o cambiar el global, el backend rechaza configuraciones que violen esta cadena (`400` + mensaje claro).

## Asignación automática

| Contexto | Lista aplicada |
|----------|----------------|
| Checkout web (`canal=WEB`) | ECOMMERCE |
| POS (`canal=POS`) | LOCAL |
| Pedido admin / CRM | B2B |
| Cliente tipo `MAYORISTA` | MAYORISTA |
| Cliente `EMPRESA`, `CORPORATIVO`, `INSTITUCION_EDUCATIVA` | B2B |

Se puede forzar con `codigoListaPrecio` en `POST /ordenes/confirmar`.

## API REST

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/listas-precios` | Todas las listas |
| GET | `/listas-precios/{id}` | Una lista |
| PUT | `/listas-precios/{id}` | Actualizar global / nombre |
| GET | `/listas-precios/{id}/detalles` | Overrides unitarios |
| POST | `/listas-precios/{id}/detalles` | Crear/actualizar override |
| DELETE | `/listas-precios/{id}/detalles/{idDetalle}` | Quitar override |
| GET | `/listas-precios/resolver?productoId=&lista=` | Precio resuelto |
| GET | `/productos?canal=WEB` | Catálogo con `precioCanal` |

## Admin UI

**Ruta:** `/admin/listas-precios`  
**Menú:** Catálogo → Listas de precios

## Integración ventas

- **OrdenVentaService** usa lista resuelta al confirmar pedido/POS/checkout.
- El pedido guarda en notas: `Lista precio: {CODIGO}`.
- **Catálogo web** pide productos con `?canal=WEB` → muestra precio e-commerce.

## Extensión futura

- Listas por cliente/contrato
- Vigencia desde/hasta
- Márgenes mínimos por categoría
- Sincronización con ERP contable
