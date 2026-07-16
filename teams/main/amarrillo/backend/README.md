# 🔧 Backend — Nexo Criminal

Spring Boot 3.2 + Java 17 + PostgreSQL + JWT.

## Requisitos previos

- Java 17+ (`java -version`)
- Maven 3.8+ (`mvn -v`)
- PostgreSQL 15+ con extensión PostGIS

## Configuración

1. Crear base de datos y aplicar schema:
   ```bash
   createdb -U postgres nexo_criminal
   psql -U postgres -d nexo_criminal -f ../database/01-schema.sql
   psql -U postgres -d nexo_criminal -f ../database/02-datos-ejemplo.sql
   ```

2. Ajustar `src/main/resources/application.properties` si tu usuario/clave de Postgres son distintos.

## Ejecución

```bash
mvn spring-boot:run
```

El backend arranca en `http://localhost:8080`.

Al primer arranque se crea el usuario `admin` / `admin123` automáticamente.

## Endpoints principales

### Autenticación
- `POST /api/v1/auth/login` → `{ "username": "admin", "password": "admin123" }`
- `POST /api/v1/auth/registrar`

### Entidades
- `GET/POST/PUT/DELETE /api/v1/personas`
- `GET/POST/PUT/DELETE /api/v1/vehiculos`
- `GET/POST/PUT/DELETE /api/v1/ubicaciones`
- `GET/POST/DELETE /api/v1/sucesos`
- `GET/POST /api/v1/relaciones`
- `GET/POST /api/v1/avistamientos`

### Motor Red Thread
- `POST /api/v1/engine/ejecutar-todo` → ejecuta las 4 reglas
- `POST /api/v1/engine/nodo-logistico`
- `POST /api/v1/engine/escolta`
- `POST /api/v1/engine/circulo-confianza`
- `POST /api/v1/engine/modus-operandi`

### Grafo
- `GET /api/v1/grafo/completo` → formato Cytoscape
- `GET /api/v1/grafo/nodo/{tipo}/{id}`

### Alertas
- `GET /api/v1/alertas`
- `GET /api/v1/alertas?pendientes=true`
- `PATCH /api/v1/alertas/{id}/estado` → `{ "estado": "CONFIRMADA" }`

## Prueba rápida con cURL

```bash
# Login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Ejecutar el motor de análisis
curl -X POST http://localhost:8080/api/v1/engine/ejecutar-todo

# Ver alertas generadas
curl http://localhost:8080/api/v1/alertas

# Ver el grafo completo
curl http://localhost:8080/api/v1/grafo/completo
```

## Estructura de paquetes

```
com.nexocriminal
├── domain/          # Entidades JPA
│   ├── persona/
│   ├── vehiculo/
│   ├── ubicacion/
│   ├── suceso/
│   ├── relacion/
│   ├── avistamiento/
│   ├── vinculo/
│   └── alerta/
├── engine/          # Red Thread Engine
├── grafo/           # Endpoint Cytoscape
└── security/        # JWT, login, usuario admin
```
