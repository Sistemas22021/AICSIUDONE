# 🎨 Frontend — Nexo Criminal

React 18 + TypeScript + Vite + Cytoscape.js.

## Requisitos

- Node.js 18+
- npm 9+ (o pnpm / yarn)

## Instalación y ejecución

```bash
npm install
npm run dev
```

La app arranca en `http://localhost:5173`. El proxy de Vite redirige
las llamadas `/api/*` al backend Spring Boot en `http://localhost:8080`.

## Login por defecto

- Usuario: `admin`
- Password: `admin123`

## Pantallas

| Ruta | Descripción |
|---|---|
| `/login` | Inicio de sesión |
| `/` | Dashboard con estadísticas y botón para ejecutar el motor |
| `/personas` | CRUD de personas |
| `/vehiculos` | CRUD de vehículos + cambio de estado |
| `/ubicaciones` | CRUD de ubicaciones |
| `/sucesos` | CRUD de sucesos (robos, desapariciones, avistamientos) |
| `/alertas` | Listado de alertas generadas por el motor |
| `/grafo` | **Visualización del Hilo Rojo con Cytoscape** |

## Estructura

```
src/
├── components/       Sidebar
├── pages/            Login, Dashboard, Personas, Vehiculos, ...
├── services/         api.ts (axios), AuthContext
├── types/            Tipos TypeScript
├── App.tsx           Router
├── main.tsx          Entrada
└── styles.css        Estilos globales (dark theme)
```

## Build de producción

```bash
npm run build
```

Genera `dist/` con los estáticos listos para servir.
