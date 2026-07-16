import { createContext, useContext, useEffect, useState, ReactNode } from 'react';

export interface UmbralesMotor {
  nodoLogisticoRadio: number;
  nodoLogisticoMinVehiculos: number;
  nodoLogisticoVentanaHoras: number;
  escoltaMinCoincidencias: number;
  escoltaVentanaMinutos: number;
  modusUmbral: number;
}

export interface Prefs {
  tema: 'oscuro' | 'claro';
  notificaciones: {
    alertasCriticas: boolean;
    resumenDiario: boolean;
    sonidoAlertas: boolean;
  };
  umbrales: UmbralesMotor;
}

const PREFS_KEY = 'nexo_prefs';

const DEFAULT_PREFS: Prefs = {
  tema: 'oscuro',
  notificaciones: {
    alertasCriticas: true,
    resumenDiario: false,
    sonidoAlertas: true,
  },
  umbrales: {
    nodoLogisticoRadio: 500,
    nodoLogisticoMinVehiculos: 3,
    nodoLogisticoVentanaHoras: 72,
    escoltaMinCoincidencias: 3,
    escoltaVentanaMinutos: 2,
    modusUmbral: 0.75,
  },
};

interface PrefsCtx {
  prefs: Prefs;
  actualizar: (p: Partial<Prefs>) => void;
  actualizarUmbrales: (u: Partial<UmbralesMotor>) => void;
  actualizarNotificaciones: (n: Partial<Prefs['notificaciones']>) => void;
  reset: () => void;
  t: (texto: string) => string;
}

const Ctx = createContext<PrefsCtx | null>(null);

function aplicarTema(tema: 'oscuro' | 'claro') {
  document.documentElement.setAttribute('data-tema', tema);
}

export const PrefsProvider = ({ children }: { children: ReactNode }) => {
  const [prefs, setPrefs] = useState<Prefs>(() => {
    try {
      const raw = localStorage.getItem(PREFS_KEY);
      if (!raw) return DEFAULT_PREFS;
      const stored = JSON.parse(raw);
      // Sacar campo idioma si quedó del legacy
      delete stored.idioma;
      return { ...DEFAULT_PREFS, ...stored };
    } catch {
      return DEFAULT_PREFS;
    }
  });

  useEffect(() => {
    localStorage.setItem(PREFS_KEY, JSON.stringify(prefs));
    aplicarTema(prefs.tema);
  }, [prefs]);

  useEffect(() => {
    aplicarTema(prefs.tema);
    document.documentElement.setAttribute('lang', 'es');
  }, []);

  const actualizar = (p: Partial<Prefs>) => setPrefs((prev) => ({ ...prev, ...p }));
  const actualizarUmbrales = (u: Partial<UmbralesMotor>) =>
    setPrefs((prev) => ({ ...prev, umbrales: { ...prev.umbrales, ...u } }));
  const actualizarNotificaciones = (n: Partial<Prefs['notificaciones']>) =>
    setPrefs((prev) => ({ ...prev, notificaciones: { ...prev.notificaciones, ...n } }));
  const reset = () => setPrefs(DEFAULT_PREFS);

  // t() ahora siempre devuelve el texto tal cual (todo en español)
  const t = (texto: string) => texto;

  return (
    <Ctx.Provider value={{ prefs, actualizar, actualizarUmbrales, actualizarNotificaciones, reset, t }}>
      {children}
    </Ctx.Provider>
  );
};

export const usePrefs = () => {
  const c = useContext(Ctx);
  if (!c) throw new Error('usePrefs debe usarse dentro de PrefsProvider');
  return c;
};