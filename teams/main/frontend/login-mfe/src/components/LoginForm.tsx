import { useState, FormEvent } from 'react';
import { useAuth } from '../hooks/useAuth';

/**
 * LoginForm — Componente de autenticación con estética Ultra-Premium.
 *
 * Mejoras aplicadas:
 * - Fondo Mesh dinámico con blobs animados.
 * - Glassmorphism de múltiples capas (Satin Glass).
 * - Tipografía Outfit con jerarquía clara.
 * - Animaciones de entrada y feedback visual pulido.
 */
export function LoginForm() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const { handleLogin, isLoading, error } = useAuth();

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    await handleLogin(username, password);
  }

  return (
    <div className="relative min-h-screen bg-[#020617] flex items-center justify-center p-6 overflow-hidden">
      
      {/* ─── Elementos de fondo (Mesh Blobs) ─── */}
      <div className="absolute top-0 -left-4 w-72 h-72 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob" />
      <div className="absolute top-0 -right-4 w-72 h-72 bg-indigo-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000" />
      <div className="absolute -bottom-8 left-20 w-72 h-72 bg-pink-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-4000" />
      
      {/* Contenedor Principal */}
      <div className="relative w-full max-w-[440px] animate-slide-up">
        
        {/* Logo / Brand UI */}
        <div className="text-center mb-10">
          <div className="relative inline-block">
            <div className="absolute inset-0 bg-indigo-500 blur-2xl opacity-20" />
            <div className="relative w-20 h-20 bg-gradient-to-tr from-indigo-600 to-violet-500 rounded-3xl flex items-center justify-center shadow-2xl mb-6 mx-auto transform hover:scale-105 transition-transform duration-500 group">
              <svg className="w-10 h-10 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5}
                  d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
              </svg>
            </div>
          </div>
          <h1 className="text-4xl font-bold text-white tracking-tight mb-3">
            SSO <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-violet-400">Portal</span>
          </h1>
          <p className="text-slate-400 font-medium">
            Acceso centralizado al ecosistema
          </p>
        </div>

        {/* Card Satin Glass */}
        <div className="satin-glass p-10 rounded-[2.5rem] relative group border-white/5 overflow-hidden">
          
          {/* Brillo sutil de fondo del card */}
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-white/10 to-transparent" />

          <form onSubmit={handleSubmit} className="space-y-7 relative z-10" id="login-form">
            
            <div className="space-y-2">
              <label htmlFor="username" className="text-sm font-semibold text-slate-300 ml-1">
                Usuario
              </label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                disabled={isLoading}
                placeholder="Ingresa tu usuario"
                className="premium-input"
              />
            </div>

            <div className="space-y-2">
              <div className="flex justify-between items-center px-1">
                <label htmlFor="password" className="text-sm font-semibold text-slate-300">
                  Contraseña
                </label>
                <button type="button" className="text-xs font-medium text-indigo-400 hover:text-indigo-300 transition-colors">
                  ¿Olvidaste tu clave?
                </button>
              </div>
              <input
                id="password"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={isLoading}
                placeholder="••••••••••••"
                className="premium-input"
              />
            </div>

            {error && (
              <div className="flex items-center gap-3 p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-red-400 text-sm animate-pulse">
                <svg className="w-5 h-5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                </svg>
                {error}
              </div>
            )}

            <button
              id="login-submit-btn"
              type="submit"
              disabled={isLoading}
              className="premium-button w-full flex items-center justify-center gap-3 text-lg"
            >
              {isLoading ? (
                <div className="flex items-center gap-2">
                  <svg className="animate-spin h-5 w-5" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" fill="none" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                  Procesando...
                </div>
              ) : (
                <>
                  Entrar
                  <svg className="w-5 h-5 group-hover:translate-x-1 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7l5 5m0 0l-5 5m5-5H6" />
                  </svg>
                </>
              )}
            </button>
          </form>
        </div>

        {/* Footer info */}
        <p className="text-center text-slate-500 text-sm mt-8">
          ¿No tienes una cuenta? <button className="text-indigo-400 font-semibold hover:underline">Regístrate</button>
        </p>
      </div>
    </div>
  );
}
