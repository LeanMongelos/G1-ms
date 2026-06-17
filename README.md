# Novatech Store - Backend

Backend del proyecto **Novatech Store** (Grupo 1).

- **Stack:** Spring Boot 4.1 · Java 25 · Spring Data JPA · H2

## Que incluye

- Proyecto Spring Boot que compila y corre.
- Base de datos **H2 en memoria** para desarrollo (no hay que instalar nada).
- Modulo de **Productos**: listar, buscar, crear, actualizar y borrar.
- Endpoint `/ping` para verificar que el backend responde.

## Requisitos

- JDK 25
- Maven NO hace falta instalarlo: se usa el wrapper `mvnw` / `mvnw.cmd`.

## Base de datos

Usamos **H2 en memoria**: se crea sola al arrancar la app y carga los datos de
ejemplo de `data.sql`. Para verla en el navegador: `http://localhost:8080/h2-console`
(JDBC URL `jdbc:h2:mem:novatech`, usuario `sa`, sin contrasena).

## Como levantar el backend

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / Mac
./mvnw spring-boot:run
```

El servidor queda en `http://localhost:8080`.

## Endpoints

| Metodo | Ruta                       | Descripcion                  |
|--------|----------------------------|------------------------------|
| GET    | `/ping`                    | Verificar que el backend vive |
| GET    | `/productos`               | Listar productos             |
| GET    | `/productos?nombre=x`      | Buscar productos por nombre  |
| GET    | `/productos?categoriaId=2` | Filtrar productos por categoria |
| GET    | `/productos/{id}`          | Obtener un producto          |
| POST   | `/productos`               | Crear un producto            |
| PUT    | `/productos/{id}`          | Actualizar un producto       |
| DELETE | `/productos/{id}`          | Borrar un producto           |

### Ejemplo: crear un producto (POST `/productos`)

```json
{
  "nombre": "Notebook HP 15",
  "descripcion": "Intel i5, 8GB RAM, 256GB SSD",
  "precio": 620000.00,
  "stock": 10,
  "proveedor": "HP",
  "categoria": { "idCategoria": 1 }
}
```

## Estructura

```
src/main/java/com/novatech/store
├── config        # Configuracion de CORS (permite que el frontend llame al backend)
├── controller    # Endpoints REST
├── entity        # Entidades JPA
├── exception     # Manejo de errores
├── repository    # Acceso a datos con Spring Data JPA
└── service       # Logica de negocio
```
