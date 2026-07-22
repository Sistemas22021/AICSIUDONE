# Frontend — Sistema Integral de Gestión Penitenciaria (SIGP)

---

## Instalación Desde Cero (PC Limpia)

Sigue estos pasos si tu computadora no tiene ningún programa instalado.

### 1. Instalar Node.js 20+

**Instalador oficial (recomendado):**
```bash
# Descargar desde: https://nodejs.org/ (versión 20.x LTS)
# Ejecutar el .msi y seguir los pasos. Marcar "Add to PATH".

# Luego en terminal:
nvm install 20
nvm use 20
```

**Verificar instalación:**
```bash
node --version   # Debe mostrar v20.x.x
npm --version    # Debe mostrar 10.x.x
```

### 2. Instalar Git

```bash
# Descargar desde: https://git-scm.com/download/win
# Ejecutar el .exe y seguir los pasos (opciones por defecto)

# Verificar:
git --version
```

### 3. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/AICSIUDONE.git
cd AICSIUDONE/teams/EquipoBlanco/frontend/prison-web
```

### 4. Instalar Dependencias

```bash
npm install
```

Esto descargará todas las dependencias definidas en `package.json` (React, Vite, Tailwind, Axios, etc.).

### 5. Configurar Variables de Entorno

```bash
# Copiar el archivo de ejemplo:
cp .env.example .env
# O en Windows:
# Copy-Item .env.example .env
```

### 6. Ejecutar en Modo Desarrollo

```bash
npm run dev
```

La aplicación se levantará en `http://localhost:5173`.

---

## Uso en Producción (Vercel + Túnel SSH)

Para que el frontend desplegado en Vercel pueda consumir tu backend local, necesitas exponer `localhost:8081` mediante un túnel SSH con **localhost.run**.

### Paso 1: Iniciar el Backend

```bash
# En la carpeta del backend:
cd AICSIUDONE/teams/EquipoBlanco/backend/prision-service
.\mvnw.cmd spring-boot:run
```

### Paso 2: Crear el Túnel SSH

```bash
# En OTRA terminal (debe quedarse abierta):
ssh -R 80:localhost:8081 nokey@localhost.run
```

Te devolverá una URL como `https://XXXXX.lhr.life`. **Guarda esta URL, la necesitarás.**

### Paso 3: Configurar Variable de Entorno en Vercel (CLI)

```bash
# 1. Instalar Vercel CLI si no la tienes:
npm install -g vercel

# 2. Iniciar sesión (primera vez):
vercel login

# 3. Vincular (solo la primera vez después de clonar):
vercel link

# Te pedirá:
# 1. Confirmar la carpeta actual (`prison-web`)
# 2. Seleccionar tu cuenta de Vercel (scope)
# 3. Elegir **el proyecto existente** `prison-web`

Esto genera `.vercel/project.json` y permite que los comandos `vercel env` funcionen.

# 4. Agregar la variable de entorno (reemplazar XXXXX con la URL del túnel):
vercel env add VITE_API_GATEWAY_URL production
# Te pedirá el valor: pega https://XXXXX.lhr.life y presiona Enter

# 5. Si necesitas eliminar una variable existente para reemplazarla:
vercel env rm VITE_API_GATEWAY_URL production
# Confirma con 'y'

# 6. Compilar
vercel build --prod && vercel --prod --prebuilt --yes   
```

### Paso 4: Probar en Desarrollo Local

Si quieres probar la conexión antes de subir a Vercel, edita el archivo `.env` local:

```
VITE_API_GATEWAY_URL=https://XXXXX.lhr.life
```

Luego reinicia el servidor de desarrollo (`npm run dev`). El frontend local ahora consumirá el backend a través del túnel.

> **Importante:** Cada vez que reinicias el túnel SSH, la URL cambia. Deberás actualizar `VITE_API_GATEWAY_URL` en las Environment Variables de Vercel y redeployar.

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

```
prison-web/
├── .env                                 # Variables de entorno (VITE_API_GATEWAY_URL)
├── .env.example                         # Ejemplo de variables de entorno
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

### Ramas y comportamientos

| Rama | Push / PR automático | Deploy a Vercel |
|------|---------------------|-----------------|
| `equipoblanco-dev` | ❌ No ejecuta pipeline | — |
| `equipoBlanco` | ✅ Ejecuta test + build | Preview (`vercel --yes`) |
| `main` | ✅ Ejecuta test + build | **Producción** (`vercel --prod`) |

**Importante:** La rama `equipoblanco-dev` **no está configurada como trigger** en el pipeline. Los PRs desde `-dev` hacia `main` **tampoco ejecutan tests** ni deploys. El flujo correcto es: `equipoblanco-dev` → PR a `equipoBlanco` → PR a `main`.

### Ejecución manual desde GitHub UI
Ir al repositorio en GitHub → **Actions** → **"Equipo Blanco - Prison Web CI/CD"** → **Run workflow** → seleccionar rama → **Run workflow**.

### Ejecución manual desde terminal (CLI)
Requiere [GitHub CLI](https://cli.github.com/) instalada y autenticada (`gh auth login`):

```bash
# Deploy preview desde equipoBlanco (no afecta producción)
gh workflow run equipoblanco-prison-web.yml --ref equipoBlanco

# Deploy a producción desde main (el único que deploya con --prod)
gh workflow run equipoblanco-prison-web.yml --ref main
```

### ¿Qué hace el pipeline?
1. **Test:** Prepara Node 20, instala dependencias (`npm ci`), ejecuta tests (`npm test`)
2. **Build & Deploy:** Compila con Vite (`npm run build`) y despliega a Vercel
   - Si la rama es `equipoBlanco`/`develop`: preview (URL temporal)
   - Si la rama es `main`: **producción** (URL definitiva)

### ¿Puedo deployar a producción sin pasar por `main`?
**No.** El deploy a producción (`--prod`) solo se ejecuta en la rama `main`. Para reflejar cambios en producción el flujo correcto es:

```
equipoblanco-dev (trabajo local — NO hace PR directo a main)
  → PR a equipoBlanco (preview para verificar)
    → PR a main (deploy automático a producción)
```

Forzar un deploy de producción desde otra rama requeriría modificar el workflow, pero no es recomendable porque saltas las verificaciones de código.

---

## Tests Implementados

Se han desarrollado pruebas unitarias utilizando **Vitest** + **React Testing Library** con un entorno simulado via **jsdom**, garantizando el correcto funcionamiento del enrutamiento y la protección por roles.

### Archivo de test
`src/App.test.tsx` — cubre los siguientes casos:
- **Renderizado:** Verifica que la aplicación renderiza el branding "SIGP" sin errores.
- **Redirección por rol:** Comprueba que `Oficial Penitenciario` es redirigido a `/dashboard` y `Administrador del Sistema` a `/celdas/configurar` al acceder a la raíz.
- **Protección de rutas:** Verifica que un `Oficial Penitenciario` no puede acceder a rutas exclusivas de `Supervisor` (ej: `/incidentes`).

### Configuración global
- `src/test/setup.ts` — importa `@testing-library/jest-dom`
- Configuración en Vite: definida en `vite.config.ts` con `globals: true`, `environment: 'jsdom'` y `setupFiles`

### Ejecutar todos los tests
```bash
npm test
```

### Ejecutar tests en modo watch
```bash
npm test -- --watch
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
