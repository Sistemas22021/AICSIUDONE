import { useEffect, useState } from 'react';
import Modal from './Modal';
import { usePrefs } from '../services/PrefsContext';
import { useAuth } from '../services/AuthContext';
import api, { configMotorService } from '../services/api';
import { useConfirm } from '../services/ConfirmContext';
import { useToast } from '../services/ToastContext';

/* ============================================================
   MODAL DE CONFIGURACIÓN
============================================================ */
export function ModalConfiguracion({
  abierto,
  onClose,
  pestañaInicial = 'general',
}: {
  abierto: boolean;
  onClose: () => void;
  pestañaInicial?: 'general' | 'motor';
}) {
  const { prefs, actualizar, reset } = usePrefs();
  const [tab, setTab] = useState<'general' | 'motor'>(pestañaInicial);
  const [guardado, setGuardado] = useState(false);
  const confirmar = useConfirm();
  const toast = useToast();

  // Estado local de los umbrales del motor (cargados del backend)
  const [umbrales, setUmbrales] = useState<any>(null);
  const [cargandoMotor, setCargandoMotor] = useState(false);
  const [guardandoMotor, setGuardandoMotor] = useState(false);

  useEffect(() => {
    if (abierto) setTab(pestañaInicial);
  }, [abierto, pestañaInicial]);

  // Carga los umbrales del backend cuando se abre la pestaña motor
  useEffect(() => {
    if (abierto && tab === 'motor' && !umbrales) {
      setCargandoMotor(true);
      configMotorService.obtener()
        .then((data) => setUmbrales(data))
        .catch(() => toast.error('No se pudieron cargar los umbrales del motor'))
        .finally(() => setCargandoMotor(false));
    }
  }, [abierto, tab]);

  // Resetea el estado del motor al cerrar (para recargar fresco la próxima vez)
  useEffect(() => {
    if (!abierto) setUmbrales(null);
  }, [abierto]);

  const setU = (campo: string, valor: number) =>
    setUmbrales((prev: any) => ({ ...prev, [campo]: valor }));

  // Guardar: en pestaña motor → backend; en otras → localStorage (como antes)
  const guardar = async () => {
    if (tab === 'motor') {
      if (!umbrales) return;
      setGuardandoMotor(true);
      try {
        const actualizada = await configMotorService.guardar(umbrales);
        setUmbrales(actualizada);
        toast.exito('Umbrales del motor guardados');
      } catch {
        toast.error('No se pudieron guardar los umbrales');
      } finally {
        setGuardandoMotor(false);
      }
      return;
    }
    setGuardado(true);
    setTimeout(() => setGuardado(false), 1800);
  };

  const restaurarMotor = async () => {
    const ok = await confirmar({
      titulo: 'Restaurar umbrales del motor',
      mensaje: '¿Volver todos los umbrales del motor a sus valores originales?',
      textoConfirmar: 'Restaurar',
      peligro: true,
    });
    if (!ok) return;
    setGuardandoMotor(true);
    try {
      const defaults = await configMotorService.restaurarDefaults();
      setUmbrales(defaults);
      toast.exito('Umbrales restaurados a sus valores originales');
    } catch {
      toast.error('No se pudieron restaurar los umbrales');
    } finally {
      setGuardandoMotor(false);
    }
  };

  return (
    <Modal abierto={abierto} onClose={onClose} titulo="Configuración del sistema" icono="settings" ancho={720}>
      <div style={{ display: 'flex', gap: 4, marginBottom: 20, borderBottom: '1px solid var(--slate-800)', paddingBottom: 12 }}>
        <button className={`option-chip ${tab === 'general' ? 'active' : ''}`} onClick={() => setTab('general')}>
          General
        </button>
        <button className={`option-chip ${tab === 'motor' ? 'active' : ''}`} onClick={() => setTab('motor')}>
          Umbrales del motor
        </button>
      </div>

      {tab === 'general' && (
        <div>
          <div className="setting-row">
            <div className="setting-label">
              <span className="material-symbols-outlined">palette</span>
              <div>
                <div className="setting-label-text">Tema</div>
                <div className="setting-label-desc">Apariencia de la interfaz</div>
              </div>
            </div>
            <div className="option-row">
              <button
                className={`option-chip ${prefs.tema === 'oscuro' ? 'active' : ''}`}
                onClick={() => actualizar({ tema: 'oscuro' })}
              >
                Oscuro
              </button>
              <button
                className={`option-chip ${prefs.tema === 'claro' ? 'active' : ''}`}
                onClick={() => actualizar({ tema: 'claro' })}
              >
                Claro
              </button>
            </div>
          </div>
          <div style={{ marginTop: 20, paddingTop: 16, borderTop: '1px solid var(--slate-800)' }}>
            <button
              className="btn-ghost"
              onClick={async () => {
                const ok = await confirmar({
                  titulo: 'Restaurar configuración',
                  mensaje: '¿Restaurar las preferencias de interfaz a sus valores por defecto?',
                  textoConfirmar: 'Restaurar',
                  peligro: true,
                });
                if (ok) {
                  reset();
                  guardar();
                  toast.exito('Configuración restaurada');
                }
              }}
            >
              <span className="material-symbols-outlined" style={{ fontSize: 14 }}>restart_alt</span>
              Restaurar valores por defecto
            </button>
          </div>
        </div>
      )}

      {tab === 'motor' && (
        <div>
          <p style={{ color: 'var(--slate-400)', fontSize: 12, marginBottom: 20 }}>
            Ajustá los parámetros heurísticos usados por el motor Red Thread. Los cambios
            se guardan en el servidor y se aplican la próxima vez que se ejecute el análisis.
          </p>

          {cargandoMotor || !umbrales ? (
            <div style={{ padding: 40, textAlign: 'center', color: 'var(--slate-500)' }}>
              <span className="material-symbols-outlined" style={{ fontSize: 32 }}>hourglass_empty</span>
              <div style={{ marginTop: 8 }}>Cargando umbrales...</div>
            </div>
          ) : (
            <>
              <div style={{ marginBottom: 24 }}>
                <h3 style={{ fontSize: 12, color: 'var(--red-500)', textTransform: 'uppercase', letterSpacing: '0.1em', margin: '0 0 12px', fontWeight: 700 }}>
                  Regla: Nodo logístico
                </h3>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Radio de búsqueda</span>
                    <span className="slider-row-value">{umbrales.nodoRadioMetros} m</span>
                  </div>
                  <input type="range" min="100" max="2000" step="50"
                    value={umbrales.nodoRadioMetros}
                    onChange={(e) => setU('nodoRadioMetros', Number(e.target.value))} />
                </div>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Mínimo de vehículos cercanos</span>
                    <span className="slider-row-value">{umbrales.nodoMinVehiculos}</span>
                  </div>
                  <input type="range" min="2" max="10"
                    value={umbrales.nodoMinVehiculos}
                    onChange={(e) => setU('nodoMinVehiculos', Number(e.target.value))} />
                </div>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Ventana temporal</span>
                    <span className="slider-row-value">{umbrales.nodoVentanaHoras} h</span>
                  </div>
                  <input type="range" min="12" max="168" step="12"
                    value={umbrales.nodoVentanaHoras}
                    onChange={(e) => setU('nodoVentanaHoras', Number(e.target.value))} />
                </div>
              </div>

              <div style={{ marginBottom: 24 }}>
                <h3 style={{ fontSize: 12, color: 'var(--red-500)', textTransform: 'uppercase', letterSpacing: '0.1em', margin: '0 0 12px', fontWeight: 700 }}>
                  Regla: Vehículo escolta
                </h3>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Mínimo de coincidencias</span>
                    <span className="slider-row-value">{umbrales.escoltaMinCoincidencias}</span>
                  </div>
                  <input type="range" min="2" max="10"
                    value={umbrales.escoltaMinCoincidencias}
                    onChange={(e) => setU('escoltaMinCoincidencias', Number(e.target.value))} />
                </div>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Ventana de tiempo entre avistamientos</span>
                    <span className="slider-row-value">{umbrales.escoltaVentanaMinutos} min</span>
                  </div>
                  <input type="range" min="1" max="30"
                    value={umbrales.escoltaVentanaMinutos}
                    onChange={(e) => setU('escoltaVentanaMinutos', Number(e.target.value))} />
                </div>
              </div>

              <div style={{ marginBottom: 24 }}>
                <h3 style={{ fontSize: 12, color: 'var(--red-500)', textTransform: 'uppercase', letterSpacing: '0.1em', margin: '0 0 12px', fontWeight: 700 }}>
                  Regla: Modus operandi
                </h3>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Umbral de similitud</span>
                    <span className="slider-row-value">{(umbrales.modusUmbral * 100).toFixed(0)}%</span>
                  </div>
                  <input type="range" min="0.5" max="1" step="0.05"
                    value={umbrales.modusUmbral}
                    onChange={(e) => setU('modusUmbral', Number(e.target.value))} />
                </div>
                <p style={{ fontSize: 11, color: 'var(--slate-500)', marginTop: 8 }}>
                  Valores más altos → mayor precisión, menos coincidencias. Valores más bajos →
                  más patrones detectados pero con más falsos positivos.
                </p>
              </div>

              <div>
                <h3 style={{ fontSize: 12, color: 'var(--red-500)', textTransform: 'uppercase', letterSpacing: '0.1em', margin: '0 0 12px', fontWeight: 700 }}>
                  Regla: Cluster de desapariciones
                </h3>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Radio del cluster</span>
                    <span className="slider-row-value">{umbrales.clusterRadioMetros} m</span>
                  </div>
                  <input type="range" min="500" max="5000" step="100"
                    value={umbrales.clusterRadioMetros}
                    onChange={(e) => setU('clusterRadioMetros', Number(e.target.value))} />
                </div>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Mínimo de personas en el cluster</span>
                    <span className="slider-row-value">{umbrales.clusterMin}</span>
                  </div>
                  <input type="range" min="2" max="10"
                    value={umbrales.clusterMin}
                    onChange={(e) => setU('clusterMin', Number(e.target.value))} />
                </div>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Ventana temporal</span>
                    <span className="slider-row-value">{umbrales.clusterVentanaDias} días</span>
                  </div>
                  <input type="range" min="7" max="90" step="1"
                    value={umbrales.clusterVentanaDias}
                    onChange={(e) => setU('clusterVentanaDias', Number(e.target.value))} />
                </div>
                <div className="slider-row">
                  <div className="slider-row-top">
                    <span className="slider-row-label">Radio para nodo sospechoso</span>
                    <span className="slider-row-value">{umbrales.clusterRadioNodoSospechoso} m</span>
                  </div>
                  <input type="range" min="200" max="3000" step="100"
                    value={umbrales.clusterRadioNodoSospechoso}
                    onChange={(e) => setU('clusterRadioNodoSospechoso', Number(e.target.value))} />
                </div>
              </div>

              <div style={{ marginTop: 20, paddingTop: 16, borderTop: '1px solid var(--slate-800)' }}>
                <button className="btn-ghost" onClick={restaurarMotor} disabled={guardandoMotor}>
                  <span className="material-symbols-outlined" style={{ fontSize: 14 }}>restart_alt</span>
                  Restaurar valores originales del motor
                </button>
              </div>
            </>
          )}
        </div>
      )}

      {guardado && (
        <div className="success" style={{ marginTop: 16 }}>
          Configuración guardada correctamente
        </div>
      )}

      <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: 20, paddingTop: 16, borderTop: '1px solid var(--slate-800)' }}>
        <button className="btn-primary" onClick={guardar} disabled={tab === 'motor' && guardandoMotor}>
          <span className="material-symbols-outlined" style={{ fontSize: 16 }}>check</span>
          {tab === 'motor' && guardandoMotor ? 'Guardando...' : 'Guardar cambios'}
        </button>
      </div>
    </Modal>
  );
}

