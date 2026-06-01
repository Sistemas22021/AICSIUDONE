import type { Meta, StoryObj } from '@storybook/react';
import { CellComponent } from './CellComponent';

const meta = {
  title: 'Components/CellComponent',
  component: CellComponent,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    value: {
      control: { type: 'number' },
      description: 'Cantidad real de internos en la celda',
    },
    className: {
      control: 'text',
      description: 'Clases de Tailwind CSS para inyectar en el indicador SVG',
    },
  },
} satisfies Meta<typeof CellComponent>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Vacia: Story = {
  args: {
    value: 0,
    className: 'w-24 h-24',
  },
};

export const BajaOcupacion: Story = {
  args: {
    value: 2,
    className: 'w-24 h-24',
  },
};

export const OcupacionMedia: Story = {
  args: {
    value: 5,
    className: 'w-24 h-24',
  },
};

export const AltaOcupacion: Story = {
  args: {
    value: 8,
    className: 'w-24 h-24',
  },
};

export const Sobrepoblacion: Story = {
  args: {
    value: 12,
    className: 'w-24 h-24',
  },
};
