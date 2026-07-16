# Frontend — Sistema Integral de Gestión Penitenciaria (SIGP)

## Cómo Levantar el Proyecto

1. **Ir a la carpeta del frontend:**
   ```bash
   cd .../AICSIUDONE/teams/EquipoBlanco/frontend/prison-web
   ```

2. **Instalar dependencias:**
   ```bash
   npm install
   ```

3. **Ejecutar en modo desarrollo:**
   ```bash
   npm run dev
   ```

   La aplicación se levantará en `http://localhost:5173`.

4. **Para ejecutar los tests:**
   ```bash
   npm test
   ```

> **Nota:** Requiere Node.js 20+. Si no tienes configurada la variable `VITE_API_GATEWAY_URL` en `.env`, el frontend apuntará por defecto a `http://localhost:8081` (backend local).

---

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

## Estructura del Proyecto

El proyecto está diseñado bajo una arquitectura modular que separa cada funcionalidad en su propio directorio:

```
prison-web/
├── .env                                 # Variables de entorno (VITE_API_GATEWAY_URL)
├── index.html                           # HTML entry point
├── package.json                         # Dependencias y scripts
├── vite.config.ts                       # Configuración de Vite + alias @cell-component + Vitest
├── tsconfig.json                        # TypeScript project references
├── tsconfig.app.json                    # Configuración TS para src/ (app)
├── tsconfig.node.json                   # Configuración TS para archivos de configuración
├── tailwind.config.js                   # Configuración de Tailwind CSS
├── postcss.config.js                    # PostCSS con Tailwind y Autoprefixer
├── eslint.config.js                     # ESLint flat config con TypeScript y React Hooks
├── vercel.json                          # Configuración de despliegue en Vercel
├── public/                              # Recursos estáticos públicos
└── src/
    ├── main.tsx                         # Entry point (ReactDOM.createRoot)
    ├── App.tsx                          # Router principal y rutas protegidas
    ├── App.test.tsx                     # Test de smoke del componente App
    ├── index.css                        # Tailwind directives + reset
    ├── modules/                         # Módulos funcionales (páginas)
    │   ├── dashboard/                   # Dashboard con métricas y resumen del penal
    │   ├── inmates/                     # Gestión de reclusos (registro, expediente, egresos, incidentes)
    │   ├── cells/                       # Celdas y mapa penitenciario (CRUD, mapa SVG)
    │   ├── postpenal/                   # Seguimiento post-penitenciario (expedientes, calendario)
    │   └── control/                     # Control, disciplina e incidentes (dashboard, bitácora)
    ├── shared/                          # Código compartido (API, auth, layout)
    └── test/                            # Configuración global de tests
```

---

## Pipelines Implementados (CI/CD)

El proyecto cuenta con integración y despliegue continuos configurados a través de GitHub Actions.

- **Archivo que lo constituye:** `.github/workflows/equipoblanco-prison-web.yml` ubicado en la raíz del repositorio.
- **Cómo ejecutarlo:**
  - Se ejecuta automáticamente ante cada evento `push` o `pull_request` hacia las ramas `main` y `develop` (siempre que existan modificaciones dentro de `teams/EquipoBlanco/frontend/prison-web/**`).
  - También puede lanzarse de manera manual desde la pestaña *Actions* de GitHub seleccionando el flujo y presionando *Run workflow* (`workflow_dispatch`).
- **Qué hace el pipeline:** Prepara un entorno Linux con Node 20, instala dependencias exactas (`npm ci`), ejecuta las pruebas unitarias con Vitest (`npm test`), compila el código a producción con Vite (`npm run build`) y despliega a Vercel (preview en develop, producción en main).

---

## Tests Implementados

Se han desarrollado pruebas unitarias utilizando **Vitest** + **React Testing Library** con un entorno simulado via **jsdom**, garantizando el correcto funcionamiento del enrutamiento y la protección por roles.

- **Archivo de test:** `src/App.test.tsx` — cubre los siguientes casos:
  - **Renderizado:** Verifica que la aplicación renderiza el branding "SIGP" sin errores.
  - **Redirección por rol:** Comprueba que `Oficial Penitenciario` es redirigido a `/dashboard` y `Administrador del Sistema` a `/celdas/configurar` al acceder a la raíz.
  - **Protección de rutas:** Verifica que un `Oficial Penitenciario` no puede acceder a rutas exclusivas de `Supervisor` (ej: `/incidentes`).
- **Configuración global:** `src/test/setup.ts` — importa `@testing-library/jest-dom`.
- **Configuración en Vite:** definida en `vite.config.ts` con `globals: true`, `environment: 'jsdom'` y `setupFiles`.
- **Cómo ejecutarlos:**
  Desde la carpeta raíz del frontend (`prison-web`), ejecuta:
  ```bash
  npm test
  ```

---

## Regla de Organización de Módulos y Cómo Agregar un Nuevo Feature

El proyecto separa estrictamente la funcionalidad por módulos. Para agregar una nueva página de manera limpia, sigue estas reglas:

1. **Identifica el Módulo:** Ubícate en la carpeta correcta dentro de `src/modules/` o crea una nueva.
2. **Crea la Página:** Crea el archivo `.tsx` exportando un componente por defecto.
3. **Agrega la Ruta:** Importa y agrega la ruta en `App.tsx` usando `<ProtectedRoute>` con los roles permitidos.
4. **Incorpora al Menú:** Agrega la entrada en `ALL_MENU_ITEMS` dentro de `SidebarLayout.tsx` con los roles correspondientes.
5. **Utiliza la API Centralizada:** Toda comunicación con el backend debe realizarse a través de la instancia Axios en `shared/api.ts`, que inyecta automáticamente los headers de autenticación.