/* ============================================================
   MODAL DE MANUAL DE USUARIO
============================================================ */
export function ModalManual({ abierto, onClose }: { abierto: boolean; onClose: () => void }) {
  return (
    <Modal abierto={abierto} onClose={onClose} titulo="Manual de Usuario" icono="book" ancho={780}>
      <div className="doc-section">
        <h3>📘 Introducción</h3>
        <p>
          Nexo Criminal es una plataforma de inteligencia que descubre vínculos no evidentes
          entre personas, vehículos y ubicaciones usando el motor Red Thread. Este manual
          resume las funciones principales del sistema.
        </p>
      </div>

      <div className="doc-section">
        <h3>🧩 Módulos principales</h3>
        <ul>
          <li><strong>Panel de control:</strong> métricas globales y ejecución del motor.</li>
          <li><strong>Personas:</strong> alta, edición y eliminación de sujetos (víctimas, sospechosos, testigos, propietarios, intermediarios).</li>
          <li><strong>Vehículos:</strong> registro de la flota con seguimiento de estado (normal, robado, recuperado, apoyo).</li>
          <li><strong>Ubicaciones:</strong> puntos georreferenciados con tipo (taller, cajero, domicilio, etc.).</li>
          <li><strong>Sucesos:</strong> robos, desapariciones, avistamientos y transacciones.</li>
          <li><strong>Personas Desaparecidas:</strong> registro de casos activos con dossier completo, fotos y seguimiento.</li>
          <li><strong>Asistente IA:</strong> análisis inteligente con Claude para predicción de zonas, reportes y patrones.</li>
          <li><strong>Alertas:</strong> notificaciones generadas por el motor al detectar patrones.</li>
          <li><strong>Grafo Red Thread:</strong> visualización interactiva de todos los vínculos descubiertos.</li>
        </ul>
      </div>

      <div className="doc-section">
        <h3>🎯 Flujo de trabajo recomendado</h3>
        <ol>
          <li>Registrá personas, vehículos y ubicaciones.</li>
          <li>Cargá los sucesos (robos, desapariciones, avistamientos).</li>
          <li>Desde el panel principal, ejecutá el motor con el botón <code>Ejecutar motor completo</code>.</li>
          <li>Revisá las alertas generadas en la sección correspondiente.</li>
          <li>Abrí el <strong>Grafo Red Thread</strong> para ver las conexiones de forma visual.</li>
          <li>Usá el <strong>Asistente IA</strong> para generar reportes y predecir zonas de búsqueda.</li>
        </ol>
      </div>

      <div className="doc-section">
        <h3>⚙️ Reglas del motor</h3>
        <ul>
          <li><strong>Nodo logístico:</strong> detecta posibles deshuesaderos cuando varios vehículos robados convergen cerca de un taller o galpón.</li>
          <li><strong>Escolta vehicular:</strong> identifica vehículos que acompañan repetidamente a otros robados.</li>
          <li><strong>Círculo de confianza:</strong> encuentra personas intermedias entre víctimas y sospechosos.</li>
          <li><strong>Modus operandi:</strong> agrupa sucesos con método delictivo similar aunque ocurran en distintas zonas.</li>
          <li><strong>Cluster de desapariciones:</strong> detecta múltiples casos de personas desaparecidas en zonas geográficas cercanas.</li>
        </ul>
      </div>
    </Modal>
  );
}

