# NovaTech Store — Backend

Backend del proyecto **NovaTech Store** (Grupo 1): una API REST para un e-commerce
completo (tienda + panel de administración). Está pensado para que cualquiera del
equipo pueda **entenderlo, levantarlo y explicarlo** sin conocer el resto del código.

- **Stack:** Spring Boot 4.1 · Java 25 · Spring Data JPA (Hibernate) · MySQL 8
- **Seguridad de contraseñas:** BCrypt (`spring-security-crypto`)
- **Build:** Maven Wrapper (`mvnw` / `mvnw.cmd`) — no hace falta instalar Maven

---

## 1. Índice

1. Índice
2. Qué hace este backend
3. Cómo está organizado (arquitectura por capas)
4. Modelo de datos (las 12 entidades del DER)
5. Endpoints de la API (todos)
6. Autenticación (login/registro con BCrypt)
7. Ecosistema de pagos
8. Configuración (base de datos, CORS, variables de entorno)
9. Datos de ejemplo y usuarios demo
10. Cómo levantar el backend
11. Cómo reiniciarlo (cuando cambia una entidad)
12. Deploy con Docker
13. Cómo defender este desarrollo (preguntas frecuentes)

---

## 2. Qué hace este backend

Expone una **API REST en JSON** que cubre toda la lógica de la tienda:

- Catálogo de **productos** (con foto), **categorías**.
- **Usuarios** (ADMIN y CLIENTE) y **perfiles de cliente**.
- **Carrito** y su detalle, **pedidos** y su detalle.
- **Pagos** (con varios métodos), **planes de cuotas** (financiación), **envíos**.
- **Reseñas** de productos.
- **Login y registro** con contraseñas hasheadas.
- Endpoint `/ping` para verificar que el servicio está vivo.

La base de datos y las tablas **se crean solas** al arrancar, y se cargan datos de
ejemplo, así que el sistema funciona apenas se levanta.

---

## 3. Cómo está organizado (arquitectura por capas)

El backend sigue el patrón clásico de Spring Boot en **4 capas**. Cada pedido HTTP
recorre este camino:

```
Cliente (frontend)
      │  HTTP (JSON)
      ▼
┌─────────────┐   ┌─────────────┐   ┌──────────────┐   ┌──────────┐
│ Controller  │ → │  Service    │ → │ Repository   │ → │  MySQL   │
│ (endpoints) │   │ (lógica)    │   │ (Spring Data)│   │ (tablas) │
└─────────────┘   └─────────────┘   └──────────────┘   └──────────┘
      ▲                                     │
      └──────── Entity (objeto JSON ◄───────┘ se convierte a/desde fila de tabla)
```

- **Controller**: define las rutas (`@RestController` + `@RequestMapping`) y traduce
  HTTP ↔ Java. No tiene lógica de negocio.
- **Service**: la lógica (validar, buscar, lanzar errores 404, etc.).
- **Repository**: interfaces de Spring Data JPA; nos dan el CRUD sin escribir SQL.
- **Entity**: clases con `@Entity` que **son** las tablas de la base.

### Estructura de carpetas

```
src/main/java/com/novatech/store
├── BackendApplication.java   # arranque de la app
├── config/                   # CorsConfig (CORS) y PasswordConfig (bean de BCrypt)
├── controller/               # endpoints REST (uno por módulo) + AuthController + PingController
├── dto/                      # objetos de entrada/salida del login (LoginRequest, RegistroRequest, UsuarioResponse)
├── entity/                   # las 12 entidades JPA (tablas del DER)
├── exception/                # ResourceNotFoundException + GlobalExceptionHandler
├── repository/               # interfaces JpaRepository (acceso a datos)
└── service/                  # lógica de negocio (una por módulo)
src/main/resources
├── application.properties    # configuración (DB, JPA, CORS)
└── data.sql                  # datos de ejemplo (seed)
```

> **Por qué `spring-security-crypto` y NO `spring-boot-starter-security`:** solo
> necesitamos **encriptar contraseñas** (BCrypt). El starter completo activaría
> filtros que bloquean todas las rutas y romperían los endpoints. Usamos solo la
> librería de cifrado y hacemos un login simple a mano.