---

## Roles del Sistema y Flujo de Autenticación

El frontend está diseñado para trabajar con un sistema de autenticación mock que replica el flujo que el API Gateway realizaría con JWT real.

- **Flujo:** La autenticación se maneja mediante **mock users** almacenados en `localStorage`. El archivo `authContext.tsx` define `MOCK_USERS` con 4 usuarios preconfigurados (uno por rol) y crea un `AuthContext` de React que expone el estado de autenticación (`username`, `role`, `hasRole()`).
- **Funcionamiento:** `AuthGuard` envuelve la aplicación con el `AuthProvider`. En cada petición, el interceptor de Axios en `shared/api.ts` lee el usuario desde `localStorage`, valida el rol e inyecta las cabeceras HTTP `X-User-Name` y `X-User-Role`. `ProtectedRoute` verifica que el usuario tenga el rol necesario antes de renderizar el contenido. El menú lateral (`SidebarLayout`) filtra sus opciones según el rol autenticado.
- Gracias a este flujo, las rutas pueden protegerse simplemente envolviéndolas con `<ProtectedRoute allowedRoles={[...]}>`.

---

## Implementaciones que siguen el Patrón SOLID

1. **Single Responsibility Principle (SRP - Responsabilidad Única):** Está presente en toda la arquitectura. `authContext.tsx` se encarga exclusivamente de la autenticación, `ProtectedRoute.tsx` solo verifica permisos de ruta, `SidebarLayout.tsx` gestiona únicamente el layout, y cada página en `modules/` se concentra en su funcionalidad de negocio.
2. **Open/Closed Principle (OCP - Abierto/Cerrado):** El sistema de rutas en `App.tsx` permite agregar nuevas rutas envueltas en `<ProtectedRoute>` sin modificar las existentes. `ALL_MENU_ITEMS` permite agregar nuevas entradas al menú sin alterar la lógica de filtrado.
3. **Liskov Substitution Principle (LSP - Sustitución de Liskov):** Los componentes que reciben `children` (como `ProtectedRoute`, `SidebarLayout`, `AuthGuard`) pueden envolver cualquier componente hijo sin alterar su comportamiento.
4. **Interface Segregation Principle (ISP - Segregación de Interfaces):** `UserRole` es un tipo union específico con solo los roles necesarios. `AuthState` expone únicamente `username`, `role` y `hasRole`, sin exponer detalles internos de `localStorage`.
5. **Dependency Inversion Principle (DIP - Inversión de Dependencia):** Los componentes dependen de la abstracción `useAuth()` (hook del contexto) en lugar de leer directamente de `localStorage`. `ProtectedRoute` depende de la abstracción `authContext`, no de implementaciones concretas de almacenamiento.

---

## Patrones de Diseño Implementados en Nuestro Código

En nuestro frontend hemos implementado los siguientes patrones:

### Patrones Creacionales

- **Singleton (Instancia única):** La instancia de Axios en `shared/api.ts` se crea una sola vez con `axios.create()` y se reutiliza en toda la aplicación. El `AuthContext` también actúa como singleton del estado de autenticación.
- **Factory Method (Método de fábrica):** La función `getInitialAuth()` en `authContext.tsx` funciona como una fábrica que construye y retorna el objeto `AuthState` inicial a partir de `localStorage`, encapsulando la lógica de creación.

### Patrones Estructurales

- **Facade (Fachada):** `authContext.tsx` actúa como una fachada que simplifica el acceso al sistema de autenticación mock (localStorage, sessionStorage, validación de roles), exponiendo una interfaz limpia (`useAuth()` con `username`, `role`, `hasRole()`) sin que los consumidores conozcan el almacenamiento subyacente.
- **Adapter (Adaptador):** `api.ts` actúa como un adaptador sobre Axios, configurando `baseURL`, interceptores y headers específicos del dominio, adaptando la librería genérica a las necesidades del proyecto.

### Patrones de Comportamiento

- **Observer (Observador):** Implementado nativamente por React Context — los componentes que usan `useAuth()` se suscriben al `AuthContext` y se re-renderizan cuando el estado de autenticación cambia.
- **Strategy (Estrategia):** `ProtectedRoute` implementa una estrategia de autorización: recibe `allowedRoles` y usa `auth.hasRole()` para decidir si renderizar el contenido o redirigir. El comportamiento varía según los roles inyectados.
- **Template Method (Método plantilla):** El interceptor de Axios en `api.ts` define la estructura fija de una petición HTTP (leer usuario, validar rol, inyectar headers), mientras que los detalles específicos de cada request quedan a cargo del consumidor.
- **Chain of Responsibility (Cadena de responsabilidad):** El flujo de autenticación forma una cadena: `AuthGuard` → provee contexto → `ProtectedRoute` verifica rol → página renderiza. Cada eslabón decide si procesa o pasa al siguiente.
- **Mediator (Mediador):** `App.tsx` actúa como mediador centralizado que coordina las rutas, la autenticación y los guards, evitando comunicación directa entre componentes de diferentes módulos.
