# Docker para Equipo Blanco

## Arquitectura

El ecosistema Docker de Equipo Blanco se compone de:

```
┌──────────────────────────────────────────────────────────┐
│                   sso-network (bridge)                   │
│                                                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  │
│  │  Eureka  │  │  Config  │  │   Auth   │  │ Gateway  │  │
│  │ Server   │  │  Server  │  │  Service │  │   API    │  │
│  │ :8761    │  │ :8888    │  │ :8080    │  │ :8090    │  │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘  │
│                                                          │
│  ┌──────────────────┐  ┌──────────────────┐              │
│  │ prision-service  │  │   prison-web     │              │
│  │ :8081            │  │ :3002            │              │
│  └──────────────────┘  └──────────────────┘              │
└──────────────────────────────────────────────────────────┘
```

- **Infraestructura compartida** (`docker-compose.common.yml`): Eureka Server, Config Server, Auth Service, API Gateway
- **Servicios de Equipo Blanco** (`docker-compose.equipoblanco.yml`): `prision-service` (backend) y `prison-web` (frontend)
- **Base de datos**: PostgreSQL en Supabase (nube), no requiere contenedor local

---

## Requisitos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) para Windows
- Git

---

## Configuración Inicial

### 1. Archivo `.env` raíz

Crear `AICSIUDONE/.env` con las variables que necesita la infraestructura:

```env
DATASOURCE_URL=jdbc:postgresql://REEMPLAZAR_CON_TU_URL
DATASOURCE_USERNAME=REEMPLAZAR
DATASOURCE_PASSWORD=REEMPLAZAR
JWT_SECRET=<string_aleatorio_de_32_caracteres_minimo>
```

> `DATASOURCE_*` son las credenciales de Supabase compartidas.  
> `JWT_SECRET` debe ser un string aleatorio de al menos 32 caracteres, puede generarse con `openssl rand -base64 32`.

### 2. Archivo `.env` del backend

El archivo `teams/EquipoBlanco/backend/prision-service/.env` ya existe con las credenciales de Supabase:

```env
DATASOURCE_URL=jdbc:postgresql://REEMPLAZAR_CON_TU_URL
DATASOURCE_USERNAME=REEMPLAZAR
DATASOURCE_PASSWORD=REEMPLAZAR
```

### 3. Archivo `.env` del frontend (para desarrollo local sin Docker)

`teams/EquipoBlanco/frontend/prison-web/.env`:

```env
VITE_API_GATEWAY_URL=url_del_api_gateway
```

---

## Comandos

### 1. Crear network si no existe

```powershell
docker compose --env-file .env -f docker/docker-compose.common.yml up -d
```

Esto construye y levanta en orden:
1. `sso-eureka` (Service Discovery) → puerto `8761`
2. `sso-config` (Config Server) → puerto `8888`
3. `sso-auth` (Auth Service) → puerto `8080`
4. `sso-gateway` (API Gateway) → puerto `8090`

### Levantar todos los servicios

```powershell
cd AICSIUDONE
docker compose --env-file .env -f docker/docker-compose.equipoblanco.yml up -d
```

Esto construye y levanta en orden:
1. `prision-service` (Backend) → puerto `8081`
2. `prison-web` (Frontend) → puerto `3002`

### Ver estado de los servicios

```powershell
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

### Ver logs de un servicio específico

```powershell
docker logs prision-service --tail 50 -f
docker logs prison-web --tail 50 -f
```

### Detener todos los servicios

```powershell
docker compose --env-file .env -f docker/docker-compose.equipoblanco.yml down
```

### Reconstruir un servicio específico

```powershell
docker compose --env-file .env -f docker/docker-compose.equipoblanco.yml up -d --build prision-service
```

### Detener y eliminar volúmenes, imágenes y contenedores huérfanos

```powershell
docker compose --env-file .env -f docker/docker-compose.equipoblanco.yml down --remove-orphans -v
```

---

## Puertos y URLs

| Servicio | Puerto Host | URL |
|----------|-------------|-----|
| Eureka Server | `8761` | http://localhost:8761 |
| Config Server | `8888` | http://localhost:8888 |
| Auth Service | `8080` | http://localhost:8080 |
| API Gateway | `8090` | http://localhost:8090 |
| **prision-service** | `8081` | http://localhost:8081 |
| **prison-web** | `3002` | http://localhost:3002 |

### Endpoints del backend

| Endpoint | Descripción |
|----------|-------------|
| `http://localhost:8081/actuator/health` | Health check |
| `http://localhost:8081/api-docs` | OpenAPI spec (JSON) |
| `http://localhost:8081/swagger-ui.html` | Swagger UI |

---

## Solución de Problemas

### `config-server` no inicia (unhealthy)

El Config Server falla porque el volumen `config-repo` está montado como `:ro` (read-only) en `docker-compose.common.yml`, pero JGit necesita escribir en `.git/index.lock`.  
El `docker-compose.equipoblanco.yml` ya soluciona esto sobrescribiendo el volumen como `:rw`:

```yaml
config-server:
  volumes:
    - ../config-repo:/workspace/config-repo:rw
  environment:
    - SPRING_CLOUD_CONFIG_SERVER_GIT_DEFAULT_LABEL=main
```

Si aún falla, verifica que:
- La rama del repo `config-repo` se llame `main` (ejecutar `git branch` dentro de `config-repo/`)
- El `.git/` no esté corrupto (forzar recreación: `cd config-repo && rm -rf .git && git init && git add -A && git commit -m "init" && git branch -m main`)

### `prision-service` no responde en `:8081`

Verifica que el mapeo de puertos sea correcto (`8081:8081`) y que la app haya terminado de arrancar:

```powershell
docker logs prision-service --tail 5
```

Debe mostrar: `Started PrisonServiceApplication in ... seconds`.

### `prison-web` no carga en `:3002`

Verifica los logs:

```powershell
docker logs prison-web
```

Si el contenedor se detiene inmediatamente, puede ser un error de Vite. Reconstruir la imagen:

```powershell
docker compose --env-file .env -f docker/docker-compose.equipoblanco.yml up -d --build prison-web
```

### El frontend no se conecta al backend

Verifica la variable `VITE_API_GATEWAY_URL`:

```powershell
docker inspect prison-web | Select-String "VITE_API_GATEWAY"
```

Debe mostrar `http://localhost:8081`. Si apunta a otro puerto, editar `docker-compose.equipoblanco.yml` y recrear el contenedor.

---

## Notas

- `VITE_API_GATEWAY_URL` es una variable de entorno inyectada en tiempo de ejecución (Vite dev server la lee del entorno del proceso), no requiere rebuild.
- Los servicios `prision-service` y `prison-web` NO dependen del healthcheck de `config-server` ni de `auth-service`. Sus dependencias están configuradas solo a nivel de red.
- Si no se necesita la infraestructura completa (Eureka, Config, Auth, Gateway), se puede levantar solo el backend de Equipo Blanco de forma independiente con `mvn spring-boot:run`.
