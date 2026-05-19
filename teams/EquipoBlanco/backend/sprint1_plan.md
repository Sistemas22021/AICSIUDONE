# Plan de Implementación — Sprint 1
## Sistema Integral de Gestión Penitenciaria · Equipo Blanco

> **Duración:** Semanas 1–3 · **Story Points:** 7  
> **Historias de usuario:** HU-S1-01, HU-S1-02, HU-S1-03, HU-S1-04

---

## Índice

1. [Instalaciones previas (hacer una sola vez)](#1-instalaciones-previas)
2. [Fase 0 — Preparar el repositorio](#2-fase-0--preparar-el-repositorio)
3. [Fase 1 — Crear el proyecto Spring Boot](#3-fase-1--crear-el-proyecto-spring-boot)
4. [Fase 2 — Configurar la base de datos](#4-fase-2--configurar-la-base-de-datos)
5. [Fase 3 — Crear el proyecto React + Vite](#5-fase-3--crear-el-proyecto-react--vite)
6. [Fase 4 — Implementar HU-S1-01 (Configurar celdas)](#6-fase-4--implementar-hu-s1-01)
7. [Fase 5 — Implementar HU-S1-02 (Mapa 2D)](#7-fase-5--implementar-hu-s1-02)
8. [Fase 6 — Implementar HU-S1-03 (Registro de interno)](#8-fase-6--implementar-hu-s1-03)
9. [Fase 7 — Implementar HU-S1-04 (Asignar celda)](#9-fase-7--implementar-hu-s1-04)
10. [Fase 8 — Dockerizar y probar todo junto](#10-fase-8--dockerizar-y-probar-todo-junto)
11. [Criterios de aceptación del Sprint](#11-criterios-de-aceptación-del-sprint)
12. [División de trabajo por integrante](#12-división-de-trabajo-por-integrante)

---

## 1. Instalaciones previas

> Cada integrante del equipo debe tener estas herramientas instaladas en su computadora antes de escribir una sola línea de código.

### 1.1 Git

Control de versiones. Permite que todos trabajen en el mismo repositorio sin pisarse.

**Windows:** Descargar en https://git-scm.com/download/win → instalar con opciones por defecto.  
**Mac:** Abrir Terminal y ejecutar: `xcode-select --install`  
**Linux:** `sudo apt install git`

Verificar que quedó instalado:
```bash
git --version
# Debe mostrar algo como: git version 2.43.0
```

Configurar identidad (hacerlo una sola vez por computadora):
```bash
git config --global user.name "Tu Nombre"
git config --global user.email "tu@email.com"
```

---

### 1.2 Java JDK 17

El lenguaje en que está escrito Spring Boot.

**Windows / Mac / Linux:** Descargar desde https://adoptium.net → seleccionar **Temurin 17 (LTS)** → instalar.

Verificar:
```bash
java -version
# Debe mostrar: openjdk version "17.x.x"

javac -version
# Debe mostrar: javac 17.x.x
```

> Si aparece una versión diferente (ej. Java 11 o Java 21), desinstalar y reinstalar Java 17.

---

### 1.3 Maven 3.9

Gestor de dependencias de Java. Descarga automáticamente Spring Boot y todas las librerías declaradas en el `pom.xml`.

**Windows:**
1. Descargar el ZIP desde https://maven.apache.org/download.cgi (Binary zip archive)
2. Descomprimir en `C:\Program Files\Apache\maven`
3. Agregar `C:\Program Files\Apache\maven\bin` a la variable de entorno `PATH`

**Mac:**
```bash
brew install maven
```

**Linux:**
```bash
sudo apt install maven
```

Verificar:
```bash
mvn -version
# Debe mostrar: Apache Maven 3.9.x
```

---

### 1.4 Node.js 20 LTS

Necesario para el frontend con React + Vite.

Descargar desde https://nodejs.org → seleccionar **20.x.x LTS** → instalar.

Verificar:
```bash
node -v
# Debe mostrar: v20.x.x

npm -v
# Debe mostrar: 10.x.x
```

---

### 1.5 Docker Desktop

Permite levantar la infraestructura del profesor (Eureka, Gateway, Auth) con un solo comando.

Descargar desde https://www.docker.com/products/docker-desktop → instalar → abrir la aplicación y dejar que termine de iniciar.

Verificar:
```bash
docker --version
# Debe mostrar: Docker version 24.x.x

docker compose version
# Debe mostrar: Docker Compose version v2.x.x
```

---

### 1.6 IntelliJ IDEA (IDE para el backend)

Editor de código recomendado para Java/Spring Boot.

Descargar la versión **Community (gratuita)** desde https://www.jetbrains.com/idea/download/

> Alternativa gratuita: VS Code con la extensión "Extension Pack for Java".

---

### 1.7 VS Code (IDE para el frontend)

Descargar desde https://code.visualstudio.com

Extensiones recomendadas (instalar desde el panel de extensiones dentro de VS Code):
- `ES7+ React/Redux/React-Native snippets`
- `Tailwind CSS IntelliSense`
- `Prettier - Code formatter`

---

### 1.8 Postman

Para probar los endpoints del backend sin necesidad del frontend.

Descargar desde https://www.postman.com/downloads/

---

## 2. Fase 0 — Preparar el repositorio

> **Responsable:** Jeisi (coordinadora) — todos deben seguir estos pasos en sus computadoras.

### 2.1 Clonar el repositorio del profesor

```bash
# Ir a la carpeta donde quieres el proyecto (ejemplo: Escritorio)
cd ~/Desktop

# Clonar
git clone https://github.com/url-del-repo-del-profesor.git
cd nombre-del-repo
```

### 2.2 Crear la rama del equipo

```bash
# Crear y cambiar a la rama del equipo
git checkout -b feature/equipo-blanco

# Verificar que estás en la rama correcta
git branch
# Debe aparecer: * feature/equipo-blanco
```

### 2.3 Crear la estructura de carpetas

Ejecutar estos comandos desde la raíz del repositorio:

```bash
mkdir -p teams/equipo-blanco/backend
mkdir -p teams/equipo-blanco/frontend
```

### 2.4 Levantar la infraestructura del profesor

Antes de hacer cualquier cosa, verificar que el boilerplate del profesor funciona:

```bash
# Desde la raíz del repositorio
docker compose -f docker/docker-compose.common.yml up --build -d

# Ver que todos los servicios están corriendo
docker compose -f docker/docker-compose.common.yml ps
```

Abrir en el navegador:
- http://localhost:8761 → debe verse el dashboard de Eureka
- http://localhost:8090 → debe responder el API Gateway
- http://localhost:8080/swagger-ui.html → debe verse la documentación del Auth Service

> Si alguno no responde, ejecutar `docker compose -f docker/docker-compose.common.yml logs -f` para ver el error.

### 2.5 Primer commit

```bash
git add teams/
git commit -m "[structural] Crear estructura de carpetas equipo-blanco"
git push origin feature/equipo-blanco
```

---

## 3. Fase 1 — Crear el proyecto Spring Boot

> **Responsable principal:** Orlando y Alexander

### 3.1 Generar el proyecto en Spring Initializr

1. Abrir https://start.spring.io en el navegador
2. Configurar así:

| Campo | Valor |
|---|---|
| Project | Maven |
| Language | Java |
| Spring Boot | 3.2.x |
| Group | `com.equipoblanco` |
| Artifact | `prison-service` |
| Name | `prison-service` |
| Package name | `com.equipoblanco.prisonservice` |
| Packaging | Jar |
| Java | 17 |

3. Agregar estas dependencias (buscarlas en el panel derecho):
   - `Spring Web`
   - `Spring Data JPA`
   - `PostgreSQL Driver`
   - `Eureka Discovery Client`
   - `Spring Boot Actuator`
   - `Lombok`
   - `Validation`

4. Clic en **Generate** → se descarga un ZIP
5. Descomprimir el ZIP y mover la carpeta a `teams/equipo-blanco/backend/prison-service/`

### 3.2 Crear la estructura de paquetes

Dentro de `src/main/java/com/equipoblanco/prisonservice/`, crear las carpetas manualmente o desde el IDE:

```
prisonservice/
├── config/
├── shared/
│   └── exception/
└── modules/
    ├── inmates/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   └── dto/
    ├── cells/
    │   ├── controller/
    │   ├── service/
    │   ├── repository/
    │   ├── model/
    │   └── dto/
    ├── dashboard/
    │   ├── controller/
    │   └── service/
    ├── postpenal/
    ├── control/
    └── alerts/
```

> Para el Sprint 1 solo se implementan `inmates/` y `cells/`. Las demás carpetas se crean vacías para que la estructura quede lista.

### 3.3 Configurar `application.yml`

Eliminar el archivo `src/main/resources/application.properties` que genera Initializr y crear en su lugar `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: EQUIPO-BLANCO-PRISON-SERVICE

  datasource:
    url: ${DATASOURCE_URL}
    username: ${DATASOURCE_USERNAME}
    password: ${DATASOURCE_PASSWORD}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

server:
  port: 8081

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info
```

### 3.4 Crear el archivo `.env` del backend

En la raíz de `teams/equipo-blanco/backend/prison-service/` crear un archivo `.env` (este archivo NO se sube a git):

```env
DATASOURCE_URL=jdbc:postgresql://tu-host-de-supabase/prison_db?sslmode=require
DATASOURCE_USERNAME=tu_usuario
DATASOURCE_PASSWORD=tu_password
```

Agregar `.env` al `.gitignore` del proyecto:
```bash
echo ".env" >> teams/equipo-blanco/backend/prison-service/.gitignore
```

### 3.5 Verificar que compila

```bash
cd teams/equipo-blanco/backend/prison-service
mvn clean compile
# Debe terminar con: BUILD SUCCESS
```

---

## 4. Fase 2 — Configurar la base de datos

> **Responsable:** Alexander

### 4.1 Crear la base de datos en Supabase

1. Ir a https://supabase.com → crear cuenta gratuita
2. Crear nuevo proyecto → nombre: `prison-db` → elegir región más cercana → contraseña segura
3. Esperar que termine de inicializar (~2 minutos)
4. Ir a **Settings → Database → Connection string** → seleccionar modo **JDBC**
5. Copiar la URL y colocarla en el `.env` del backend

### 4.2 Diseño de tablas del Sprint 1

Las entidades JPA crearán las tablas automáticamente (`ddl-auto: update`), pero es útil entender qué se va a crear:

**Tabla `cells` (celdas):**

| Columna | Tipo | Descripción |
|---|---|---|
| id | UUID | Clave primaria |
| identifier | VARCHAR | Identificador único (ej. "Celda A-01") |
| max_capacity | INTEGER | Capacidad máxima de reclusos |
| conduct_level | VARCHAR | Nivel requerido: BAJO / MEDIO / ALTO |
| length_meters | DECIMAL | Largo en metros |
| width_meters | DECIMAL | Ancho en metros |
| created_at | TIMESTAMP | Fecha de creación |

**Tabla `inmates` (internos):**

| Columna | Tipo | Descripción |
|---|---|---|
| id | UUID | Clave primaria |
| cedula | VARCHAR | Cédula de identidad (única) |
| first_name | VARCHAR | Primer nombre |
| second_name | VARCHAR | Segundo nombre |
| first_lastname | VARCHAR | Primer apellido |
| second_lastname | VARCHAR | Segundo apellido |
| birth_date | DATE | Fecha de nacimiento |
| crime | VARCHAR | Delito imputado |
| case_number | VARCHAR | Número de expediente judicial |
| court | VARCHAR | Tribunal de sentencia |
| admission_date | DATE | Fecha de ingreso |
| sentence_years | INTEGER | Años de condena |
| sentence_months | INTEGER | Meses de condena |
| release_date | DATE | Fecha estimada de liberación (calculada) |
| eye_color | VARCHAR | Color de ojos |
| hair_color | VARCHAR | Color de cabello |
| build | VARCHAR | Complexión |
| height_cm | INTEGER | Estatura en cm |
| weight_kg | DECIMAL | Peso en kg |
| distinguishing_marks | TEXT | Señas particulares |
| photo_url | VARCHAR | URL de la fotografía |
| fingerprint_url | VARCHAR | URL de la imagen de huellas |
| status | VARCHAR | Estado: ACTIVO_SIN_CELDA / ACTIVO_CON_CELDA / EGRESADO |
| cell_id | UUID (FK) | Celda asignada (puede ser null) |
| created_at | TIMESTAMP | Fecha de registro |

**Tabla `belongings` (pertenencias):**

| Columna | Tipo | Descripción |
|---|---|---|
| id | UUID | Clave primaria |
| inmate_id | UUID (FK) | Interno al que pertenece |
| description | VARCHAR | Descripción del objeto |
| quantity | INTEGER | Cantidad |
| observations | TEXT | Observaciones / estado |

**Tabla `cell_assignments` (historial de asignaciones):**

| Columna | Tipo | Descripción |
|---|---|---|
| id | UUID | Clave primaria |
| inmate_id | UUID (FK) | Interno |
| cell_id | UUID (FK) | Celda |
| assigned_by | VARCHAR | Usuario que hizo la asignación |
| assigned_at | TIMESTAMP | Fecha y hora de asignación |

---

## 5. Fase 3 — Crear el proyecto React + Vite

> **Responsable principal:** Jeisi y Samuel

### 5.1 Generar el proyecto

```bash
cd teams/equipo-blanco/frontend

npm create vite@latest prison-web -- --template react
cd prison-web
npm install
```

### 5.2 Instalar dependencias necesarias

```bash
# React Router — para navegar entre pantallas
npm install react-router-dom

# Axios — para llamar al backend
npm install axios

# Tailwind CSS — para estilos rápidos
npm install -D tailwindcss postcss autoprefixer
npx tailwindcss init -p
```

Configurar Tailwind en `tailwind.config.js`:
```js
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: { extend: {} },
  plugins: [],
}
```

Agregar en `src/index.css` (reemplazar todo el contenido):
```css
@tailwind base;
@tailwind components;
@tailwind utilities;
```

### 5.3 Crear archivo `.env`

En la raíz de `prison-web/` crear `.env`:
```env
VITE_API_GATEWAY_URL=http://localhost:8090
VITE_LOGIN_MFE_URL=http://localhost:3000
```

### 5.4 Crear el `AuthGuard`

Crear `src/shared/AuthGuard.jsx`:

```jsx
import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'

export default function AuthGuard({ children }) {
  const [checked, setChecked] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    // 1. Buscar token en la URL (viene del Login MFE)
    const params = new URLSearchParams(window.location.search)
    const tokenFromUrl = params.get('token')

    if (tokenFromUrl) {
      sessionStorage.setItem('token', tokenFromUrl)
      // Limpiar el token de la URL
      window.history.replaceState({}, '', window.location.pathname)
    }

    // 2. Verificar si hay token en sesión
    const token = sessionStorage.getItem('token')

    if (!token) {
      // 3. Si no hay token, redirigir al Login MFE del profesor
      const loginUrl = import.meta.env.VITE_LOGIN_MFE_URL
      const redirect = encodeURIComponent(window.location.href)
      window.location.href = `${loginUrl}?redirect=${redirect}`
      return
    }

    setChecked(true)
  }, [])

  if (!checked) return <div>Verificando sesión...</div>
  return children
}
```

### 5.5 Crear el cliente HTTP

Crear `src/shared/api.js`:

```js
import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_GATEWAY_URL + '/api/v1',
})

// Adjuntar el token en cada petición automáticamente
api.interceptors.request.use((config) => {
  const token = sessionStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default api
```

### 5.6 Configurar las rutas en `App.jsx`

```jsx
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import AuthGuard from './shared/AuthGuard'
import CellConfigPage from './modules/cells/CellConfigPage'
import CellMapPage from './modules/cells/CellMapPage'
import InmateRegisterPage from './modules/inmates/InmateRegisterPage'

export default function App() {
  return (
    <BrowserRouter>
      <AuthGuard>
        <Routes>
          <Route path="/" element={<Navigate to="/mapa" />} />
          <Route path="/celdas/configurar" element={<CellConfigPage />} />
          <Route path="/mapa" element={<CellMapPage />} />
          <Route path="/internos/registrar" element={<InmateRegisterPage />} />
        </Routes>
      </AuthGuard>
    </BrowserRouter>
  )
}
```

### 5.7 Verificar que el frontend arranca

```bash
cd teams/equipo-blanco/frontend/prison-web
npm run dev
# Abrir http://localhost:5173
```

---

## 6. Fase 4 — Implementar HU-S1-01

### Configurar estructura de celdas del establecimiento

> **Rol:** Administrador del Sistema  
> **Responsable backend:** Alexander · **Responsable frontend:** Samuel

---

### 6.1 Backend — Entidad `Cell`

Crear `src/main/java/com/equipoblanco/prisonservice/modules/cells/model/Cell.java`:

```java
package com.equipoblanco.prisonservice.modules.cells.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cells")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Column(nullable = false)
    private Integer maxCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConductLevel conductLevel;

    private BigDecimal lengthMeters;
    private BigDecimal widthMeters;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum ConductLevel {
        BAJO, MEDIO, ALTO
    }
}
```

### 6.2 Backend — DTO

Crear `src/main/java/com/equipoblanco/prisonservice/modules/cells/dto/CellDto.java`:

```java
package com.equipoblanco.prisonservice.modules.cells.dto;

import com.equipoblanco.prisonservice.modules.cells.model.Cell.ConductLevel;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CellDto {
    private UUID id;

    @NotBlank(message = "El identificador es obligatorio")
    private String identifier;

    @NotNull @Min(1)
    private Integer maxCapacity;

    @NotNull
    private ConductLevel conductLevel;

    private BigDecimal lengthMeters;
    private BigDecimal widthMeters;

    // Campos calculados (solo lectura)
    private Integer currentOccupancy;
    private String occupancyStatus; // DISPONIBLE, LIMITE, LLENO
}
```

### 6.3 Backend — Repository

Crear `src/main/java/com/equipoblanco/prisonservice/modules/cells/repository/CellRepository.java`:

```java
package com.equipoblanco.prisonservice.modules.cells.repository;

import com.equipoblanco.prisonservice.modules.cells.model.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CellRepository extends JpaRepository<Cell, UUID> {
    Optional<Cell> findByIdentifier(String identifier);
    boolean existsByIdentifier(String identifier);
}
```

### 6.4 Backend — Service

Crear `src/main/java/com/equipoblanco/prisonservice/modules/cells/service/CellService.java`:

```java
package com.equipoblanco.prisonservice.modules.cells.service;

import com.equipoblanco.prisonservice.modules.cells.dto.CellDto;
import com.equipoblanco.prisonservice.modules.cells.model.Cell;
import com.equipoblanco.prisonservice.modules.cells.repository.CellRepository;
import com.equipoblanco.prisonservice.modules.inmates.repository.InmateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CellService {

    private final CellRepository cellRepository;
    private final InmateRepository inmateRepository;

    public List<CellDto> getAllCells() {
        return cellRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    public CellDto createCell(CellDto dto) {
        if (cellRepository.existsByIdentifier(dto.getIdentifier())) {
            throw new RuntimeException("Ya existe una celda con el identificador: " + dto.getIdentifier());
        }
        Cell cell = Cell.builder()
            .identifier(dto.getIdentifier())
            .maxCapacity(dto.getMaxCapacity())
            .conductLevel(dto.getConductLevel())
            .lengthMeters(dto.getLengthMeters())
            .widthMeters(dto.getWidthMeters())
            .build();
        return toDto(cellRepository.save(cell));
    }

    public CellDto updateCell(UUID id, CellDto dto) {
        Cell cell = cellRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Celda no encontrada"));
        cell.setIdentifier(dto.getIdentifier());
        cell.setMaxCapacity(dto.getMaxCapacity());
        cell.setConductLevel(dto.getConductLevel());
        cell.setLengthMeters(dto.getLengthMeters());
        cell.setWidthMeters(dto.getWidthMeters());
        return toDto(cellRepository.save(cell));
    }

    public void deleteCell(UUID id) {
        Cell cell = cellRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Celda no encontrada"));
        int occupancy = inmateRepository.countByCellId(id);
        if (occupancy > 0) {
            throw new RuntimeException("No se puede eliminar la celda porque tiene reclusos activos");
        }
        cellRepository.delete(cell);
    }

    private CellDto toDto(Cell cell) {
        int occupancy = inmateRepository.countByCellId(cell.getId());
        String status = occupancy >= cell.getMaxCapacity() ? "LLENO"
            : occupancy >= cell.getMaxCapacity() * 0.8 ? "LIMITE"
            : "DISPONIBLE";

        return CellDto.builder()
            .id(cell.getId())
            .identifier(cell.getIdentifier())
            .maxCapacity(cell.getMaxCapacity())
            .conductLevel(cell.getConductLevel())
            .lengthMeters(cell.getLengthMeters())
            .widthMeters(cell.getWidthMeters())
            .currentOccupancy(occupancy)
            .occupancyStatus(status)
            .build();
    }
}
```

### 6.5 Backend — Controller

Crear `src/main/java/com/equipoblanco/prisonservice/modules/cells/controller/CellController.java`:

```java
package com.equipoblanco.prisonservice.modules.cells.controller;

import com.equipoblanco.prisonservice.modules.cells.dto.CellDto;
import com.equipoblanco.prisonservice.modules.cells.service.CellService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cells")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CellController {

    private final CellService cellService;

    @GetMapping
    public ResponseEntity<List<CellDto>> getAll() {
        return ResponseEntity.ok(cellService.getAllCells());
    }

    @PostMapping
    public ResponseEntity<CellDto> create(@Valid @RequestBody CellDto dto) {
        return ResponseEntity.ok(cellService.createCell(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CellDto> update(@PathVariable UUID id, @Valid @RequestBody CellDto dto) {
        return ResponseEntity.ok(cellService.updateCell(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cellService.deleteCell(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 6.6 Frontend — Página de configuración de celdas

Crear `src/modules/cells/CellConfigPage.jsx`:

```jsx
import { useState, useEffect } from 'react'
import api from '../../shared/api'

const CONDUCT_LEVELS = ['BAJO', 'MEDIO', 'ALTO']

export default function CellConfigPage() {
  const [cells, setCells] = useState([])
  const [form, setForm] = useState({
    identifier: '', maxCapacity: '', conductLevel: 'BAJO',
    lengthMeters: '', widthMeters: ''
  })
  const [editingId, setEditingId] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => { loadCells() }, [])

  async function loadCells() {
    const res = await api.get('/cells')
    setCells(res.data)
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    try {
      if (editingId) {
        await api.put(`/cells/${editingId}`, form)
      } else {
        await api.post('/cells', form)
      }
      setForm({ identifier: '', maxCapacity: '', conductLevel: 'BAJO', lengthMeters: '', widthMeters: '' })
      setEditingId(null)
      loadCells()
    } catch (err) {
      setError(err.response?.data?.message || 'Error al guardar la celda')
    }
  }

  async function handleDelete(id) {
    if (!confirm('¿Eliminar esta celda?')) return
    try {
      await api.delete(`/cells/${id}`)
      loadCells()
    } catch (err) {
      alert(err.response?.data?.message || 'No se puede eliminar')
    }
  }

  function handleEdit(cell) {
    setEditingId(cell.id)
    setForm({
      identifier: cell.identifier,
      maxCapacity: cell.maxCapacity,
      conductLevel: cell.conductLevel,
      lengthMeters: cell.lengthMeters || '',
      widthMeters: cell.widthMeters || ''
    })
  }

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-medium mb-6">Configuración de celdas</h1>

      {/* Formulario */}
      <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-xl p-5 mb-8">
        <h2 className="text-lg font-medium mb-4">{editingId ? 'Editar celda' : 'Nueva celda'}</h2>
        {error && <p className="text-red-600 text-sm mb-3">{error}</p>}
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="text-sm text-gray-600">Identificador *</label>
            <input className="w-full border rounded-lg px-3 py-2 mt-1"
              value={form.identifier}
              onChange={e => setForm({...form, identifier: e.target.value})}
              placeholder="Ej. Celda A-01" required />
          </div>
          <div>
            <label className="text-sm text-gray-600">Capacidad máxima *</label>
            <input type="number" min="1" className="w-full border rounded-lg px-3 py-2 mt-1"
              value={form.maxCapacity}
              onChange={e => setForm({...form, maxCapacity: e.target.value})}
              required />
          </div>
          <div>
            <label className="text-sm text-gray-600">Nivel de conducta *</label>
            <select className="w-full border rounded-lg px-3 py-2 mt-1"
              value={form.conductLevel}
              onChange={e => setForm({...form, conductLevel: e.target.value})}>
              {CONDUCT_LEVELS.map(l => <option key={l}>{l}</option>)}
            </select>
          </div>
          <div>
            <label className="text-sm text-gray-600">Largo (m)</label>
            <input type="number" step="0.01" className="w-full border rounded-lg px-3 py-2 mt-1"
              value={form.lengthMeters}
              onChange={e => setForm({...form, lengthMeters: e.target.value})} />
          </div>
          <div>
            <label className="text-sm text-gray-600">Ancho (m)</label>
            <input type="number" step="0.01" className="w-full border rounded-lg px-3 py-2 mt-1"
              value={form.widthMeters}
              onChange={e => setForm({...form, widthMeters: e.target.value})} />
          </div>
        </div>
        <div className="flex gap-2 mt-4">
          <button type="submit" className="bg-blue-600 text-white px-5 py-2 rounded-lg text-sm">
            {editingId ? 'Actualizar' : 'Crear celda'}
          </button>
          {editingId && (
            <button type="button" onClick={() => { setEditingId(null); setForm({ identifier:'', maxCapacity:'', conductLevel:'BAJO', lengthMeters:'', widthMeters:'' }) }}
              className="border px-5 py-2 rounded-lg text-sm">Cancelar</button>
          )}
        </div>
      </form>

      {/* Lista de celdas */}
      <table className="w-full text-sm border-collapse">
        <thead>
          <tr className="border-b text-left text-gray-500">
            <th className="pb-2">Identificador</th>
            <th className="pb-2">Capacidad</th>
            <th className="pb-2">Ocupación</th>
            <th className="pb-2">Nivel conducta</th>
            <th className="pb-2">Estado</th>
            <th className="pb-2">Acciones</th>
          </tr>
        </thead>
        <tbody>
          {cells.map(cell => (
            <tr key={cell.id} className="border-b py-2">
              <td className="py-3 font-medium">{cell.identifier}</td>
              <td>{cell.maxCapacity}</td>
              <td>{cell.currentOccupancy}/{cell.maxCapacity}</td>
              <td>{cell.conductLevel}</td>
              <td>
                <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                  cell.occupancyStatus === 'DISPONIBLE' ? 'bg-green-100 text-green-800' :
                  cell.occupancyStatus === 'LIMITE' ? 'bg-yellow-100 text-yellow-800' :
                  'bg-red-100 text-red-800'}`}>
                  {cell.occupancyStatus}
                </span>
              </td>
              <td className="flex gap-2 py-3">
                <button onClick={() => handleEdit(cell)} className="text-blue-600 hover:underline text-xs">Editar</button>
                <button onClick={() => handleDelete(cell.id)} className="text-red-500 hover:underline text-xs">Eliminar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
```

### 6.7 Registrar la ruta en el API Gateway del profesor

En el archivo `GatewayConfig.java` del profesor agregar:

```java
.route("equipo-blanco-prison", r -> r
    .path("/api/v1/cells/**", "/api/v1/inmates/**")
    .filters(f -> f.filter(jwtAuthFilter))
    .uri("lb://EQUIPO-BLANCO-PRISON-SERVICE"))
```

---

## 7. Fase 5 — Implementar HU-S1-02

### Visualizar mapa 2D de celdas y ocupación

> **Rol:** Oficial / Supervisor Penitenciario  
> **Responsable:** Jeisi + Samuel (frontend únicamente, los datos vienen del endpoint de celdas ya creado)

### 7.1 Frontend — Mapa 2D

Crear `src/modules/cells/CellMapPage.jsx`:

```jsx
import { useState, useEffect } from 'react'
import api from '../../shared/api'

const COLOR = {
  DISPONIBLE: { bg: 'bg-green-100 border-green-400', text: 'text-green-800', dot: 'bg-green-500' },
  LIMITE:     { bg: 'bg-yellow-100 border-yellow-400', text: 'text-yellow-800', dot: 'bg-yellow-500' },
  LLENO:      { bg: 'bg-red-100 border-red-400', text: 'text-red-800', dot: 'bg-red-500' },
}

export default function CellMapPage() {
  const [cells, setCells] = useState([])
  const [tooltip, setTooltip] = useState(null)
  const [summary, setSummary] = useState({ total: 0, active: 0, available: 0, occupancyPct: 0 })

  useEffect(() => {
    loadCells()
    const interval = setInterval(loadCells, 5000)
    return () => clearInterval(interval)
  }, [])

  async function loadCells() {
    const res = await api.get('/cells')
    const data = res.data
    setCells(data)
    const total = data.length
    const active = data.reduce((s, c) => s + c.currentOccupancy, 0)
    const capacity = data.reduce((s, c) => s + c.maxCapacity, 0)
    const available = data.filter(c => c.occupancyStatus !== 'LLENO').length
    setSummary({
      total,
      active,
      available,
      occupancyPct: capacity > 0 ? Math.round((active / capacity) * 100) : 0
    })
  }

  return (
    <div className="p-6 max-w-5xl mx-auto">
      <h1 className="text-2xl font-medium mb-2">Mapa de celdas</h1>

      {/* Panel resumen */}
      <div className="grid grid-cols-4 gap-3 mb-6">
        {[
          { label: 'Total celdas', value: summary.total },
          { label: 'Reclusos activos', value: summary.active },
          { label: 'Celdas disponibles', value: summary.available },
          { label: 'Ocupación general', value: `${summary.occupancyPct}%` },
        ].map(item => (
          <div key={item.label} className="bg-gray-50 rounded-xl p-4">
            <p className="text-xs text-gray-500 mb-1">{item.label}</p>
            <p className="text-2xl font-medium">{item.value}</p>
          </div>
        ))}
      </div>

      {/* Leyenda */}
      <div className="flex gap-4 mb-4 text-xs text-gray-600">
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-green-500 inline-block"></span>Disponible (&lt;80%)</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-yellow-500 inline-block"></span>Al límite (≥80%)</span>
        <span className="flex items-center gap-1"><span className="w-3 h-3 rounded-full bg-red-500 inline-block"></span>Lleno (100%)</span>
      </div>

      {/* Grilla de celdas */}
      <div className="grid grid-cols-4 gap-3">
        {cells.map(cell => {
          const colors = COLOR[cell.occupancyStatus]
          return (
            <div key={cell.id}
              className={`relative border-2 rounded-xl p-4 cursor-pointer transition-all ${colors.bg}`}
              onMouseEnter={() => setTooltip(cell)}
              onMouseLeave={() => setTooltip(null)}>
              <div className={`flex items-center gap-2 font-medium text-sm ${colors.text}`}>
                <span className={`w-2.5 h-2.5 rounded-full ${colors.dot}`}></span>
                {cell.identifier}
              </div>
              <div className="mt-2 text-xs text-gray-600">
                <p>{cell.currentOccupancy} / {cell.maxCapacity} reclusos</p>
              </div>

              {/* Tooltip */}
              {tooltip?.id === cell.id && (
                <div className="absolute z-10 top-full left-0 mt-1 bg-white border border-gray-200 rounded-lg shadow-md p-3 text-xs w-48">
                  <p><span className="text-gray-500">Capacidad:</span> {cell.maxCapacity}</p>
                  <p><span className="text-gray-500">Actuales:</span> {cell.currentOccupancy}</p>
                  <p><span className="text-gray-500">Nivel conducta:</span> {cell.conductLevel}</p>
                  <p><span className="text-gray-500">Estado:</span> {cell.occupancyStatus}</p>
                </div>
              )}
            </div>
          )
        })}
      </div>
    </div>
  )
}
```

---

## 8. Fase 6 — Implementar HU-S1-03

### Registrar ingreso completo de un recluso

> **Responsable backend:** Alexander · **Responsable frontend:** Jeisi

### 8.1 Backend — Entidad `Inmate`

Crear `src/main/java/com/equipoblanco/prisonservice/modules/inmates/model/Inmate.java`:

```java
package com.equipoblanco.prisonservice.modules.inmates.model;

import com.equipoblanco.prisonservice.modules.cells.model.Cell;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "inmates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Inmate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String cedula;

    @Column(nullable = false) private String firstName;
    private String secondName;
    @Column(nullable = false) private String firstLastname;
    private String secondLastname;
    private LocalDate birthDate;

    // Información judicial
    private String crime;
    private String caseNumber;
    private String court;

    // Condena
    private LocalDate admissionDate;
    private Integer sentenceYears;
    private Integer sentenceMonths;
    private LocalDate estimatedReleaseDate;

    // Características físicas
    private String eyeColor;
    private String hairColor;
    private String build;
    private Integer heightCm;
    private BigDecimal weightKg;
    @Column(columnDefinition = "TEXT")
    private String distinguishingMarks;

    // Biométricos
    private String photoUrl;
    private String fingerprintUrl;

    @Enumerated(EnumType.STRING)
    private InmateStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cell_id")
    private Cell cell;

    @OneToMany(mappedBy = "inmate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Belonging> belongings;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = InmateStatus.ACTIVO_SIN_CELDA;
    }

    public enum InmateStatus {
        ACTIVO_SIN_CELDA, ACTIVO_CON_CELDA, EGRESADO
    }
}
```

Crear `src/main/java/com/equipoblanco/prisonservice/modules/inmates/model/Belonging.java`:

```java
package com.equipoblanco.prisonservice.modules.inmates.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "belongings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Belonging {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmate_id", nullable = false)
    private Inmate inmate;

    private String description;
    private Integer quantity;
    @Column(columnDefinition = "TEXT")
    private String observations;
}
```

### 8.2 Backend — Repository

Crear `src/main/java/com/equipoblanco/prisonservice/modules/inmates/repository/InmateRepository.java`:

```java
package com.equipoblanco.prisonservice.modules.inmates.repository;

import com.equipoblanco.prisonservice.modules.inmates.model.Inmate;
import com.equipoblanco.prisonservice.modules.inmates.model.Inmate.InmateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InmateRepository extends JpaRepository<Inmate, UUID> {
    Optional<Inmate> findByCedulaAndStatusNot(String cedula, InmateStatus status);
    boolean existsByCedulaAndStatusNot(String cedula, InmateStatus status);

    @Query("SELECT COUNT(i) FROM Inmate i WHERE i.cell.id = :cellId AND i.status = 'ACTIVO_CON_CELDA'")
    int countByCellId(UUID cellId);

    List<Inmate> findByStatus(InmateStatus status);
}
```

### 8.3 Backend — Service

Crear `src/main/java/com/equipoblanco/prisonservice/modules/inmates/service/InmateService.java`:

```java
package com.equipoblanco.prisonservice.modules.inmates.service;

import com.equipoblanco.prisonservice.modules.inmates.dto.InmateDto;
import com.equipoblanco.prisonservice.modules.inmates.model.Belonging;
import com.equipoblanco.prisonservice.modules.inmates.model.Inmate;
import com.equipoblanco.prisonservice.modules.inmates.model.Inmate.InmateStatus;
import com.equipoblanco.prisonservice.modules.inmates.repository.InmateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InmateService {

    private final InmateRepository inmateRepository;

    public boolean cedulaHasActiveRecord(String cedula) {
        return inmateRepository.existsByCedulaAndStatusNot(cedula, InmateStatus.EGRESADO);
    }

    public InmateDto register(InmateDto dto) {
        if (cedulaHasActiveRecord(dto.getCedula())) {
            throw new RuntimeException("Ya existe un expediente activo con la cédula: " + dto.getCedula());
        }

        // Calcular fecha estimada de liberación
        LocalDate release = dto.getAdmissionDate()
            .plusYears(dto.getSentenceYears() != null ? dto.getSentenceYears() : 0)
            .plusMonths(dto.getSentenceMonths() != null ? dto.getSentenceMonths() : 0);

        Inmate inmate = Inmate.builder()
            .cedula(dto.getCedula())
            .firstName(dto.getFirstName())
            .secondName(dto.getSecondName())
            .firstLastname(dto.getFirstLastname())
            .secondLastname(dto.getSecondLastname())
            .birthDate(dto.getBirthDate())
            .crime(dto.getCrime())
            .caseNumber(dto.getCaseNumber())
            .court(dto.getCourt())
            .admissionDate(dto.getAdmissionDate())
            .sentenceYears(dto.getSentenceYears())
            .sentenceMonths(dto.getSentenceMonths())
            .estimatedReleaseDate(release)
            .eyeColor(dto.getEyeColor())
            .hairColor(dto.getHairColor())
            .build_(dto.getBuild())
            .heightCm(dto.getHeightCm())
            .weightKg(dto.getWeightKg())
            .distinguishingMarks(dto.getDistinguishingMarks())
            .photoUrl(dto.getPhotoUrl())
            .fingerprintUrl(dto.getFingerprintUrl())
            .status(InmateStatus.ACTIVO_SIN_CELDA)
            .build();

        // Registrar pertenencias
        if (dto.getBelongings() != null) {
            List<Belonging> belongings = dto.getBelongings().stream().map(b ->
                Belonging.builder()
                    .inmate(inmate)
                    .description(b.getDescription())
                    .quantity(b.getQuantity())
                    .observations(b.getObservations())
                    .build()
            ).toList();
            inmate.setBelongings(belongings);
        }

        return toDto(inmateRepository.save(inmate));
    }

    public List<InmateDto> getByStatus(InmateStatus status) {
        return inmateRepository.findByStatus(status).stream()
            .map(this::toDto).toList();
    }

    private InmateDto toDto(Inmate i) {
        return InmateDto.builder()
            .id(i.getId())
            .cedula(i.getCedula())
            .firstName(i.getFirstName())
            .firstLastname(i.getFirstLastname())
            .status(i.getStatus())
            .estimatedReleaseDate(i.getEstimatedReleaseDate())
            .build();
    }
}
```

### 8.4 Backend — Controller

Crear `src/main/java/com/equipoblanco/prisonservice/modules/inmates/controller/InmateController.java`:

```java
package com.equipoblanco.prisonservice.modules.inmates.controller;

import com.equipoblanco.prisonservice.modules.inmates.dto.InmateDto;
import com.equipoblanco.prisonservice.modules.inmates.model.Inmate.InmateStatus;
import com.equipoblanco.prisonservice.modules.inmates.service.InmateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inmates")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InmateController {

    private final InmateService inmateService;

    @GetMapping("/check-cedula/{cedula}")
    public ResponseEntity<Map<String, Boolean>> checkCedula(@PathVariable String cedula) {
        boolean exists = inmateService.cedulaHasActiveRecord(cedula);
        return ResponseEntity.ok(Map.of("hasActiveRecord", exists));
    }

    @PostMapping
    public ResponseEntity<InmateDto> register(@RequestBody InmateDto dto) {
        return ResponseEntity.ok(inmateService.register(dto));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<InmateDto>> getUnassigned() {
        return ResponseEntity.ok(inmateService.getByStatus(InmateStatus.ACTIVO_SIN_CELDA));
    }
}
```

---

## 9. Fase 7 — Implementar HU-S1-04

### Asignar recluso a una celda desde el mapa

> **Responsable backend:** Orlando · **Responsable frontend:** Jeisi

### 9.1 Backend — Service de asignación

Agregar en `CellService.java` el método:

```java
public CellDto assignInmate(UUID cellId, UUID inmateId, String assignedBy) {
    Cell cell = cellRepository.findById(cellId)
        .orElseThrow(() -> new RuntimeException("Celda no encontrada"));

    int occupancy = inmateRepository.countByCellId(cellId);
    if (occupancy >= cell.getMaxCapacity()) {
        throw new RuntimeException("La celda está llena");
    }

    Inmate inmate = inmateRepository.findById(inmateId)
        .orElseThrow(() -> new RuntimeException("Recluso no encontrado"));

    if (inmate.getStatus() == Inmate.InmateStatus.ACTIVO_CON_CELDA) {
        throw new RuntimeException("El recluso ya tiene celda asignada");
    }

    inmate.setCell(cell);
    inmate.setStatus(Inmate.InmateStatus.ACTIVO_CON_CELDA);
    inmateRepository.save(inmate);

    // Registrar en historial
    CellAssignment assignment = CellAssignment.builder()
        .inmate(inmate)
        .cell(cell)
        .assignedBy(assignedBy)
        .assignedAt(LocalDateTime.now())
        .build();
    cellAssignmentRepository.save(assignment);

    return toDto(cell);
}
```

Agregar en `CellController.java`:

```java
@PostMapping("/{cellId}/assign/{inmateId}")
public ResponseEntity<CellDto> assign(
        @PathVariable UUID cellId,
        @PathVariable UUID inmateId,
        @RequestHeader("X-User-Name") String assignedBy) {
    return ResponseEntity.ok(cellService.assignInmate(cellId, inmateId, assignedBy));
}
```

### 9.2 Frontend — Modal de asignación en el mapa

Actualizar `CellMapPage.jsx` para que las celdas disponibles abran un modal de asignación:

```jsx
// Agregar estado del modal
const [assignModal, setAssignModal] = useState(null) // cell seleccionada
const [unassigned, setUnassigned] = useState([])

async function openAssignModal(cell) {
  if (cell.occupancyStatus === 'LLENO') return
  const res = await api.get('/inmates/unassigned')
  setUnassigned(res.data)
  setAssignModal(cell)
}

async function handleAssign(inmateId) {
  await api.post(`/cells/${assignModal.id}/assign/${inmateId}`)
  setAssignModal(null)
  loadCells()
}

// En el JSX, hacer las celdas clickeables y agregar el modal al final:
// onClick={() => openAssignModal(cell)}

{assignModal && (
  <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
    <div className="bg-white rounded-xl p-6 w-96 max-h-[80vh] overflow-y-auto">
      <h2 className="text-lg font-medium mb-1">Asignar a {assignModal.identifier}</h2>
      <p className="text-sm text-gray-500 mb-4">Selecciona un recluso sin celda asignada</p>
      {unassigned.length === 0
        ? <p className="text-sm text-gray-400">No hay reclusos pendientes de asignación</p>
        : unassigned.map(inmate => (
          <div key={inmate.id}
            className="flex items-center justify-between border-b py-3 hover:bg-gray-50 cursor-pointer px-2 rounded"
            onClick={() => handleAssign(inmate.id)}>
            <div>
              <p className="font-medium text-sm">{inmate.firstName} {inmate.firstLastname}</p>
              <p className="text-xs text-gray-500">C.I. {inmate.cedula}</p>
            </div>
            <span className="text-blue-600 text-xs">Asignar →</span>
          </div>
        ))
      }
      <button onClick={() => setAssignModal(null)}
        className="mt-4 w-full border rounded-lg py-2 text-sm text-gray-600">
        Cancelar
      </button>
    </div>
  </div>
)}
```

---

## 10. Fase 8 — Dockerizar y probar todo junto

> **Responsable:** Orlando

### 10.1 Crear el `Dockerfile` del backend

En `teams/equipo-blanco/backend/prison-service/Dockerfile`:

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apk add --no-cache maven && mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 10.2 Crear el `Dockerfile` del frontend

En `teams/equipo-blanco/frontend/prison-web/Dockerfile`:

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
```

### 10.3 Crear el `docker-compose.yml` del equipo

En `teams/equipo-blanco/docker-compose.yml`:

```yaml
services:
  prison-service:
    build: ./backend/prison-service
    ports:
      - "8081:8081"
    environment:
      - DATASOURCE_URL=${DATASOURCE_URL}
      - DATASOURCE_USERNAME=${DATASOURCE_USERNAME}
      - DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD}
      - EUREKA_SERVER_URL=http://eureka-server:8761/eureka/
    networks:
      - sso-network

  prison-web:
    build: ./frontend/prison-web
    ports:
      - "3002:80"
    networks:
      - sso-network

networks:
  sso-network:
    external: true
    name: docker_sso-network
```

### 10.4 Levantar todo el sistema

```bash
# Terminal 1: infraestructura del profesor
docker compose -f docker/docker-compose.common.yml up -d

# Terminal 2: nuestro sistema
cd teams/equipo-blanco
docker compose up --build
```

### 10.5 Verificar que todo funciona

| URL | Qué debe mostrar |
|---|---|
| http://localhost:8761 | Eureka con `EQUIPO-BLANCO-PRISON-SERVICE` registrado |
| http://localhost:8081/actuator/health | `{"status":"UP"}` |
| http://localhost:3002 | Frontend del sistema |

---

## 11. Criterios de aceptación del Sprint

Antes de dar el Sprint por terminado, verificar cada punto:

### HU-S1-01 — Configurar celdas
- [ ] El administrador puede crear una celda con todos sus campos
- [ ] No se pueden crear dos celdas con el mismo identificador
- [ ] Se puede editar cualquier campo de una celda existente
- [ ] No se puede eliminar una celda con reclusos activos
- [ ] La lista de celdas se muestra correctamente

### HU-S1-02 — Mapa 2D
- [ ] El mapa muestra todas las celdas creadas
- [ ] Las celdas verdes tienen menos del 80% de ocupación
- [ ] Las celdas amarillas tienen 80% o más
- [ ] Las celdas rojas están al 100%
- [ ] El tooltip muestra capacidad, ocupación actual y nivel de conducta
- [ ] El panel resumen muestra totales correctos

### HU-S1-03 — Registro de interno
- [ ] El formulario tiene las 6 secciones completas
- [ ] El botón "Verificar cédula" detecta duplicados activos
- [ ] La fecha estimada de liberación se calcula automáticamente
- [ ] La edad se calcula sola desde la fecha de nacimiento
- [ ] Se pueden agregar múltiples pertenencias
- [ ] El expediente se guarda con estado "Activo — Sin celda asignada"

### HU-S1-04 — Asignar celda
- [ ] Las celdas llenas (rojo) no son seleccionables
- [ ] Al hacer clic en una celda disponible, aparece la lista de reclusos sin celda
- [ ] Al confirmar, el mapa actualiza el contador de la celda
- [ ] El expediente del recluso cambia a "Activo — Con celda asignada"
- [ ] No se puede asignar dos veces el mismo recluso

---

## 12. División de trabajo por integrante

| Integrante | Tareas del Sprint 1 |
|---|---|
| **Jeisi** | Fase 0 completa · AuthGuard · api.js · App.jsx · CellMapPage.jsx · InmateRegisterPage.jsx (frontend HU-S1-03) · Modal de asignación (frontend HU-S1-04) |
| **Orlando** | Servicio de asignación en CellService · assignInmate endpoint · Dockerfile backend · docker-compose.yml |
| **Alexander** | Supabase + BD · Entidades Cell, Inmate, Belonging, CellAssignment · Repositories · InmateService · CellService base |
| **Samuel** | CellConfigPage.jsx (HU-S1-01) · CellMapPage.jsx (apoyo HU-S1-02) |
| **Santiago** | Infraestructura del profesor (verificar que corre) · Apoyo con WebSocket config (preparar para Sprint 2) |

---

## Orden recomendado de ejecución

```
Semana 1:
  Día 1-2: Todos instalan herramientas (Sección 1)
  Día 2:   Jeisi crea la rama y estructura (Fase 0)
  Día 3:   Alexander genera Spring Boot y Supabase (Fases 1-2)
  Día 4:   Jeisi y Samuel generan React + Vite (Fase 3)
  Día 5:   Primera reunión de sincronización

Semana 2:
  Alexander + Orlando: Entidades, repositories, services (Fases 4-6 backend)
  Jeisi + Samuel: Páginas del frontend en paralelo (Fases 4-7 frontend)

Semana 3:
  Día 1-2: HU-S1-04 completa (asignación)
  Día 3:   Dockerizar (Fase 8)
  Día 4:   Pruebas con todos los criterios de aceptación (Sección 11)
  Día 5:   Commit final + push + pull request
```

---

*Equipo Blanco · Universidad de Oriente · Sección 0520 · Semestre I-2026*
