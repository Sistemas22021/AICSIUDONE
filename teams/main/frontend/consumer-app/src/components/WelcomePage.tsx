import { getAccessToken } from '../services/tokenService';

/**
 * WelcomePage — Página de bienvenida de la Consumer App.
 *
 * Solo se renderiza si el AuthGuard verificó que hay una sesión válida.
 * Muestra el estado de la sesión y cómo se integra con el ecosistema SSO.
 */
export function WelcomePage() {
  const token = getAccessToken();
  // Decodificar el payload del JWT para mostrar el username
  // NOTA: Esto es solo para visualización. NUNCA confiar en el payload sin verificar en el servidor.
  const username = token ? decodeJwtUsername(token) : 'Usuario';

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 flex items-center justify-center p-4">
      <div className="w-full max-w-2xl">

        {/* Header de éxito */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-emerald-500/20
                          border border-emerald-500/30 rounded-3xl mb-6 shadow-lg shadow-emerald-500/20">
            <svg className="w-10 h-10 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h1 className="text-4xl font-bold text-white tracking-tight">
            ¡Bienvenido, {username}! 🎉
          </h1>
          <p className="text-slate-400 mt-3 text-lg">
            Autenticación SSO exitosa
          </p>
        </div>

        {/* Card de información */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8 shadow-2xl space-y-6">

          {/* Estado de la sesión */}
          <div>
            <h2 className="text-white font-semibold mb-3 flex items-center gap-2">
              <span className="w-2 h-2 bg-emerald-400 rounded-full animate-pulse" />
              Estado de la sesión
            </h2>
            <div className="space-y-2">
              <InfoRow label="Access Token" value="✅ En memoria (15 min)" />
              <InfoRow label="Refresh Token" value="✅ HttpOnly Cookie (7 días)" />
              <InfoRow label="Usuario" value={username} />
            </div>
          </div>

          <hr className="border-white/10" />

          {/* Flujo SSO explicado */}
          <div>
            <h2 className="text-white font-semibold mb-3">🔄 Flujo SSO completado</h2>
            <ol className="space-y-2 text-sm text-slate-300">
              {[
                'Consumer App detectó que no había token en memoria',
                'Redirigió al Login MFE con ?redirect=<esta-url>',
                'Login MFE autenticó las credenciales contra el Auth Service',
                'Auth Service generó Access Token (JWT) y Refresh Token (Cookie)',
                'Login MFE redirigió de vuelta con ?token=<jwt>',
                'Consumer App guardó el token en memoria y limpió la URL',
              ].map((step, i) => (
                <li key={i} className="flex items-start gap-3">
                  <span className="shrink-0 w-5 h-5 bg-indigo-600 text-white text-xs rounded-full
                                   flex items-center justify-center font-bold mt-0.5">
                    {i + 1}
                  </span>
                  {step}
                </li>
              ))}
            </ol>
          </div>

        </div>

        <p className="text-center text-slate-500 text-xs mt-6">
          Consumer App — SSO Boilerplate Educativo
        </p>
      </div>
    </div>
  );
}

function InfoRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between text-sm">
      <span className="text-slate-400">{label}</span>
      <span className="text-slate-200 font-medium">{value}</span>
    </div>
  );
}

function decodeJwtUsername(token: string): string {
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub || 'Usuario';
  } catch {
    return 'Usuario';
  }
}
