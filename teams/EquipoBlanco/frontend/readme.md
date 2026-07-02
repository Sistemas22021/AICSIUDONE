# Frontend — Sistema Integral de Gestión Penitenciaria (SIGP)

## Stack Tecnológico

| Capa | Tecnología |
|------|-----------|
| Framework | React 19 |
| Lenguaje | TypeScript (ES2023) |
| Build Tool | Vite 8 + Rolldown |
| Estilos | Tailwind CSS 3 + CSS Modules |
| Enrutamiento | React Router DOM 7 |
| HTTP Client | Axios |
| Iconos | Lucide React |
| Linter | ESLint 10 + TypeScript ESLint |
| Componentes externos | `@cell-component` (librería react privada) |

---

## CI/CD — Pipeline

### ¿Qué es CI/CD?

**CI (Continuous Integration)**: cada vez que subes código a GitHub, GitHub Actions descarga tu repo, instala dependencias, ejecuta tests y verifica que todo funciona.

**CD (Continuous Deployment)**: después de pasar los tests, el código se despliega automáticamente a Vercel.

En este frontend: **CI/CD completo** — test → build → deploy a Vercel.

### ¿Cómo funciona?

#### 1. El archivo del pipeline

```
.github/workflows/equipoblanco-prison-web.yml
```

Define:
- **Cuándo** se activa
- **Qué** hacer (2 jobs: test + build-and-deploy)
- **Dónde** (ubuntu-latest con Node 20)

#### 2. ¿Cuándo se activa?

| Evento | Ramas | Condición adicional |
|---|---|---|
| `push` (subir código) | `main`, `develop` | Cambios en `teams/EquipoBlanco/frontend/prison-web/**` |
| `pull_request` | `main`, `develop` | Cambios en la misma ruta |
| `workflow_dispatch` | Cualquiera | Manual desde GitHub.com |

> Si cambias archivos del backend, NO se activa este pipeline. Cada servicio tiene su propio pipeline independiente.

#### 3. ¿Qué hace exactamente?

```
Push a develop o main →
  Job 1: "Test"
    - checkout del repositorio
    - setup Node 20 con caché de npm
    - npm ci (instala dependencias exactas)
    - npm test (Vitest + React Testing Library)
    
  Job 2: "Build & Deploy a Vercel" (solo si Test pasó Y es push, no PR)
    - checkout
    - setup Node 20 con caché
    - npm ci
    - npm run build (Vite compila a producción)
      (inyecta VITE_API_URL desde secrets)
    - Deploy a Vercel:
      • Si es rama develop → vercel (preview)
      • Si es rama main    → vercel --prod (producción)
```

### Secrets necesarios para el pipeline

El pipeline necesita autenticarse en Vercel. Configúralos en:

```
GitHub → Settings → Secrets and variables → Actions → New repository secret
```

| Secret | Valor | ¿Para qué? |
|---|---|---|
| `VERCEL_TOKEN` | Token de Vercel | Autenticar el CLI de Vercel |
| `VERCEL_ORG_ID` | ID del team en Vercel | Identificar la organización |
| `VERCEL_PROJECT_ID_PRISON_WEB` | ID del proyecto en Vercel | Identificar el proyecto a desplegar |
| `VITE_API_URL` | URL del backend | Ej: `https://e31a2aa6b01992.lhr.life` |

#### Cómo obtener estos valores

**VERCEL_TOKEN**: https://vercel.com/account/tokens → "Create Token" → copiar `vcp_...`

**VERCEL_ORG_ID**: https://vercel.com → Dashboard → Settings → General → "Team ID"

**VERCEL_PROJECT_ID**: https://vercel.com → proyecto `equipoblanco-prision-web` → Settings → General → "Project ID"

### Códigos completos para terminal

#### Activar el pipeline (subir cambios a GitHub)

```bash
# 1. Ir al repo
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE

# 2. Verificar cambios
git status

# 3. Agregar, commitear y subir
git add .
git commit -m "descripción del cambio"
git push origin develop
# → Automáticamente GitHub Actions ejecuta Test → Build → Deploy preview a Vercel

# Cuando quieras producción:
git push origin main
# → Test → Build → Deploy --prod a Vercel
```

#### Ver el resultado

```
1. Ir a https://github.com/TU_USER/AICSIUDONE/actions
2. Click en el workflow en ejecución "[Equipo Blanco][Frontend] Prison Web"
3. Ver los logs de cada job
```

#### Deploy manual a Vercel (sin usar GitHub Actions)

```bash
# 1. Ir al frontend
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE\teams\EquipoBlanco\frontend\prison-web

# 2. (Si cambió la URL del túnel) Actualizar variable en Vercel
vercel env rm VITE_API_GATEWAY_URL production
vercel env add VITE_API_GATEWAY_URL production
# Pegar la URL del túnel activo, ej: https://XXXX.lhr.life

# 3. Reconstruir y redeployear
vercel build --prod && vercel --prod --prebuilt --yes
```