---

## 4. Modelo de datos (las 12 entidades del DER)

Las claves primarias se generan solas (`IDENTITY`). Los nombres de tabla los crea
Hibernate en `snake_case` (`perfil_cliente`, `detalle_pedido`, etc.).

| Entidad | Tabla | Campos principales | Relaciones |
|---|---|---|---|
| **Usuario** | `usuario` | idUsuario, nombre, email, contrasena (hash, nunca se devuelve), rol (`ADMIN`/`CLIENTE`), fechaRegistro | — |
| **Categoria** | `categoria` | idCategoria, nombre, descripcion | — |
| **Producto** | `producto` | idProducto, nombre, descripcion, precio, stock, proveedor, **imagen** (foto en base64, `LONGTEXT`) | → Categoria (ManyToOne) |
| **PerfilCliente** | `perfil_cliente` | idCliente, direccion, telefono, historialCrediticio, tipoCliente | → Usuario |
| **Carrito** | `carrito` | idCarrito, fechaCreacion | → Usuario |
| **DetalleCarrito** | `detalle_carrito` | idDetalleCarrito, cantidad | → Carrito, → Producto |
| **Pedido** | `pedido` | idPedido, fecha, estado, total | → Usuario |
| **DetallePedido** | `detalle_pedido` | idDetalle, cantidad, precioUnitario | → Pedido, → Producto |
| **Pago** | `pago` | idPago, fechaPago, monto, metodo, **proveedorBilletera**, **referencia**, **estado** | → Pedido, → Usuario (aprobadoPor) |
| **PlanCuotas** | `plan_cuotas` | idPlan, cantidadCuotas, interes, estado | → PerfilCliente, → Pedido |
| **Envio** | `envio` | idEnvio, direccionEnvio, empresaLogistica, estadoEnvio | → Pedido |
| **Resena** | `resena` | idResena, comentario, puntuacion (1–5), fecha | → Producto, → Usuario |

**Detalles que conviene saber explicar:**

- `Producto.imagen` guarda la foto **como texto base64** (un *data URL* tipo
  `data:image/jpeg;base64,...`) en una columna `LONGTEXT`. Así no manejamos archivos
  sueltos en el servidor y la imagen viaja en el mismo JSON del producto.
- `Usuario.contrasena` está anotada con `@JsonProperty(access = WRITE_ONLY)`: se puede
  **recibir** (al registrar) pero **nunca se envía** en las respuestas JSON.
- En `Pago`, los campos `proveedorBilletera`, `referencia` y `estado` son **opcionales**
  y se usan según el método de pago (ver sección 7).

---

## 5. Endpoints de la API

Base local: `http://localhost:8080`

### 5.1. CRUD estándar (igual para todos los módulos)

Cada módulo expone las mismas 5 operaciones. Reemplazá `{recurso}` por uno de:
`categorias`, `usuarios`, `productos`, `perfiles`, `carritos`, `detalle-carritos`,
`pedidos`, `detalle-pedidos`, `pagos`, `planes`, `envios`, `resenas`.

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/{recurso}` | Listar todos | 200 + lista |
| GET | `/{recurso}/{id}` | Obtener uno por id | 200 / 404 |
| POST | `/{recurso}` | Crear uno nuevo | 201 + creado |
| PUT | `/{recurso}/{id}` | Actualizar uno existente | 200 / 404 |
| DELETE | `/{recurso}/{id}` | Borrar uno por id | 204 / 404 |

### 5.2. Endpoints especiales

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/ping` | Verificar que el backend está vivo (`{status:"ok",...}`) |
| GET | `/productos?nombre=teclado` | Buscar productos por nombre |
| GET | `/productos?categoriaId=2` | Filtrar productos por categoría |
| POST | `/auth/register` | Registrar un cliente nuevo |
| POST | `/auth/login` | Iniciar sesión |
| GET | `/pagos/qr?idPedido=5&monto=1200` | Devuelve el payload (simulado) para dibujar un QR de pago |

