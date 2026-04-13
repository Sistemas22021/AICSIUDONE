import { useState, FormEvent } from 'react';
import { useAuth } from '../hooks/useAuth';

/**
 * LoginForm — Componente de formulario de autenticación.
 *
 * Responsabilidad única: renderizar el formulario y delegar la
 * lógica al hook useAuth.
 *
 * Diseño: Vista premium con glassmorphism, enfocada en UX.
 * Se usa Tailwind CSS utility-first.
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
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-indigo-950 to-slate-900 flex items-center justify-center p-4">

      {/* Card principal con glassmorphism */}
      <div className="w-full max-w-md">

        {/* Logo / Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-indigo-600 rounded-2xl mb-4 shadow-lg shadow-indigo-500/40">
            <svg className="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2}
                d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
            </svg>
          </div>
          <h1 className="text-3xl font-bold text-white tracking-tight">
            Bienvenido
          </h1>
          <p className="text-slate-400 mt-2 text-sm">
            Ecosistema SSO Centralizado
          </p>
        </div>

        {/* Formulario */}
        <div className="bg-white/5 backdrop-blur-xl border border-white/10 rounded-2xl p-8 shadow-2xl">
          <form onSubmit={handleSubmit} className="space-y-5" id="login-form">

            {/* Campo Username */}
            <div>
              <label htmlFor="username" className="block text-sm font-medium text-slate-300 mb-1.5">
                Usuario
              </label>
              <input
                id="username"
                type="text"
                autoComplete="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                disabled={isLoading}
                placeholder="tu_usuario"
                className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white
                           placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500
                           focus:border-transparent transition-all duration-200 disabled:opacity-50"
              />
            </div>

            {/* Campo Password */}
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-slate-300 mb-1.5">
                Contraseña
              </label>
              <input
                id="password"
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={isLoading}
                placeholder="••••••••"
                className="w-full px-4 py-3 bg-white/5 border border-white/10 rounded-xl text-white
                           placeholder:text-slate-500 focus:outline-none focus:ring-2 focus:ring-indigo-500
                           focus:border-transparent transition-all duration-200 disabled:opacity-50"
              />
            </div>

            {/* Mensaje de error */}
            {error && (
              <div
                role="alert"
                className="flex items-center gap-2.5 px-4 py-3 bg-red-500/10 border border-red-500/20
                           rounded-xl text-red-400 text-sm"
              >
                <svg className="w-4 h-4 shrink-0" fill="currentColor" viewBox="0 0 20 20">
                  <path fillRule="evenodd"
                    d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z"
                    clipRule="evenodd" />
                </svg>
                {error}
              </div>
            )}

            {/* Botón de submit */}
            <button
              id="login-submit-btn"
              type="submit"
              disabled={isLoading}
              className="w-full py-3.5 bg-indigo-600 hover:bg-indigo-500 disabled:bg-indigo-600/50
                         text-white font-semibold rounded-xl transition-all duration-200
                         shadow-lg shadow-indigo-500/30 hover:shadow-indigo-500/50
                         focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2
                         focus:ring-offset-slate-900 disabled:cursor-not-allowed
                         flex items-center justify-center gap-2"
            >
              {isLoading ? (
                <>
                  <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z" />
                  </svg>
                  Autenticando...
                </>
              ) : 'Iniciar sesión'}
            </button>

          </form>
        </div>

        {/* Footer informativo */}
        <p className="text-center text-slate-500 text-xs mt-6">
          SSO Boilerplate — Arquitectura Hexagonal + React
        </p>

      </div>
    </div>
  );
}
