# Skill: Modelar y Probar Componentes en Blanco

Este skill define las instrucciones para crear, modelar y probar componentes visuales "en blanco" o de forma aislada sin fricción dentro del proyecto sandbox de componentes.

## 📌 Instrucciones Principales

Cuando el usuario te solicite probar, crear o experimentar con un componente visual "en blanco", de forma aislada, o sin la fricción del flujo de negocio principal, deberás seguir estrictamente este flujo:

1. **Ubicación del Proyecto:**
   - Todo componente en blanco debe ser creado en el proyecto sandbox: [lib-react-component](file:///d:/proyects/2026/sso-boilerplate/teams/main/frontend/lib-react-component).

2. **Estructura del Componente:**
   - Crea el archivo del componente dentro de la carpeta de componentes del proyecto: `teams/main/frontend/lib-react-component/src/components/`.
   - Utiliza **TypeScript (TSX)** para la definición del componente.
   - Utiliza **Tailwind CSS v4** para todos los estilos visuales (sin hojas de estilo CSS ad-hoc ni Tailwind heredado v3 si es posible).
   - Divide la lógica de negocio (contenedor) de la lógica de renderizado gráfico (presentacional) si el componente tiene una complejidad media o alta.

3. **Integración con Storybook:**
   - Genera un archivo de historia para Storybook: `teams/main/frontend/lib-react-component/src/components/<NombreComponente>.stories.tsx`.
   - Define al menos 3 o 4 variaciones del componente en la historia para comprobar diferentes estados visuales (por ejemplo: vacío, cargando, error, estados límite, colores opcionales, etc.).
   - Utiliza el formato de historias CSF3 (Component Story Format v3):
     ```typescript
     import type { Meta, StoryObj } from '@storybook/react';
     import { MiComponente } from './MiComponente';

     const meta = {
       title: 'Components/MiComponente',
       component: MiComponente,
       tags: ['autodocs'],
     } satisfies Meta<typeof MiComponente>;

     export default meta;
     type Story = StoryObj<typeof meta>;

     export const Default: Story = {
       args: {
         // props por defecto
       },
     };
     ```

4. **Documentación del Componente:**
   - Todos los specs técnicos y de diseño del componente deben registrarse en un archivo Markdown en `docs/componentes/<NombreComponente>.md`.
   - El spec debe contener:
     - Descripción General de la Arquitectura
     - Especificación de Props e Interfaz de TypeScript
     - Reglas de Negocio / Mapeo de Estados
     - Instrucciones de Uso y Visualización en Storybook

---
*Nota para la ejecución: El sandbox permite diseñar componentes limpios y visualmente excelentes antes de integrarlos al flujo de micro-frontends del SSO.*
