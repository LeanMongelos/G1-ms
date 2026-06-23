# Despliegue en producción

## Requisitos

| Componente | Versión mínima |
|------------|----------------|
| Java | 25 |
| MySQL | 8.0 |
| Node (build FE) | 20+ |
| Nginx o similar | TLS termination |

## Variables de entorno

Copiar `backend/.env.example`:

```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DB_URL=jdbc:mysql://host:3306/novatech_store?useUnicode=true&characterEncoding=UTF-8&serverTimezone=America/Argentina/Buenos_Aires
DB_USER=novatech_app
DB_PASSWORD=<secreto>
CORS_ORIGINS=https://tienda.novatech.com.ar
```

## Perfil Spring `prod`

Archivo: `backend/src/main/resources/application-prod.properties`

- `ddl-auto=validate` — no altera schema automáticamente
- `spring.sql.init.mode=never` — no ejecuta data.sql
- SQL logging desactivado
- CORS restringido

## Base de datos

```sql
CREATE DATABASE novatech_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'novatech_app'@'%' IDENTIFIED BY 'password_seguro';
GRANT ALL PRIVILEGES ON novatech_store.* TO 'novatech_app'@'%';
FLUSH PRIVILEGES;
```

**Migración inicial:** ejecutar app una vez en dev con `ddl-auto=update` para generar schema, luego exportar con mysqldump o usar Flyway.

## Backend

```bash
cd backend
./mvnw -DskipTests package
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Health checks

```bash
curl https://api.novatech.com.ar/actuator/health
curl https://api.novatech.com.ar/ping
```

Respuesta health OK:

```json
{
  "status": "UP",
  "service": "novatech-backend",
  "components": { "db": "UP" }
}
```

## Frontend

```bash
cd frontend
npm ci
npm run build
# Artefactos en dist/G1-ui/browser
```

Servir estáticos con Nginx:

```nginx
server {
  listen 443 ssl;
  server_name tienda.novatech.com.ar;

  root /var/www/novatech/dist/G1-ui/browser;
  index index.html;

  location / {
    try_files $uri $uri/ /index.html;
  }

  location /api/ {
    proxy_pass http://127.0.0.1:8080/;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
  }
}
```

Ajustar `frontend/src/environments/environment.prod.ts` con URL API producción.

## Docker (opcional)

Ejemplo compose mínimo:

```yaml
services:
  mysql:
    image: mysql:8
    environment:
      MYSQL_DATABASE: novatech_store
      MYSQL_ROOT_PASSWORD: root
    volumes:
      - mysql_data:/var/lib/mysql

  backend:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_URL: jdbc:mysql://mysql:3306/novatech_store
      DB_USER: root
      DB_PASSWORD: root
      CORS_ORIGINS: http://localhost:4200
    ports:
      - "8080:8080"
    depends_on:
      - mysql

volumes:
  mysql_data:
```

## Checklist go-live

- [ ] MySQL con backups automáticos
- [ ] TLS/SSL en frontend y API
- [ ] CORS solo origen producción
- [ ] Secrets en vault/env, no en repo
- [ ] `data.sql` desactivado
- [ ] Health check en load balancer
- [ ] Logs centralizados (ELK, CloudWatch)
- [ ] AFIP WSFE (ver ROADMAP)
- [ ] Auth JWT server-side
- [ ] Rate limiting / WAF

## Rollback

1. Mantener JAR anterior + snapshot DB.
2. Revertir deploy frontend (artefacto estático versionado).
3. Verificar `/actuator/health` post-rollback.
