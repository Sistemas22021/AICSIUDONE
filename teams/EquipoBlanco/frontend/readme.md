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