#### Flujo completo para trabajar con backend local + Vercel

```bash
# Terminal 1 - Backend (siempre abierta)
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE\teams\EquipoBlanco\backend\prision-service
.\mvnw.cmd spring-boot:run

# Terminal 2 - Túnel (siempre abierta, después de que backend inicie)
ssh -R 80:localhost:8081 nokey@localhost.run
# Copiar la URL que aparece: https://XXXX.lhr.life

# Terminal 3 - Configurar Vercel con la nueva URL y redeploy
cd C:\Users\Jeisi Rosales\Documents\SI\AICSIUDONE\teams\EquipoBlanco\frontend\prison-web
vercel env rm VITE_API_GATEWAY_URL production
vercel env add VITE_API_GATEWAY_URL production
# Pegar la URL del túnel
vercel build --prod && vercel --prod --prebuilt --yes

# Terminal 4 - Desarrollo local (opcional)
npm run dev
# Abrir http://localhost:5173
```

> **Importante**: Cada vez que reinicias el túnel SSH, la URL cambia. Debes repetir los pasos de configuración de Vercel.

### Resumen

| Situación | Comando |
|---|---|
| Subir cambios y activar pipeline | `git push origin develop` |
| Deploy manual a Vercel | `vercel build --prod && vercel --prod --prebuilt --yes` |
| Actualizar URL del backend en Vercel | `vercel env rm VITE_API_GATEWAY_URL production` + `vercel env add VITE_API_GATEWAY_URL production` |
| Iniciar túnel para backend local | `ssh -R 80:localhost:8081 nokey@localhost.run` |
| Desarrollo local | `npm run dev` |
| Tests locales | `npm test` |

---

## Túnel ngrok (front en Vercel + backend local)

Para que el frontend desplegado en Vercel pueda conectar a tu backend local:

```bash
# 1. Iniciar backend
cd teams/EquipoBlanco/backend/prision-service
./mvnw spring-boot:run

# 2. Exponer con ngrok (nueva terminal)
ngrok http 8081
# → https://XXXX.ngrok-free.app

# 3. Configurar en Vercel
cd teams/EquipoBlanco/frontend/prison-web
vercel env rm VITE_API_GATEWAY_URL production
vercel env add VITE_API_GATEWAY_URL production
# Pegar: https://XXXX.ngrok-free.app

# 4. Redeploy
vercel build --prod && vercel --prod --prebuilt --yes
```

> El túnel solo funciona mientras ngrok esté corriendo en la terminal. Al cerrarlo, la URL deja de responder.

---

## Flujo de trabajo recomendado

| Situación | Frontend | Backend | ¿Pipeline? |
|---|---|---|---|
| Desarrollo diario | `npm run dev` (local) | `./mvnw spring-boot:run` | No |
| Prueba con datos reales desde Vercel | Vercel + ngrok | `./mvnw spring-boot:run` + `ngrok http 8081` | Solo CI |
| Deploy a producción | Vercel (main) | Backend desplegado (futuro) | CI/CD completo |

---

## Despliegue manual a Vercel

Sin usar el pipeline de GitHub:

```bash
cd teams/EquipoBlanco/frontend/prison-web
vercel --prod
```

La primera vez pide configuración del proyecto. Las siguientes veces despliega automáticamente.

---

## Estructura de Carpetas

| Capa | Tecnología |
|------|-----------|
| Framework | React 19 |
| Lenguaje | TypeScript (ES2023) |
| Build Tool | Vite 8 + Rolldown |
| Estilos | Tailwind CSS 3 + CSS Modules |
| Enrutamiento | React Router DOM 7 |
| HTTP Client | Axios |
| Iconos | Lucide React |
| Linter | ESLint 10 + TypeScript ESLint |
| Componentes externos | `@cell-component` (librería react privada) |

---

## Estructura de Carpetas