/* ============================================================
   MODAL DE TUTORIAL RÁPIDO
============================================================ */
export function ModalTutorial({ abierto, onClose }: { abierto: boolean; onClose: () => void }) {
  return (
    <Modal abierto={abierto} onClose={onClose} titulo="Tutorial rápido" icono="play_circle" ancho={720}>
      <p style={{ color: 'var(--slate-400)', marginBottom: 20 }}>
        Seguí estos pasos para hacer una primera demo funcional del sistema en menos de 5 minutos.
      </p>

      {[
        {
          t: 'Cargar datos de prueba',
          d: (
            <>
              Ejecutá el script Python desde la carpeta <code>scripts/</code>:<br />
              <code>python generar_datos.py</code><br />
              Esto crea 50 personas, 30 vehículos, 15 ubicaciones y 20 sucesos automáticamente.
            </>
          ),
        },
        {
          t: 'Ejecutar el motor Red Thread',
          d: 'Volvé al panel principal y hacé click en "Ejecutar motor completo". El sistema analizará los datos y generará alertas.',
        },
        {
          t: 'Revisar las alertas',
          d: 'Andá a la sección "Alertas" y mirá los patrones detectados. Cambiá el estado de cada alerta según tu criterio (confirmada, descartada, en revisión).',
        },
        {
          t: 'Explorar el grafo',
          d: 'Abrí la sección "Grafo Red Thread". Los nodos en rojo representan entidades sospechosas y las líneas rojas son los hilos que el motor descubrió.',
        },
        {
          t: 'Probar el Asistente IA',
          d: 'Andá a "Asistente IA" y hacé preguntas sobre tus datos. Probá generar un reporte ejecutivo desde el detalle de una persona desaparecida.',
        },
      ].map((p, i) => (
        <div className="tutorial-step" key={i}>
          <div className="tutorial-step-num">{i + 1}</div>
          <div className="tutorial-step-content">
            <h4>{p.t}</h4>
            <p>{p.d}</p>
          </div>
        </div>
      ))}

      <div
        style={{
          marginTop: 20,
          padding: 14,
          background: 'rgba(34, 211, 238, 0.1)',
          border: '1px solid rgba(34, 211, 238, 0.3)',
          color: 'var(--tertiary)',
          fontSize: 12,
        }}
      >
        💡 <strong>Tip:</strong> los datos de ejemplo están preparados para disparar automáticamente
        las 5 reglas del motor (Nodo logístico, Escolta, Círculo de confianza, Modus operandi y Cluster de desapariciones).
      </div>
    </Modal>
  );
}