### 5.3. Ejemplos

**Crear producto** — `POST /productos`
```json
{
  "nombre": "Notebook HP 15",
  "descripcion": "Intel i5, 8GB RAM, 256GB SSD",
  "precio": 620000.00,
  "stock": 10,
  "proveedor": "HP",
  "categoria": { "idCategoria": 1 },
  "imagen": "data:image/png;base64,iVBORw0KGgo..."
}
```

**Crear reseña** — `POST /resenas` (para relacionados alcanza con el id):
```json
{ "producto": { "idProducto": 1 }, "usuario": { "idUsuario": 2 }, "puntuacion": 5, "comentario": "Excelente" }
```

**Errores**: el `GlobalExceptionHandler` devuelve JSON claro — `404` cuando el id no
existe (`ResourceNotFoundException`) y `400` cuando el dato es inválido.

---

## 6. Autenticación (login/registro con BCrypt)

El login es **casero** (no usa el filtro de Spring Security). Vive en `AuthController`.

- **`POST /auth/register`** — body `{ "nombre", "email", "contrasena" }`.
  - Valida que vengan los datos, que el email no exista (si existe → `409`).
  - Guarda el usuario con rol `CLIENTE` y la contraseña **hasheada con BCrypt**.
  - Responde `201` con `UsuarioResponse` (id, nombre, email, rol) — **sin** contraseña.
- **`POST /auth/login`** — body `{ "email", "contrasena" }`.
  - Busca por email; compara con `passwordEncoder.matches(...)` contra el hash.
  - Si está mal (email o clave) → `401` con mensaje genérico (no revela cuál falló).
  - Si está bien → `200` con `UsuarioResponse`.

> El frontend guarda ese `UsuarioResponse` en `localStorage` y usa el `rol` para
> decidir si puede entrar al panel admin. No usamos tokens/JWT (alcance del proyecto).

---

## 7. Ecosistema de pagos

El checkout del frontend ofrece **7 métodos**; el backend los **registra de verdad**
en la tabla `pago`. Los pagos están **simulados** (no hay pasarela real conectada),
pero la operación queda persistida.

| Método (`Pago.metodo`) | Qué guarda además |
|---|---|
| `TARJETA` | `referencia` (TARJ-...) |
| `EFECTIVO` | — |
| `TRANSFERENCIA` | — |
| `MERCADO_PAGO` | `referencia` (MP-...) |
| `BILLETERA_VIRTUAL` | `proveedorBilletera` (MODO, Ualá, etc.) + `referencia` (BV-...) |
| `QR` | `referencia` del QR |
| `PRESTAMO_CASA` | financiación: además se crea un `PlanCuotas` (cuotas + interés) |

- `PagoService.crear()` deja `estado = "APROBADO"` por defecto si no viene.
- `GET /pagos/qr` arma un texto simulado (`NOVATECH|PEDIDO:..|MONTO:..|REF:..`) que el
  frontend convierte en imagen QR.
- **Punto de integración real de Mercado Pago:** documentado en el frontend
  (`checkout.ts`, método `iniciarPagoMercadoPago()`); el endpoint `/pagos/qr` queda
  listo para reemplazar el payload simulado por el real de la pasarela.

---

## 8. Configuración

Todo está en `src/main/resources/application.properties` y se puede sobrescribir con
**variables de entorno** (útil para deploy) usando la sintaxis `${VARIABLE:valorPorDefecto}`.

