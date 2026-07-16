// services/authMode.ts
// Interruptor central de modo de autenticacion.
// VITE_AUTH_MODE = 'local' (default) usa el login propio de Nexo Criminal.
// VITE_AUTH_MODE = 'sso'   usa el login centralizado del profesor (SSO).

export type AuthMode = 'local' | 'sso';

export const AUTH_MODE: AuthMode =
  (import.meta.env.VITE_AUTH_MODE as AuthMode) === 'sso' ? 'sso' : 'local';

export const esSSO = () => AUTH_MODE === 'sso';
export const esLocal = () => AUTH_MODE === 'local';

// Config del SSO (solo se usan en modo sso). Valores por defecto = puertos del boilerplate.
export const SSO_CONFIG = {
  // URL del Login MFE del profesor (la pantalla de login)
  loginUrl: import.meta.env.VITE_SSO_LOGIN_URL || 'http://localhost:3000',
  // Base del Auth Service / API Gateway (para refresh). El gateway del profesor escucha en 8090.
  authApiUrl: import.meta.env.VITE_SSO_AUTH_URL || 'http://localhost:8090',
};
