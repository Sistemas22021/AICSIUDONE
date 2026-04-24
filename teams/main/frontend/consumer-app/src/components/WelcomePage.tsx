import { getAccessToken } from '../services/tokenService';

/**
 * WelcomePage — Página de bienvenida con estética Dashboard Premium.
 *
 * Mejoras:
 * - Layout espacioso y moderno.
 * - Iconografía refinada.
 * - Visualización clara del estado de los tokens.
 * - Step-by-step con indicadores de éxito.
 */
export function WelcomePage() {
  const token = getAccessToken();
  const username = token ? decodeJwtUsername(token) : 'Usuario';

  return (
    <div className="min-h-screen bg-[#020617] text-slate-200 relative overflow-hidden">
      
      {/* Fondo Mesh sutil */}
      <div className="absolute top-0 right-0 w-[500px] h-[500px] bg-indigo-500/10 rounded-full blur-[120px]" />
      <div className="absolute bottom-0 left-0 w-[500px] h-[500px] bg-purple-500/10 rounded-full blur-[120px]" />

      <div className="relative z-10 max-w-5xl mx-auto px-6 py-12 lg:py-20">
        
        {/* Header Section */}
        <header className="flex flex-col md:flex-row md:items-center justify-between gap-8 mb-16 animate-fade-in">
          <div>
            <div className="flex items-center gap-3 mb-4">
              <span className="px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-bold tracking-wider uppercase">
                Sesión Activa
              </span>
            </div>
            <h1 className="text-5xl font-bold text-white tracking-tight mb-4">
              Hola de nuevo, <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-violet-400">{username}</span>
            </h1>
            <p className="text-slate-400 text-lg max-w-xl">
              Has iniciado sesión exitosamente en el ecosistema SSO. Todos los servicios están sincronizados.
            </p>
          </div>
          
          {/* Avatar / User Card */}
          <div className="satin-glass p-6 rounded-[2rem] flex items-center gap-4 border-white/5 shadow-2xl">
            <div className="w-14 h-14 bg-gradient-to-tr from-indigo-600 to-violet-500 rounded-2xl flex items-center justify-center text-white text-xl font-bold shadow-lg">
              {username.charAt(0).toUpperCase()}
            </div>
            <div>
              <p className="text-white font-bold">{username}</p>
              <p className="text-slate-500 text-sm italic">Usuario autenticado</p>
            </div>
          </div>
        </header>

        <div className="grid lg:grid-cols-3 gap-8 items-start">
          
          {/* Main Info Cards */}
          <div className="lg:col-span-2 space-y-8 animate-slide-up">
            
            {/* SSO Flow Visualizer */}
            <section className="premium-card">
              <h2 className="text-xl font-bold text-white mb-8 flex items-center gap-3">
                <svg className="w-6 h-6 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
                </svg>
                Flujo de Autenticación Realizado
              </h2>
              
              <div className="relative pl-8 border-l-2 border-white/5 space-y-10">
                {[
                  { title: 'Detección de Sesión', desc: 'La Consumer App identificó la ausencia de un token válido.' },
                  { title: 'Redirección Segura', desc: 'Transferencia al Portal SSO Centralizado via URL callback.' },
                  { title: 'Validación de Credenciales', desc: 'El Auth Service autenticó tu identidad con éxito.' },
                  { title: 'Emisión de Tokens', desc: 'Sincronización de JWT (Memoria) y Refresh Token (HttpOnly).' }
                ].map((item, i) => (
                  <div key={i} className="relative group">
                    <div className="absolute -left-[41px] top-1 w-4 h-4 rounded-full bg-slate-900 border-2 border-indigo-500 group-hover:bg-indigo-500 transition-colors duration-300" />
                    <h3 className="text-white font-semibold mb-1 group-hover:text-indigo-400 transition-colors">{item.title}</h3>
                    <p className="text-slate-400 text-sm leading-relaxed">{item.desc}</p>
                  </div>
                ))}
              </div>
            </section>
          </div>

          {/* Sidebar / Token Info */}
          <aside className="space-y-8 animate-slide-up" style={{ animationDelay: '0.2s' }}>
            
            <section className="premium-card">
              <h2 className="text-lg font-bold text-white mb-6">Estado Técnico</h2>
              <div className="space-y-4">
                <TechBadge label="Access Token" status="Memoria" />
                <TechBadge label="Refresh Token" status="HttpOnly" />
                <TechBadge label="Provider" status="Central SSO" />
              </div>
              
              <button className="w-full mt-8 py-4 bg-white/5 hover:bg-red-500/10 border border-white/10 hover:border-red-500/30 text-slate-300 hover:text-red-400 rounded-2xl font-bold transition-all duration-300 flex items-center justify-center gap-2 group">
                <svg className="w-5 h-5 group-hover:rotate-12 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                </svg>
                Cerrar Sesión
              </button>
            </section>

          </aside>
        </div>

        <footer className="mt-20 pt-8 border-t border-white/5 flex items-center justify-between text-slate-500 text-sm">
          <p>© 2026 SSO Boilerplate Centralizado</p>
          <div className="flex gap-6">
            <button className="hover:text-white transition-colors">Documentación</button>
            <button className="hover:text-white transition-colors">Soporte</button>
          </div>
        </footer>
      </div>
    </div>
  );
}

function TechBadge({ label, status }: { label: string; status: string }) {
  return (
    <div className="flex items-center justify-between p-3 rounded-xl bg-white/5 border border-white/5">
      <span className="text-slate-400 text-sm font-medium">{label}</span>
      <span className="text-white text-sm font-bold">{status}</span>
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