| Variable | Por defecto (local) | Para qué sirve |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/novatech_store?createDatabaseIfNotExist=true&...` | Dirección de la base |
| `DB_USER` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | (la de la máquina local) | Contraseña de MySQL |
| `CORS_ORIGINS` | `*` | Orígenes (frontend) que pueden llamar al backend |

**JPA / Hibernate:**
- `spring.jpa.hibernate.ddl-auto=update` → Hibernate **crea/actualiza las tablas solo**
  a partir de las entidades. (Por eso, si agregás un campo a una entidad, hay que
  reiniciar el backend para que cree la columna.)
- `spring.sql.init.mode=always` + `defer-datasource-initialization=true` → ejecuta
  `data.sql` en cada arranque, **después** de crear las tablas.

**CORS** (`CorsConfig.java`): usa `allowedOriginPatterns` con `*` por defecto, así el
navegador puede llamar al backend aunque se entre por un **túnel público** (Cloudflare,
etc.). En un deploy real conviene poner la URL exacta del frontend con `CORS_ORIGINS`.

---

## 9. Datos de ejemplo y usuarios demo

`data.sql` carga categorías, 4 productos, y **dos usuarios demo** con contraseña
hasheada (usa `INSERT IGNORE` + `UPDATE` para que el login funcione siempre):

| Rol | Email | Contraseña |
|---|---|---|
| ADMIN | `admin@novatech.com` | `admin123` |
| CLIENTE | `cliente@novatech.com` | `cliente123` |

> Los `$2a$10$...` del archivo son los **hashes BCrypt** de esas contraseñas reales.

---

## 10. Cómo levantar el backend

**Requisitos:** JDK 25 y MySQL 8 corriendo en `localhost:3306`.

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

Queda en `http://localhost:8080`. Cuando ves en consola
`Started BackendApplication in X seconds`, está listo.

Prueba rápida:
```bash
curl http://localhost:8080/ping
curl http://localhost:8080/productos
```

---

## 11. Cómo reiniciarlo (cuando cambia una entidad)

Como `ddl-auto=update` agrega columnas nuevas al arrancar, después de tocar una
entidad hay que reiniciar:

```powershell
# Windows: cerrar el proceso Java y volver a levantar
Get-Process -Name java | Stop-Process -Force
.\mvnw.cmd -q -DskipTests spring-boot:run
```

Los datos ya guardados en MySQL **se mantienen** (reiniciar no borra la base).

---

## 12. Deploy con Docker

Hay un `Dockerfile` (build multi-stage del backend) y un `docker-compose.yml` en la
raíz del proyecto que levanta **MySQL + backend + frontend** juntos:

```bash
docker compose up --build
```

En deploy, pasá `DB_URL`, `DB_USER`, `DB_PASSWORD` y `CORS_ORIGINS` como variables de
entorno (no dejes credenciales en el código).

---

## 13. Cómo defender este desarrollo (preguntas frecuentes)

**¿Por qué Spring Boot con arquitectura en capas?**
Separa responsabilidades: el controller maneja HTTP, el service la lógica y el
repository el acceso a datos. Es el estándar de la industria y facilita testear y mantener.

**¿Por qué JPA/Hibernate y no SQL a mano?**
Spring Data JPA genera el CRUD automáticamente desde las interfaces `Repository`, y
mapea las entidades a tablas. Escribimos menos código y menos propenso a errores.

**¿Cómo se protegen las contraseñas?**
Nunca se guardan en texto plano: se hashean con **BCrypt**. Además, el campo nunca
se devuelve en las respuestas (`WRITE_ONLY`), y el login responde con un mensaje
genérico para no filtrar si el email existe.

**¿Por qué no Spring Security completo / JWT?**
Por el alcance del proyecto: necesitábamos un login funcional sin bloquear los
endpoints del resto del equipo. Usamos solo la librería de cifrado (BCrypt) y un
login propio. La estructura permite migrar a JWT/Security más adelante.

**¿Cómo guardan las fotos de los productos?**
Como texto base64 en la columna `imagen` (`LONGTEXT`). Simplifica el despliegue (no
hay carpeta de archivos ni servidor de estáticos) y la imagen viaja en el JSON.
Para catálogos muy grandes, lo profesional sería almacenamiento de objetos (S3); para
este proyecto, base64 es suficiente y robusto.

**¿Los pagos son reales?**
Están **simulados** pero se **registran** en la base con su método, referencia y
estado. Dejamos el punto exacto donde se enchufaría Mercado Pago real (con credenciales).

**¿Por qué CORS con `*`?**
Para poder compartir la demo por un túnel público sin reconfigurar nada. En producción
se restringe al dominio real del frontend con la variable `CORS_ORIGINS`.