```
prison-web/
├── src/
│   ├── main.tsx                                       # Entry point (ReactDOM.createRoot)
│   ├── App.tsx                                        # Router principal y rutas protegidas
│   ├── App.css                                        # Estilos legacy (counter, hero, next-steps)
│   ├── index.css                                      # Tailwind directives + reset
│   ├── vite-env.d.ts                                  # Declaraciones de tipos para Vite y @cell-component
│   ├── assets/                                        # Recursos estáticos
│   │   ├── hero.png
│   │   ├── react.svg
│   │   └── vite.svg
│   ├── modules/                                       # Módulos funcionales (pages)
│   │   ├── dashboard/
│   │   │   └── DashboardPage.tsx                      # Dashboard con métricas, occupancy chart, traslados
│   │   ├── inmates/                                   # Gestión de reclusos
│   │   │   ├── InmateRegisterPage.tsx                 # Registro de nuevo recluso
│   │   │   ├── InmateRecordPage.tsx                   # Expediente y detalle de recluso
│   │   │   ├── DischargePage.tsx                      # Egreso de recluso
│   │   │   └── TransferRequestModal.tsx               # Modal de solicitud de traslado
│   │   ├── cells/                                     # Celdas y mapa
│   │   │   ├── CellConfigPage.tsx                     # CRUD de celdas
│   │   │   └── CellMapPage.tsx                        # Mapa interactivo SVG de celdas
│   │   ├── postpenal/                                 # Seguimiento post-penitenciario
│   │   │   ├── PostPenalPage.tsx                      # Lista de expedientes con asignación
│   │   │   ├── PostPenalProfilePage.tsx               # Perfil de seguimiento (domicilio, riesgo)
│   │   │   └── CalendarioPage.tsx                     # Calendario de presentaciones
│   │   └── control/                                   # Control y alertas
│   │       ├── ControlDashboardPage.tsx               # Dashboard de control con resumen de alertas
│   │       └── PresentacionesPendientesPage.tsx        # Presentaciones del día con registro
│   └── shared/                                        # Código compartido
│       ├── api.ts                                     # Instancia Axios con interceptors (headers auth)
│       ├── authContext.tsx                             # Contexto de autenticación + MOCK_USERS
│       ├── AuthGuard.tsx                              # Wrapper que provee el contexto de auth
│       ├── ProtectedRoute.tsx                          # Guard de rutas por roles
│       ├── SidebarLayout.tsx                          # Layout con sidebar colapsable y header
│       ├── mockUser.ts                                # Utilidad para leer usuario mock de localStorage
│       └── PlaceholderPage.tsx                        # Página placeholder para módulos en desarrollo
├── index.html                                         # HTML entry point
├── vite.config.ts                                     # Configuración de Vite + alias @cell-component
├── tsconfig.json                                      # TypeScript project references
├── tsconfig.app.json                                  # Configuración TS para src/ (app)
├── tsconfig.node.json                                 # Configuración TS para archivos de configuración
├── tailwind.config.js                                 # Configuración de Tailwind CSS
├── postcss.config.js                                  # PostCSS con Tailwind y Autoprefixer
├── eslint.config.js                                   # ESLint flat config con TypeScript y React Hooks
├── .env                                               # Variables de entorno (VITE_API_GATEWAY_URL)
├── package.json
└── README.md
```

### Reglas de organización de módulos

1. Cada **módulo funcional** vive en `modules/<nombre>/`
2. Las **pages** de cada módulo son archivos individuales dentro de la carpeta del módulo (sin subcarpetas `controller/`, `dto/`, `model/`, `repository/`, `service/`)
3. El código **compartido** (api, auth, layout) vive en `shared/`
4. Cada page se construye con componentes inline o funciones auxiliares dentro del mismo archivo
5. El enrutamiento se define centralizadamente en `App.tsx`

### Cómo agregar una página nueva

1. Ubicar el módulo correspondiente o crear uno nuevo en `modules/<nombre>/`
2. Crear el archivo de página (ej: `MiPagina.tsx`) exportando un componente por defecto
3. Importar y agregar la ruta en `App.tsx` usando `<ProtectedRoute>` con los roles permitidos
4. Si la página necesita un sidebar, envolver el contenido en `<SidebarLayout>`
5. Agregar la entrada en el menú lateral editando `ALL_MENU_ITEMS` en `SidebarLayout.tsx`

---

## Control de Acceso por Roles

### Roles del Sistema

| Rol | Descripción |
|-----|-------------|
| `Administrador del Sistema` | Configura infraestructura (celdas, planos) |
| `Oficial Penitenciario` | Gestión diaria del penal (ingresos, egresos, mapa) |
| `Supervisor Penitenciario` | Supervisión del penal (aprobación de traslados) |
| `Oficial de Seguimiento` | Seguimiento post-penitenciario (presentaciones, calendario) |
| `Supervisor Policial` | Supervisión post-penitenciaria (asignación de oficiales, alertas) |

### Cómo funciona

La autenticación se maneja mediante **mock users** almacenados en `localStorage`:

1. `authContext.tsx` define `MOCK_USERS` con 5 usuarios preconfigurados (uno por rol)
2. Al iniciar, se lee `mock_user` de `localStorage` (o se establece el default: Carlos Méndez — Oficial Penitenciario)
3. `AuthGuard` envuelve la app y provee el contexto de autenticación
4. Cada ruta se protege con `<ProtectedRoute allowedRoles={[...]}>` que redirige si el rol no tiene permiso
5. `api.ts` inyecta en cada request los headers `X-User-Name` y `X-User-Role` automáticamente
6. El menú lateral (`SidebarLayout`) filtra las opciones según el rol del usuario
7. `handleLogout()` limpia `localStorage` y `sessionStorage`, forzando un reload