/* ============================================================
   MODAL DE DOCUMENTACIÓN API
============================================================ */
export function ModalAPI({ abierto, onClose }: { abierto: boolean; onClose: () => void }) {
  const endpoints = [
      // ---- Autenticación ----
      { m: 'POST', url: '/api/v1/auth/login', d: 'Iniciar sesión (devuelve JWT)' },
      { m: 'POST', url: '/api/v1/auth/registrar', d: 'Registrar un usuario nuevo' },

      // ---- Personas ----
      { m: 'GET', url: '/api/v1/personas', d: 'Listar todas las personas' },
      { m: 'POST', url: '/api/v1/personas', d: 'Crear persona' },
      { m: 'PUT', url: '/api/v1/personas/{id}', d: 'Actualizar persona' },
      { m: 'DELETE', url: '/api/v1/personas/{id}', d: 'Eliminar persona' },

      // ---- Vehículos ----
      { m: 'GET', url: '/api/v1/vehiculos', d: 'Listar vehículos' },
      { m: 'POST', url: '/api/v1/vehiculos', d: 'Registrar vehículo' },
      { m: 'PATCH', url: '/api/v1/vehiculos/{id}/estado', d: 'Cambiar estado del vehículo' },

      // ---- Ubicaciones ----
      { m: 'GET', url: '/api/v1/ubicaciones', d: 'Listar ubicaciones' },
      { m: 'POST', url: '/api/v1/ubicaciones', d: 'Registrar ubicación' },

      // ---- Sucesos ----
      { m: 'GET', url: '/api/v1/sucesos', d: 'Listar sucesos' },
      { m: 'POST', url: '/api/v1/sucesos', d: 'Registrar suceso' },

      // ---- Desaparecidas ----
      { m: 'GET', url: '/api/v1/desaparecidas', d: 'Listar personas desaparecidas' },
      { m: 'POST', url: '/api/v1/desaparecidas', d: 'Registrar desaparición' },
      { m: 'POST', url: '/api/v1/desaparecidas/{id}/foto', d: 'Subir foto de la persona' },

      // ---- Motor de Reglas (Engine) ----
      { m: 'POST', url: '/api/v1/engine/ejecutar-todo', d: 'Ejecutar las 5 reglas del motor' },
      { m: 'POST', url: '/api/v1/engine/nodo-logistico', d: 'Ejecutar solo nodo logístico' },
      { m: 'POST', url: '/api/v1/engine/escolta', d: 'Ejecutar solo escolta' },
      { m: 'POST', url: '/api/v1/engine/circulo-confianza', d: 'Ejecutar solo círculo de confianza' },
      { m: 'POST', url: '/api/v1/engine/modus-operandi', d: 'Ejecutar solo modus operandi' },
      { m: 'POST', url: '/api/v1/engine/cluster-desapariciones', d: 'Ejecutar cluster de desapariciones' },

      // ---- Configuración del Motor (Umbrales) ----
      { m: 'GET', url: '/api/v1/config/motor', d: 'Obtener los umbrales del motor' },
      { m: 'PUT', url: '/api/v1/config/motor', d: 'Guardar nuevos umbrales' },
      { m: 'POST', url: '/api/v1/config/motor/default', d: 'Restaurar umbrales por defecto' },

      // ---- Catálogo de Modus Operandi ----
      { m: 'GET', url: '/api/v1/modus', d: 'Listar modus operandi activos' },
      { m: 'GET', url: '/api/v1/modus/todos', d: 'Listar todos los modus (incluye inactivos)' },
      { m: 'POST', url: '/api/v1/modus', d: 'Crear un modus operandi' },
      { m: 'PUT', url: '/api/v1/modus/{id}', d: 'Actualizar un modus operandi' },
      { m: 'DELETE', url: '/api/v1/modus/{id}', d: 'Desactivar un modus operandi' },

      // ---- Grafo ----
      { m: 'GET', url: '/api/v1/grafo/completo', d: 'Grafo completo en formato Cytoscape' },

      // ---- Alertas ----
      { m: 'GET', url: '/api/v1/alertas', d: 'Listar alertas' },
      { m: 'PATCH', url: '/api/v1/alertas/{id}/estado', d: 'Cambiar estado de alerta' },

      // ---- Inteligencia Artificial (IA) ----
      { m: 'GET', url: '/api/v1/ia/estado', d: 'Estado de configuración de la IA' },
      { m: 'POST', url: '/api/v1/ia/chat', d: 'Chat conversacional con Claude' },
      { m: 'POST', url: '/api/v1/ia/zonas-busqueda/{id}', d: 'Predicción de zonas de búsqueda' },
      { m: 'POST', url: '/api/v1/ia/reporte/{tipo}/{id}', d: 'Generar reporte ejecutivo' },
      { m: 'POST', url: '/api/v1/ia/clasificar-modus', d: 'Clasificar modus operandi desde una descripción (IA)' }
  ];

  return (
    <Modal abierto={abierto} onClose={onClose} titulo="Documentación API" icono="api" ancho={860}>
      <div className="doc-section">
        <h3>🔐 Autenticación</h3>
        <p>Todas las peticiones (excepto <code>/auth/*</code>) deben incluir el header:</p>
        <pre><code>Authorization: Bearer &lt;token_jwt&gt;</code></pre>
      </div>

      <div className="doc-section">
        <h3>📡 Ejemplo: Login</h3>
        <pre><code>{`curl -X POST http://localhost:8080/api/v1/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{"username":"admin2","password":"admin123"}'`}</code></pre>
      </div>

      <div className="doc-section">
        <h3>📋 Endpoints disponibles</h3>
        {endpoints.map((e, i) => (
          <div className="endpoint-row" key={i}>
            <span className={`method-badge ${e.m.toLowerCase()}`}>{e.m}</span>
            <code style={{ color: 'white', flex: 1 }}>{e.url}</code>
            <span style={{ color: 'var(--slate-500)', fontFamily: 'var(--font-body)', fontSize: 11 }}>
              {e.d}
            </span>
          </div>
        ))}
      </div>

      <div className="doc-section">
        <h3>📘 Códigos de respuesta</h3>
        <ul>
          <li><code>200 OK</code> — Operación exitosa</li>
          <li><code>201 Created</code> — Recurso creado</li>
          <li><code>400 Bad Request</code> — Datos inválidos o duplicados</li>
          <li><code>401 Unauthorized</code> — Token inválido o ausente</li>
          <li><code>404 Not Found</code> — Recurso inexistente</li>
          <li><code>500 Internal Server Error</code> — Error del servidor</li>
        </ul>
      </div>
    </Modal>
  );
}

/* ============================================================
   MODAL DE MI PERFIL
============================================================ */
export function ModalPerfil({ abierto, onClose }: { abierto: boolean; onClose: () => void }) {
  const { user } = useAuth();
  const fechaIngreso = new Date().toLocaleDateString('es-ES');

  return (
    <Modal abierto={abierto} onClose={onClose} titulo="Mi perfil" icono="person" ancho={540}>
      <div
        style={{
          display: 'flex',
          gap: 16,
          alignItems: 'center',
          padding: 16,
          background: 'var(--slate-950)',
          border: '1px solid var(--slate-800)',
          marginBottom: 20,
        }}
      >
        <div
          style={{
            width: 72,
            height: 72,
            borderRadius: '50%',
            background: 'var(--red-600)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontSize: 28,
            fontWeight: 800,
          }}
        >
          {(user?.nombreCompleto || user?.username || '??').slice(0, 2).toUpperCase()}
        </div>
        <div>
          <div style={{ color: 'white', fontSize: 18, fontWeight: 700 }}>
            {user?.nombreCompleto || user?.username}
          </div>
          <div
            style={{
              color: 'var(--red-500)',
              fontSize: 10,
              fontWeight: 700,
              textTransform: 'uppercase',
              letterSpacing: '0.15em',
              marginTop: 2,
            }}
          >
            {user?.rol}
          </div>
          <div style={{ color: 'var(--slate-500)', fontSize: 11, marginTop: 4, fontFamily: 'var(--font-mono)' }}>
            ID: {user?.username}
          </div>
        </div>
      </div>

      <div className="dossier-fields" style={{ padding: 0 }}>
        <div className="dossier-field">
          <span>Nombre completo</span>
          <strong>{user?.nombreCompleto || '—'}</strong>
        </div>
        <div className="dossier-field">
          <span>Usuario</span>
          <strong>{user?.username}</strong>
        </div>
        <div className="dossier-field">
          <span>Rol asignado</span>
          <strong>{user?.rol}</strong>
        </div>
        <div className="dossier-field">
          <span>Sesión iniciada</span>
          <strong>{fechaIngreso}</strong>
        </div>
        <div className="dossier-field">
          <span>Estado</span>
          <strong style={{ color: 'var(--green)' }}>● Activo</strong>
        </div>
      </div>

      <p style={{ color: 'var(--slate-500)', fontSize: 11, marginTop: 16, fontStyle: 'italic' }}>
        Para modificar tus datos personales, contactá al administrador del sistema.
      </p>
    </Modal>
  );
}

/* ============================================================
   MODAL CAMBIAR CONTRASEÑA
============================================================ */
export function ModalCambiarPassword({
  abierto,
  onClose,
}: {
  abierto: boolean;
  onClose: () => void;
}) {
  const { user } = useAuth();
  const [actual, setActual] = useState('');
  const [nueva, setNueva] = useState('');
  const [confirma, setConfirma] = useState('');
  const [msg, setMsg] = useState<{ tipo: 'error' | 'success'; texto: string } | null>(null);
  const [cargando, setCargando] = useState(false);

  const resetear = () => {
    setActual('');
    setNueva('');
    setConfirma('');
    setMsg(null);
  };

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setMsg(null);

    if (nueva.length < 6) {
      return setMsg({ tipo: 'error', texto: 'La nueva contraseña debe tener al menos 6 caracteres.' });
    }
    if (nueva !== confirma) {
      return setMsg({ tipo: 'error', texto: 'Las contraseñas no coinciden.' });
    }
    if (actual === nueva) {
      return setMsg({ tipo: 'error', texto: 'La nueva contraseña debe ser distinta a la actual.' });
    }

    setCargando(true);
    try {
      await api.post('/auth/cambiar-password', {
        username: user?.username,
        passwordActual: actual,
        passwordNueva: nueva,
      });
      setMsg({
        tipo: 'success',
        texto: '✅ Contraseña actualizada correctamente. La próxima vez que ingreses, usá la nueva.',
      });
      setTimeout(() => {
        resetear();
        onClose();
      }, 2500);
    } catch (e: any) {
      setMsg({
        tipo: 'error',
        texto: e?.response?.data?.error || 'No se pudo cambiar la contraseña.',
      });
    } finally {
      setCargando(false);
    }
  };

  const handleClose = () => {
    resetear();
    onClose();
  };

  return (
    <Modal abierto={abierto} onClose={handleClose} titulo="Cambiar contraseña" icono="vpn_key" ancho={480}>
      <form onSubmit={submit}>
        <div className="form-group" style={{ marginBottom: 14 }}>
          <label className="form-label">Contraseña actual</label>
          <input
            type="password"
            value={actual}
            onChange={(e) => setActual(e.target.value)}
            required
            placeholder="••••••••"
            autoFocus
          />
        </div>
        <div className="form-group" style={{ marginBottom: 14 }}>
          <label className="form-label">Nueva contraseña</label>
          <input
            type="password"
            value={nueva}
            onChange={(e) => setNueva(e.target.value)}
            required
            placeholder="Mínimo 6 caracteres"
            minLength={6}
          />
        </div>
        <div className="form-group" style={{ marginBottom: 14 }}>
          <label className="form-label">Confirmar nueva contraseña</label>
          <input
            type="password"
            value={confirma}
            onChange={(e) => setConfirma(e.target.value)}
            required
            placeholder="Repetí la nueva contraseña"
          />
        </div>

        {msg && <div className={msg.tipo === 'error' ? 'error' : 'success'}>{msg.texto}</div>}

        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end', marginTop: 16 }}>
          <button type="button" className="btn-ghost" onClick={handleClose}>
            Cancelar
          </button>
          <button type="submit" className="btn-primary" disabled={cargando}>
            <span className="material-symbols-outlined" style={{ fontSize: 16 }}>
              save
            </span>
            {cargando ? 'Verificando...' : 'Cambiar contraseña'}
          </button>
        </div>
      </form>
    </Modal>
  );
}