# Especificación Técnica: Indicador de Ocupación de Celdas (SVG)

La solución visual para indicar la ocupación de las celdas se divide en dos componentes de React para separar la lógica de negocio de la renderización gráfica:

1. **PieChart (Presentacional):** Un componente "tonto" que únicamente dibuja el SVG basándose en los parámetros exactos (cantidad de trozos y color) que recibe.
2. **CellComponent (Lógico):** Un componente "inteligente" que recibe la cantidad de internos, aplica las reglas de negocio para determinar el color correspondiente, y envuelve al PieChart.

---

## Componente 1: PieChart (Presentacional)

### Responsabilidad
Renderizar de forma pura el gráfico vectorial escalable (SVG) con un número específico de segmentos coloreados. No contiene lógica de negocio.

### Interfaz (Props)
```typescript
interface PieChartProps {
  /** Cantidad exacta de espacios a pintar. El componente lo limitará internamente entre 0 y 8. */
  value: number;
  /** Color del relleno de los segmentos. */
  sliceColor?: string;
  /** Color del borde exterior del círculo (por defecto "black"). */
  borderColor?: string;
  /** Clases utilitarias (Tailwind) para definir el tamaño y posicionamiento. */
  className?: string;
}
```

### Detalles de Implementación Técnica (SVG)
- **Geometría Base:** Se define un único path estático que representa exactamente 1/8 del círculo (45 grados) usando un `viewBox="0 0 100 100"`.
- **Path:** `M 50 50 L 50 0 A 50 50 0 0 1 85.3553 14.6447 Z`
- **Límites y Renderizado:**
  - Aplica un clamp matemático al prop `value` para asegurarse de que iterará un máximo de 8 veces: `Math.max(0, Math.min(8, value))`.
  - Genera los segmentos coloreados iterando el path base y aplicando una transformación de rotación (`transform="rotate(grados origenX origenY)"`), incrementando 45 grados por índice.
- **Capas Estáticas:** Debe incluir un fondo blanco sólido (`circle`), el grupo de 4 líneas cruzadas negras (`line`) que forman las divisiones internas, y un borde exterior negro de 3px.

---

## Componente 2: CellComponent (Contenedor Lógico)

### Responsabilidad
Actuar como intermediario entre los datos del mapa de la prisión y el componente gráfico. Procesa el número bruto de internos y lo traduce a las directivas visuales (colores) que requiere el PieChart.

### Interfaz (Props)
```typescript
interface CellComponentProps {
  /** Cantidad real de internos en la celda (sin límite máximo). */
  value: number;
  /** Clases utilitarias (Tailwind) para inyectar en el contenedor. */
  className?: string;
}
```

### Reglas de Negocio (Mapeo de Estados y Colores)
El componente evalúa el prop `value` y calcula el color que pasará como `sliceColor` al `PieChart` según la siguiente tabla:

| Rango de Ocupación (`value`) | Estado Representado | Color a renderizar |
|---|---|---|
| `<= 0` | Vacío | `#ffffff` (Blanco) |
| `1 a 3` | Baja Ocupación | `#22c55e` (Verde) |
| `4 a 6` | Ocupación Media | `#eab308` (Amarillo) |
| `7 a 8` | Alta Ocupación / Lleno | `#ef4444` (Rojo) |
| `> 8` | Sobrepoblación | `#a855f7` (Morado) |

### Comportamiento de Ensamblaje
El `CellComponent` retorna internamente el `<PieChart />`, pasándole el `value` original (sabiendo que el `PieChart` se encargará de cortarlo a 8 visualmente) y el `sliceColor` calculado a partir de la tabla de reglas de negocio, forzando el `borderColor="black"`.