### Mapa de rutas y roles

| Ruta | Página | Roles Permitidos |
|------|--------|------------------|
| `/` | Redirección según rol | — |
| `/dashboard` | DashboardPage | Oficial Penitenciario, Supervisor Penitenciario, Administrador del Sistema |
| `/celdas/configurar` | CellConfigPage | Administrador del Sistema |
| `/mapa` | CellMapPage | Oficial Penitenciario, Supervisor Penitenciario, Administrador del Sistema |
| `/internos/registrar` | InmateRegisterPage | Oficial Penitenciario, Administrador del Sistema |
| `/internos/expediente/:id` | InmateRecordPage | Oficial Penitenciario, Supervisor Penitenciario, Administrador del Sistema |
| `/internos/egreso` | DischargePage | Oficial Penitenciario, Administrador del Sistema |
| `/post` | PostPenalPage | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |
| `/post/expediente/:id/perfil` | PostPenalProfilePage | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |
| `/post/expediente/:id/calendario` | CalendarioPage | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |
| `/control` | ControlDashboardPage | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |
| `/control/pendientes` | PresentacionesPendientesPage | Oficial de Seguimiento, Supervisor Policial, Administrador del Sistema |

### Cómo agregar un rol nuevo

1. Agregar el rol al tipo `UserRole` en `authContext.tsx`
2. Agregar un usuario mock en `MOCK_USERS` dentro de `authContext.tsx`
3. Agregar el rol en `allowedRoles` de las rutas correspondientes en `App.tsx`
4. Agregar el rol en los filtros de `ALL_MENU_ITEMS` en `SidebarLayout.tsx`

---

## Buenas Prácticas

- **API centralizada**: toda comunicación con el backend se realiza a través de la instancia Axios en `shared/api.ts`, que inyecta automáticamente los headers de autenticación
- **Protección de rutas**: usar `<ProtectedRoute>` con `allowedRoles` explícitos — nunca confiar solo en el menú lateral
- **Mock users**: el sistema usa usuarios simulados almacenados en `localStorage` con clave `mock_user`; cambiar de usuario implica modificar `MOCK_USERS` en `authContext.tsx`
- **Sidebar adaptativo**: el menú lateral filtra dinámicamente sus opciones según el rol del usuario autenticado
- **Modularidad**: cada página vive en su módulo funcional dentro de `modules/`; páginas reutilizables o genéricas van en `shared/`
- **Rutas protegidas**: toda ruta sensible debe envolverse en `<ProtectedRoute>` con roles explícitos

---

## Patrones de diseño implementados

### 1. **Context Pattern (React Context)**
`AuthContext` en `authContext.tsx` provee el estado de autenticación a todo el árbol de componentes sin prop drilling.

### 2. **HOC Pattern (Higher-Order Component)**
`ProtectedRoute` actúa como un HOC que verifica permisos antes de renderizar el contenido, redirigiendo si el rol no está autorizado.

### 3. **Provider Pattern**
`AuthGuard` es un wrapper provider que envuelve la aplicación con `AuthProvider`, siguiendo el patrón estándar de React Context.

### 4. **Factory Pattern (Axios Instance)**
`shared/api.ts` crea una instancia preconfigurada de Axios con `baseURL` e interceptores, funcionando como factory para todas las peticiones HTTP.

### 5. **Interceptor Pattern**
Los interceptors de Axios inyectan automáticamente los headers `X-User-Name` y `X-User-Role` en cada request, separando la lógica de autenticación de las páginas.

### 6. **Layout Pattern**
`SidebarLayout` es un layout componente que envuelve el contenido principal con sidebar y header, manteniendo una estructura consistente en todas las páginas.

### 7. **Route Guard Pattern**
`ProtectedRoute` implementa un guard de navegación que verifica roles antes de permitir el acceso a una ruta, similar a guards en frameworks backend.

### 8. **Modular Architecture**
Organización del código en módulos funcionales (`dashboard`, `inmates`, `cells`, `postpenal`, `control`) con boundaries bien definidos.

### 9. **Singleton Pattern (Axios Instance)**
La instancia de Axios en `shared/api.ts` se crea una sola vez y se reutiliza en toda la aplicación.

### 10. **Custom Hook Pattern (useAuth)**
`useAuth()` es un custom hook que encapsula la lógica de acceso al contexto de autenticación con `useContext`.

### 11. **LocalStorage Mock Pattern**
El sistema de autenticación mock utiliza `localStorage` para persistir el usuario seleccionado, permitiendo simular diferentes roles sin un backend de auth real.

### 12. **Filtered Menu Pattern**
El menú lateral filtra dinámicamente sus ítems según el rol del usuario, ocultando opciones no autorizadas y evitando navegación a rutas restringidas.
