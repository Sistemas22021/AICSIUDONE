import { useState, useRef, useCallback, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { Settings, X, User, Move, MapPin, Info, Users, CheckCircle, AlertTriangle, HelpCircle } from 'lucide-react'
import api from '../../shared/api'
import SidebarLayout from '../../shared/SidebarLayout'

// ─── Escala: el canvas tendrá dimensiones fijas ───────────────────────────────
const CANVAS_W = 900;  // px — ancho del canvas SVG
const CANVAS_H = 600;  // px — alto del canvas SVG
const PUNTO_RADIO = 18; // Radio del círculo que representa la celda (en px)

// ─── Helpers ─────────────────────────────────────────────────────────────────
function getEstado(celda) {
  const pct = celda.reclusosAsignados.length / celda.capacidad;
  if (pct >= 1) return 'llena';
  if (pct >= 0.8) return 'limite';
  return 'disponible';
}

const COLORES = {
  disponible: { fill: '#d4edda', stroke: '#27500A', header: '#3B6D11', text: '#173404', bg: 'rgba(212, 237, 218, 0.9)', dot: '#22c55e' },
  limite: { fill: '#fff3cd', stroke: '#854F0B', header: '#854F0B', text: '#412402', bg: 'rgba(255, 243, 205, 0.9)', dot: '#eab308' },
  llena: { fill: '#f8d7da', stroke: '#791F1F', header: '#A32D2D', text: '#501313', bg: 'rgba(248, 215, 218, 0.9)', dot: '#ef4444' },
};

function calculatePct(celda) {
  if (!celda.capacidad) return 0;
  return Math.round((celda.reclusosAsignados.length / celda.capacidad) * 100);
}

function calculateAge(birthDate) {
  if (!birthDate) return '—';
  const birth = new Date(birthDate);
  const today = new Date();
  let age = today.getFullYear() - birth.getFullYear();
  const m = today.getMonth() - birth.getMonth();
  if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;
  return age;
}

export default function CellMapPage() {
  const [celdas, setCeldas] = useState([]);
  const [reclusos, setReclusos] = useState([]);
  const [imagenesFondo, setImagenesFondo] = useState({ 1: null, 2: null, 3: null });
  const [pisoActivo, setPisoActivo] = useState(1);
  const imagenFondo = imagenesFondo[pisoActivo];
  const [modoEditor, setModoEditor] = useState(false);
  const [mostrarSelectorImagen, setMostrarSelectorImagen] = useState(false);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');

  // Estado local de posiciones (en píxeles dentro del canvas)
  const [posiciones, setPosiciones] = useState({});

  const [drag, setDrag] = useState(null);
  const [celdaHover, setCeldaHover] = useState(null);
  const [tooltipPos, setTooltipPos] = useState({ x: 0, y: 0 });
  const [modalAsignacion, setModalAsignacion] = useState(null);
  const [reclusoSeleccionado, setReclusoSeleccionado] = useState('');
  const [reclusoExpediente, setReclusoExpediente] = useState(null);
  const [celdaArrastrando, setCeldaArrastrando] = useState(null);

  const svgRef = useRef(null);
  const fileInputRef = useRef(null);

  // Cargar datos del backend
  const loadData = async () => {
    try {
      // Cargar celdas
      const cellsRes = await api.get('/cells');
      const cellsData = cellsRes.data;

      // Cargar reclusos
      let inmatesData = [];
      try {
        const inmatesRes = await api.get('/inmates');
        inmatesData = inmatesRes.data;
        setReclusos(inmatesData);
      } catch (err) {
        console.error('Error cargando reclusos', err);
        setReclusos([]);
      }

      // Mapear celdas con sus reclusos
      const mappedCeldas = cellsData.map(c => ({
        id: c.id,
        identificador: c.identifier,
        capacidad: c.maxCapacity,
        nivelConducta: c.conductLevel,
        largo: c.lengthMeters,
        ancho: c.widthMeters,
        reclusosAsignados: inmatesData.filter(i => i.cellId === c.id && i.status === 'ACTIVO_CON_CELDA')
      }));

      setCeldas(mappedCeldas);

      // Cargar posiciones del localStorage
      const savedPositions = localStorage.getItem('posicionesCeldas');
      const positionsMap = savedPositions ? JSON.parse(savedPositions) : {};

      const newPos = { ...positionsMap };
      mappedCeldas.forEach(c => {
        if (!newPos[c.id]) {
          newPos[c.id] = { x: -100, y: -100 }; // fuera de la vista si no está colocada
        }
      });
      setPosiciones(newPos);
      setLoading(false);
    } catch (err) {
      console.error(err);
      setErrorMsg('Error al conectar con los servicios. Verifique que los microservicios estén corriendo.');
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    // Cargar imágenes guardadas al iniciar
    const img1 = localStorage.getItem('mapaFondo_1') || localStorage.getItem('mapaFondo');
    const img2 = localStorage.getItem('mapaFondo_2');
    const img3 = localStorage.getItem('mapaFondo_3');
    setImagenesFondo({
      1: img1,
      2: img2,
      3: img3
    });
  }, []);

  // Reclusos sin celda
  const reclusosSinCelda = reclusos.filter(r => r.status === 'ACTIVO_SIN_CELDA');

  // Resumen global
  const totalCeldas = celdas.length;
  const totalReclusos = celdas.reduce((s, c) => s + c.reclusosAsignados.length, 0);
  const capacidadTotal = celdas.reduce((s, c) => s + c.capacidad, 0);
  const capacidadDisponible = capacidadTotal - totalReclusos;
  const porcentajeOcupacion = capacidadTotal > 0
    ? ((totalReclusos / capacidadTotal) * 100).toFixed(1)
    : '0';

  // Manejo de imagen de fondo
  const handleImageUpload = (e) => {
    const file = e.target.files?.[0];
    if (file) {
      if (!file.type.startsWith('image/')) {
        setErrorMsg('Error: Solo se admiten archivos de imagen (PNG, JPG, JPEG, SVG, etc.).');
        setMostrarSelectorImagen(false);
        setTimeout(() => setErrorMsg(''), 5000);
        return;
      }
      setErrorMsg(''); // Limpiar cualquier error previo
      const reader = new FileReader();
      reader.onload = (event) => {
        const imageUrl = event.target?.result;
        setImagenesFondo(prev => {
          const next = { ...prev, [pisoActivo]: imageUrl };
          localStorage.setItem(`mapaFondo_${pisoActivo}`, imageUrl);
          if (pisoActivo === 1) {
            localStorage.setItem('mapaFondo', imageUrl);
          }
          return next;
        });
        setMostrarSelectorImagen(false);
      };
      reader.readAsDataURL(file);
    }
  };

  const eliminarImagenFondo = () => {
    if (confirm(`¿Eliminar el plano de fondo del Piso ${pisoActivo}?`)) {
      setImagenesFondo(prev => {
        const next = { ...prev, [pisoActivo]: null };
        localStorage.removeItem(`mapaFondo_${pisoActivo}`);
        if (pisoActivo === 1) {
          localStorage.removeItem('mapaFondo');
        }
        return next;
      });
    }
  };

  // Drag & drop
  const getSVGPoint = useCallback((e) => {
    const svg = svgRef.current;
    if (!svg) return { x: 0, y: 0 };
    const rect = svg.getBoundingClientRect();
    const scaleX = CANVAS_W / rect.width;
    const scaleY = CANVAS_H / rect.height;
    return {
      x: (e.clientX - rect.left) * scaleX,
      y: (e.clientY - rect.top) * scaleY,
    };
  }, []);

  const getContainerPoint = useCallback((x, y) => {
    const svg = svgRef.current;
    if (!svg) return { x: 0, y: 0 };
    const rect = svg.getBoundingClientRect();
    return {
      x: x * (rect.width / CANVAS_W),
      y: y * (rect.height / CANVAS_H)
    };
  }, []);

  const iniciarDrag = useCallback((e, celdaId) => {
    if (!modoEditor) return;
    e.preventDefault();
    e.stopPropagation();
    const pt = getSVGPoint(e);
    const pos = posiciones[celdaId] ?? { x: CANVAS_W / 2, y: CANVAS_H / 2 };
    setDrag({
      celdaId,
      startMouseX: pt.x,
      startMouseY: pt.y,
      startPosX: pos.x,
      startPosY: pos.y,
    });
    setCeldaArrastrando(celdaId);
  }, [modoEditor, posiciones, getSVGPoint]);

  useEffect(() => {
    if (!drag) return;

    const onMove = (e) => {
      const pt = getSVGPoint(e);
      const dx = pt.x - drag.startMouseX;
      const dy = pt.y - drag.startMouseY;
      const currentCellPos = posiciones[drag.celdaId] || {};
      const currentR = currentCellPos.r || PUNTO_RADIO;
      const newX = Math.max(currentR, Math.min(drag.startPosX + dx, CANVAS_W - currentR));
      const newY = Math.max(currentR, Math.min(drag.startPosY + dy, CANVAS_H - currentR));
      setPosiciones(prev => ({
        ...prev,
        [drag.celdaId]: {
          ...prev[drag.celdaId],
          x: newX,
          y: newY
        }
      }));
    };

    const onUp = () => {
      setDrag(null);
      setCeldaArrastrando(null);
    };

    window.addEventListener('mousemove', onMove);
    window.addEventListener('mouseup', onUp);
    return () => {
      window.removeEventListener('mousemove', onMove);
      window.removeEventListener('mouseup', onUp);
    };
  }, [drag, getSVGPoint]);

  // Click en celda (abre modal de detalle y asignación, o menú en modo editor)
  const handleCeldaClick = (celda, e) => {
    if (modoEditor) {
      if (e) e.stopPropagation();
      setCeldaHover(prev => prev === celda.id ? null : celda.id);
      const pos = posiciones[celda.id];
      if (pos) {
        const pt = getContainerPoint(pos.x, pos.y);
        setTooltipPos(pt);
      }
    } else {
      setModalAsignacion(celda);
    }
  };

  const confirmarAsignacion = async () => {
    if (reclusoSeleccionado && modalAsignacion) {
      try {
        const username = sessionStorage.getItem('username') || 'Oficial';
        await api.post(`/cells/${modalAsignacion.id}/assign/${reclusoSeleccionado}`, {}, {
          headers: {
            'X-User-Name': username
          }
        });
        alert(`Recluso asignado a celda ${modalAsignacion.identificador} correctamente.`);
        setModalAsignacion(null);
        setReclusoSeleccionado('');
        loadData();
      } catch (err) {
        alert(err.response?.data?.message || 'No se pudo asignar el recluso.');
      }
    }
  };

  // Celdas dentro del plano (con posición válida y del piso activo)
  const celdasEnPlano = celdas.filter(c => {
    const pos = posiciones[c.id];
    const floorMatch = pos && (pos.floor || 1) === pisoActivo;
    return pos && pos.x >= 0 && pos.x <= CANVAS_W && pos.y >= 0 && pos.y <= CANVAS_H && floorMatch;
  });

  // Celdas sin colocar (fuera del plano)
  const celdasSinColocar = celdas.filter(c => {
    const pos = posiciones[c.id];
    return !pos || pos.x < 0 || pos.x > CANVAS_W || pos.y < 0 || pos.y > CANVAS_H;
  });

  // Render SVG de cada celda
  const renderCelda = (celda) => {
    const pos = posiciones[celda.id];
    if (!pos) return null;

    const estado = getEstado(celda);
    const col = COLORES[estado];
    const p = calculatePct(celda);
    const isDragging = drag?.celdaId === celda.id;
    const isHover = celdaHover === celda.id;

    const radioBase = pos.r || PUNTO_RADIO;
    const radioActual = isHover ? radioBase + 3 : radioBase;

    return (
      <g
        key={celda.id}
        style={{ cursor: modoEditor ? 'grab' : 'pointer' }}
        onMouseDown={e => iniciarDrag(e, celda.id)}
        onMouseEnter={e => {
          if (!modoEditor && !drag) {
            setCeldaHover(celda.id);
            const pt = getContainerPoint(pos.x, pos.y);
            setTooltipPos(pt);
          }
        }}
        onMouseLeave={() => {
          if (!modoEditor) {
            setCeldaHover(null);
          }
        }}
        onClick={e => handleCeldaClick(celda, e)}
        opacity={isDragging ? 0.6 : 1}
      >
        {/* Sombra o halo en hover */}
        {isHover && !modoEditor && (
          <circle
            cx={pos.x} cy={pos.y}
            r={radioActual + 4}
            fill="none"
            stroke={col.stroke}
            strokeWidth={3}
            opacity={0.35}
            className="animate-pulse"
          />
        )}

        {/* Círculo de fondo con blur o glass effect visual */}
        <circle
          cx={pos.x} cy={pos.y}
          r={radioActual}
          fill={col.fill}
          stroke={col.stroke}
          strokeWidth={2.5}
        />

        {/* Anillo de progreso de ocupación */}
        <circle
          cx={pos.x} cy={pos.y}
          r={radioActual - 3}
          fill="none"
          stroke={col.stroke}
          strokeWidth={1.5}
          strokeDasharray={`${(p / 100) * (2 * Math.PI * (radioActual - 3))} ${2 * Math.PI * (radioActual - 3)}`}
          strokeDashoffset="0"
          transform={`rotate(-90 ${pos.x} ${pos.y})`}
          opacity={0.6}
        />

        {/* Texto ocupación */}
        <text
          x={pos.x} y={pos.y - 2}
          textAnchor="middle"
          fontSize={11}
          fontWeight="bold"
          fill={col.text}
          fontFamily="sans-serif"
        >
          {celda.reclusosAsignados.length}/{celda.capacidad}
        </text>

        <text
          x={pos.x} y={pos.y + 8}
          textAnchor="middle"
          fontSize={8.5}
          fontWeight="bold"
          fill={col.text}
          fontFamily="sans-serif"
        >
          {celda.identificador}
        </text>

        {/* Indicador visual de modo editor */}
        {modoEditor && (
          <g opacity={0.4}>
            <circle cx={pos.x} cy={pos.y} r={radioActual - 2} fill="none" stroke={col.stroke} strokeWidth={1} strokeDasharray="2 2" />
            <text
              x={pos.x} y={pos.y}
              textAnchor="middle"
              dominantBaseline="central"
              fontSize={14}
              fill={col.stroke}
              fontFamily="sans-serif"
            >
              ⠿
            </text>
          </g>
        )}
      </g>
    );
  };

  const celdaConTooltip = celdaHover ? celdas.find(c => c.id === celdaHover) : null;

  const guardarPlano = () => {
    localStorage.setItem('posicionesCeldas', JSON.stringify(posiciones));
    setModoEditor(false);
    alert('Ubicación de celdas guardada con éxito.');
  };

const quitarCeldaDelPlano = (celdaId, identificador) => {
  if (confirm(`¿Está seguro de quitar la Celda ${identificador} del plano? Volverá a la lista de celdas sin colocar.`)) {
    setPosiciones(prev => {
      const nuevasPosiciones = { ...prev };
      // La enviamos fuera de la vista para que el filtro 'celdasSinColocar' la detecte
      nuevasPosiciones[celdaId] = { x: -100, y: -100 }; 
      return nuevasPosiciones;
    });
    // Si el tooltip o modal de esta celda estaba abierto, los cerramos
    setCeldaHover(null);
  }
};

  return (
    <SidebarLayout>
      <div className="max-w-7xl mx-auto space-y-6">

        {/* Encabezado */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-gray-150 pb-5">
          <div>
            <h1 className="text-3xl font-extrabold text-gray-900 tracking-tight flex items-center gap-2">
              <MapPin className="w-8 h-8 text-blue-600" />
              Mapa de Distribución 2D
            </h1>
            <p className="text-sm text-gray-500 mt-1">
              Visualice las celdas en el penal. Haga clic en una celda para ver internos o gestionar asignaciones.
            </p>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            {imagenFondo && (
              <button
                onClick={eliminarImagenFondo}
                className="px-3.5 py-2 text-xs font-semibold text-red-600 bg-red-50 border border-red-200 hover:bg-red-100 rounded-lg transition-colors cursor-pointer"
              >
                Eliminar plano Piso {pisoActivo}
              </button>
            )}
            <button
              onClick={() => setMostrarSelectorImagen(true)}
              className="flex items-center gap-2 px-3.5 py-2 rounded-lg border bg-blue-50 text-blue-700 border-blue-200 hover:bg-blue-100 transition-colors text-xs font-semibold cursor-pointer"
            >
              <MapPin className="w-4 h-4" />
              {imagenFondo ? `Cambiar plano Piso ${pisoActivo}` : `Cargar plano Piso ${pisoActivo}`}
            </button>
            <button
              onClick={() => modoEditor ? guardarPlano() : setModoEditor(true)}
              className={`flex items-center gap-2 px-4 py-2 rounded-lg border text-xs font-semibold transition-all ${modoEditor
                ? 'bg-emerald-600 text-white border-emerald-600 hover:bg-emerald-700 shadow-md'
                : 'bg-white text-gray-700 border-gray-300 hover:bg-gray-50'
                }`}
            >
              <Move className="w-4 h-4" />
              {modoEditor ? 'Guardar plano' : 'Editar plano'}
            </button>
            <Link
              to="/celdas/configurar"
              className="bg-gray-900 text-white px-4 py-2 rounded-lg hover:bg-gray-800 transition-colors flex items-center gap-2 text-xs font-semibold shadow-sm"
            >
              <Settings className="w-4 h-4" />
              Configurar celdas
            </Link>
          </div>
        </div>

        {errorMsg && (
          <div className="p-4 bg-red-50 border border-red-200 rounded-xl flex items-center gap-3 text-red-800">
            <AlertTriangle className="w-5 h-5 text-red-600 flex-shrink-0" />
            <div className="text-sm font-medium">{errorMsg}</div>
          </div>
        )}

        {/* Resumen Global Cards */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          {[
            { label: 'Total de celdas', valor: totalCeldas, sub: `${celdas.filter(c => getEstado(c) === 'llena').length} al límite/llenas`, icon: Users },
            { label: 'Reclusos activos', valor: totalReclusos, sub: `De ${capacidadTotal} plazas totales`, icon: User },
            { label: 'Capacidad disponible', valor: capacidadDisponible, sub: 'Plazas libres', icon: CheckCircle },
            { label: 'Ocupación general', valor: `${porcentajeOcupacion}%`, sub: parseFloat(porcentajeOcupacion) >= 80 ? 'Nivel crítico' : 'Nivel normal', icon: Info, color: parseFloat(porcentajeOcupacion) >= 80 ? 'text-amber-600' : 'text-gray-900' },
          ].map((card, i) => {
            const CardIcon = card.icon;
            return (
              <div key={i} className="bg-white rounded-xl border border-gray-200 p-4 shadow-sm flex items-center justify-between">
                <div className="space-y-1">
                  <p className="text-xs font-medium text-gray-400 uppercase tracking-wider">{card.label}</p>
                  <p className={`text-2xl font-black ${card.color || 'text-gray-900'}`}>{card.valor}</p>
                  <p className="text-[11px] text-gray-400 font-medium">{card.sub}</p>
                </div>
                <div className="bg-gray-50 p-2.5 rounded-lg border border-gray-100">
                  <CardIcon className="w-5 h-5 text-gray-400" />
                </div>
              </div>
            );
          })}
        </div>

        {/* Editor Info Banner */}
        {modoEditor && (
          <div className="flex items-center gap-3 bg-amber-50 border border-amber-250 text-amber-900 rounded-xl px-4 py-3 text-sm shadow-sm animate-fade-in">
            <Move className="w-5 h-5 text-amber-600 flex-shrink-0 animate-bounce" />
            <div>
              <span className="font-bold">Modo editor activo:</span> Arrastre los puntos de las celdas para posicionarlos sobre el plano de fondo. Puede arrastrar nuevas celdas no posicionadas desde el panel lateral derecho. Al finalizar, haga clic en <strong className="text-emerald-700">Guardar plano</strong>.
            </div>
          </div>
        )}

        {/* Modal: Subir plano de fondo */}
        {mostrarSelectorImagen && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-50 animate-fade-in">
            <div className="bg-white rounded-xl shadow-2xl p-6 w-full max-w-md border border-gray-250">
              <div className="flex items-center justify-between mb-4 border-b border-gray-100 pb-3">
                <h3 className="text-lg font-bold text-gray-900">Cargar plano del penal</h3>
                <button
                  onClick={() => setMostrarSelectorImagen(false)}
                  className="p-1 hover:bg-gray-100 rounded-lg text-gray-400 hover:text-gray-600 transition-colors"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
              <p className="text-xs text-gray-500 mb-4 leading-relaxed">
                Seleccione una imagen esquemática de la infraestructura del penal. Esta imagen servirá de fondo para ubicar y ordenar visualmente las celdas.
              </p>

              <div className="border-2 border-dashed border-gray-200 hover:border-blue-400 rounded-xl p-6 transition-colors text-center cursor-pointer relative">
                <input
                  type="file"
                  accept="image/*"
                  onChange={handleImageUpload}
                  className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
                />
                <MapPin className="w-10 h-10 text-gray-300 mx-auto mb-2" />
                <span className="text-sm font-semibold text-blue-600 block">Seleccionar archivo de imagen</span>
                <span className="text-xs text-gray-400 mt-1 block">JPG, PNG o SVG (Recomendado: 900x600 px)</span>
              </div>

              <div className="flex gap-3 mt-5">
                <button
                  onClick={() => setMostrarSelectorImagen(false)}
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg hover:bg-gray-50 text-gray-700 text-sm font-semibold transition-colors"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Plano Principal + Panel Lateral */}
        <div className="flex flex-col xl:flex-row gap-5 items-start">

          {/* Plano SVG */}
          <div className="flex-1 bg-white rounded-xl border border-gray-200 shadow-sm overflow-hidden w-full">

            {/* Cabecera / Leyenda del plano */}
            <div className="flex flex-wrap items-center justify-between gap-3 px-5 py-3.5 border-b border-gray-100 bg-gray-50">
              <div className="flex flex-wrap items-center gap-6">
                <div className="flex gap-4">
                  {[
                    { color: '#d4edda', border: '#27500A', label: 'Disponible (<80%)', dot: 'bg-emerald-500' },
                    { color: '#fff3cd', border: '#854F0B', label: 'Al Límite (≥80%)', dot: 'bg-yellow-500' },
                    { color: '#f8d7da', border: '#791F1F', label: 'Llena (100%)', dot: 'bg-red-500' },
                  ].map((l, i) => (
                    <div key={i} className="flex items-center gap-2">
                      <span className={`w-2.5 h-2.5 rounded-full ${l.dot}`}></span>
                      <span className="text-xs font-semibold text-gray-600">{l.label}</span>
                    </div>
                  ))}
                </div>

                {/* SELECTOR DE PISOS */}
                <div className="flex items-center gap-1.5 border-l border-gray-250 pl-6">
                  <span className="text-xs font-bold text-gray-400 uppercase tracking-wider">🏢 Piso:</span>
                  <div className="flex bg-gray-200/80 p-0.5 rounded-lg border border-gray-300/50">
                    {[1, 2, 3].map((f) => (
                      <button
                        key={f}
                        onClick={() => {
                          setPisoActivo(f);
                          setCeldaHover(null); // Ocultar tooltip al cambiar de piso
                        }}
                        className={`px-3 py-1 rounded-md text-xs font-bold transition-all cursor-pointer ${
                          pisoActivo === f
                            ? 'bg-blue-600 text-white shadow-sm'
                            : 'text-gray-600 hover:text-gray-900 hover:bg-gray-100/50'
                        }`}
                      >
                        Piso {f}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
              <div className="flex items-center gap-1.5 text-xs font-medium text-gray-450">
                <Info className="w-3.5 h-3.5" />
                <span>Haga clic sobre una celda para ver internos</span>
              </div>
            </div>

            {/* Canvas SVG */}
            <div className="relative overflow-auto" style={{ userSelect: 'none' }}>
              <svg
                ref={svgRef}
                width="100%"
                height="auto"
                viewBox={`0 0 ${CANVAS_W} ${CANVAS_H}`}
                style={{ display: 'block', background: '#f8fafc', cursor: modoEditor ? 'crosshair' : 'default' }}
                onClick={() => {
                  if (modoEditor) {
                    setCeldaHover(null);
                  }
                }}
              >
                {/* Plano de fondo cargado */}
                {imagenFondo ? (
                  <image
                    href={imagenFondo}
                    x="0"
                    y="0"
                    width={CANVAS_W}
                    height={CANVAS_H}
                    preserveAspectRatio="xMidYMid meet"
                    opacity={modoEditor ? 0.75 : 1}
                  />
                ) : (
                  // Fondo alternativo por defecto
                  <g>
                    <rect width={CANVAS_W} height={CANVAS_H} fill="#f1f5f9" />
                    {/* Cuadrícula decorativa */}
                    <defs>
                      <pattern id="grid" width="40" height="40" patternUnits="userSpaceOnUse">
                        <path d="M 40 0 L 0 0 0 40" fill="none" stroke="#e2e8f0" strokeWidth="1" />
                      </pattern>
                    </defs>
                    <rect width={CANVAS_W} height={CANVAS_H} fill="url(#grid)" />

                    {!modoEditor && (
                      <g>
                        <text
                          x={CANVAS_W / 2}
                          y={CANVAS_H / 2 - 20}
                          textAnchor="middle"
                          fontSize={16}
                          fontWeight="bold"
                          fill="#64748b"
                          fontFamily="sans-serif"
                        >
                          🗺️ No se ha cargado el plano del penal
                        </text>
                        <text
                          x={CANVAS_W / 2}
                          y={CANVAS_H / 2 + 10}
                          textAnchor="middle"
                          fontSize={12}
                          fill="#94a3b8"
                          fontFamily="sans-serif"
                        >
                          Haga clic en "Cargar mapa" para subir la imagen de distribución física.
                        </text>
                      </g>
                    )}
                  </g>
                )}

                {/* Renderizar celdas */}
                {celdasEnPlano.map(renderCelda)}
              </svg>

              {/* Tooltip flotante */}
{celdaConTooltip && (() => {
  const estado = getEstado(celdaConTooltip);
  const pos = posiciones[celdaConTooltip.id];
  if (!pos) return null;
  
  return (
    <div
      className="absolute z-50 bg-white/95 backdrop-blur-md shadow-2xl rounded-xl border border-gray-200 p-4 transition-all duration-150 animate-fade-in"
      style={{
        left: `${tooltipPos.x + 5}px`, 
        top: `${tooltipPos.y - 60}px`,
        minWidth: '240px',
        pointerEvents: 'auto', // Siempre permitir eventos interactivos dentro del menú clickeado
      }}
      onClick={(e) => e.stopPropagation()} // Evita cerrar el menú al hacer click dentro de él
    >
      <div className="flex items-center justify-between mb-2 pb-2 border-b border-gray-100">
        <div className="flex items-center gap-2">
          <span className={`w-2.5 h-2.5 rounded-full ${
            estado === 'llena' ? 'bg-red-500' : estado === 'limite' ? 'bg-yellow-500' : 'bg-emerald-500'
          }`}></span>
          <span className="font-bold text-gray-900 text-sm">Celda {celdaConTooltip.identificador}</span>
        </div>

        {/* Botón de cerrar/descolocar (quick trigger) */}
        {modoEditor && (
          <button
            type="button"
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              quitarCeldaDelPlano(celdaConTooltip.id, celdaConTooltip.identificador);
            }}
            className="p-1 text-red-600 hover:bg-red-50 rounded-lg transition-colors cursor-pointer"
            style={{ pointerEvents: 'auto' }}
            title="Quitar del plano"
          >
            <X className="w-4 h-4 text-red-600" />
          </button>
        )}
      </div>

      {modoEditor ? (
        <div className="space-y-3.5 text-xs text-gray-700">
          {/* TAMAÑO DEL PUNTO */}
          <div className="space-y-1">
            <div className="flex justify-between items-center text-[10px] font-bold text-gray-400 uppercase tracking-wider">
              <span>Tamaño del Punto</span>
              <span className="text-blue-600 bg-blue-50 px-1.5 py-0.5 rounded font-mono font-bold">
                {pos.r || PUNTO_RADIO}px
              </span>
            </div>
            <input
              type="range"
              min="12"
              max="36"
              value={pos.r || PUNTO_RADIO}
              onChange={(e) => {
                const newR = parseInt(e.target.value, 10);
                setPosiciones(prev => ({
                  ...prev,
                  [celdaConTooltip.id]: {
                    ...prev[celdaConTooltip.id],
                    r: newR
                  }
                }));
              }}
              className="w-full h-1.5 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600 focus:outline-none"
            />
          </div>

          {/* ASIGNAR A PISO */}
          <div className="space-y-1">
            <div className="text-[10px] font-bold text-gray-400 uppercase tracking-wider">
              Mover a Piso
            </div>
            <div className="grid grid-cols-3 gap-1">
              {[1, 2, 3].map((f) => (
                <button
                  key={f}
                  type="button"
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    setPosiciones(prev => ({
                      ...prev,
                      [celdaConTooltip.id]: {
                        ...prev[celdaConTooltip.id],
                        floor: f
                      }
                    }));
                    if (f !== pisoActivo) {
                      setCeldaHover(null); // Ocultar si se mueve a otro piso
                    }
                  }}
                  className={`py-1 rounded text-xs font-semibold border transition-all cursor-pointer ${
                    (pos.floor || 1) === f
                      ? 'bg-blue-600 text-white border-blue-600 shadow-sm'
                      : 'bg-white text-gray-650 border-gray-250 hover:bg-gray-50'
                  }`}
                >
                  Piso {f}
                </button>
              ))}
            </div>
          </div>

          {/* BOTÓN PROMINENTE DE ELIMINAR */}
          <button
            type="button"
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              quitarCeldaDelPlano(celdaConTooltip.id, celdaConTooltip.identificador);
            }}
            className="w-full mt-2 py-1.5 px-3 bg-red-50 hover:bg-red-100 text-red-700 rounded-lg text-xs font-bold flex items-center justify-center gap-1.5 border border-red-155 transition-colors cursor-pointer"
          >
            <X className="w-3.5 h-3.5" />
            Quitar del plano
          </button>
        </div>
      ) : (
        <div className="space-y-1 text-xs text-gray-700">
          <div className="flex justify-between">
            <span className="text-gray-500">Capacidad:</span>
            <span className="font-bold">{celdaConTooltip.capacidad} internos</span>
          </div>
          <div className="flex justify-between">
            <span className="text-gray-500">Ocupación:</span>
            <span className="font-bold">
              {celdaConTooltip.reclusosAsignados.length} ({calculatePct(celdaConTooltip)}%)
            </span>
          </div>
        </div>
      )}
    </div>
  );
})()}
            </div>
          </div>

          {/* Panel Lateral: celdas sin colocar */}
          {modoEditor && (
            <div className="w-full xl:w-72 bg-white rounded-xl border border-gray-200 p-4 shadow-sm shrink-0">
              <h3 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-3 flex items-center justify-between">
                <span>📦 Celdas sin colocar</span>
                <span className="bg-gray-100 text-gray-600 px-2 py-0.5 rounded-full font-bold">
                  {celdasSinColocar.length}
                </span>
              </h3>

              {celdasSinColocar.length === 0 ? (
                <div className="text-center py-8 px-4 border-2 border-dashed border-gray-100 rounded-lg">
                  <p className="text-xs text-gray-400 font-medium">✓ Todas las celdas han sido posicionadas</p>
                </div>
              ) : (
                <div className="space-y-3.5 max-h-[500px] overflow-y-auto pr-1">
                  {celdasSinColocar.map(celda => {
                    const estado = getEstado(celda);
                    const col = COLORES[estado];
                    return (
                      <div
                        key={celda.id}
                        className="rounded-xl border-2 p-3.5 cursor-grab active:cursor-grabbing hover:shadow-md transition-all"
                        style={{ background: col.fill, borderColor: col.stroke }}
                        onMouseDown={(e) => {
                          const nuevaPos = {
                            x: CANVAS_W / 2,
                            y: CANVAS_H / 2,
                            floor: pisoActivo,
                            r: PUNTO_RADIO
                          };
                          setPosiciones(prev => ({
                            ...prev,
                            [celda.id]: nuevaPos,
                          }));
                          setTimeout(() => {
                            iniciarDrag(e, celda.id);
                          }, 10);
                        }}
                      >
                        <div className="flex items-center justify-between border-b pb-2 mb-2" style={{ borderColor: col.stroke, opacity: 0.25 }}>
                          <span className="font-extrabold text-sm" style={{ color: col.text }}>
                            Celda {celda.identificador}
                          </span>
                          <Move className="w-4 h-4" style={{ color: col.stroke }} />
                        </div>
                        <div className="text-xs space-y-1" style={{ color: col.text }}>
                          <div className="font-semibold">👥 Ocupación: {celda.reclusosAsignados.length} / {celda.capacidad}</div>
                          <div>Nivel Conducta: <strong className="font-bold">{celda.nivelConducta}</strong></div>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}

              <div className="mt-4 p-3 bg-blue-50 border border-blue-150 rounded-xl text-xs text-blue-800 leading-relaxed">
                <span className="font-bold block mb-1">💡 Tips de Edición:</span>
                • Haga clic sobre cualquier celda en esta lista y arrástrela al centro del plano.<br />
                • Arrastre los puntos en el plano para colocarlos en su posición real.<br />
                • Haga clic en "Guardar plano" al concluir.
              </div>
            </div>
          )}
        </div>

        {/* ── Modal: Detalle de Celda y Asignación ── */}
        {modalAsignacion && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-55 transition-all duration-300">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg overflow-hidden transform transition-all border border-gray-200">
              {/* Header */}
              <div className="flex items-center justify-between px-6 py-4.5 bg-gray-900 text-white">
                <div>
                  <h3 className="text-lg font-bold">Celda {modalAsignacion.identificador}</h3>
                  <p className="text-xs text-gray-300 mt-0.5">Gestión de internos y plazas disponibles</p>
                </div>
                <button
                  onClick={() => {
                    setModalAsignacion(null)
                    setReclusoSeleccionado('')
                  }}
                  className="p-1.5 hover:bg-gray-800 rounded-lg transition-colors text-white"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              {/* Content */}
              <div className="p-6 space-y-5 max-h-[70vh] overflow-y-auto">

                {/* Resumen Celda */}
                <div className="grid grid-cols-3 gap-3 p-3.5 bg-gray-50 rounded-xl border border-gray-200 text-sm">
                  <div>
                    <span className="text-gray-500 block text-xs font-semibold">Ocupación</span>
                    <span className="font-extrabold text-gray-800 text-base">{modalAsignacion.reclusosAsignados.length} / {modalAsignacion.capacidad}</span>
                  </div>
                  <div>
                    <span className="text-gray-500 block text-xs font-semibold">Nivel Conducta</span>
                    <span className={`inline-block px-2.5 py-0.5 rounded-full text-xs font-bold mt-1 ${modalAsignacion.nivelConducta === 'ALTO' ? 'bg-red-100 text-red-700' :
                      modalAsignacion.nivelConducta === 'MEDIO' ? 'bg-yellow-100 text-yellow-800' :
                        'bg-green-100 text-green-700'
                      }`}>{modalAsignacion.nivelConducta}</span>
                  </div>
                  <div>
                    <span className="text-gray-500 block text-xs font-semibold">Dimensiones</span>
                    <span className="font-extrabold text-gray-800 block mt-0.5">
                      {modalAsignacion.largo && modalAsignacion.ancho ? `${modalAsignacion.largo}m × ${modalAsignacion.ancho}m` : '—'}
                    </span>
                  </div>
                </div>

                {/* Lista de reclusos asignados */}
                <div>
                  <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2.5 flex items-center gap-1.5">
                    <Users className="w-4 h-4 text-gray-400" />
                    Internos en esta celda ({modalAsignacion.reclusosAsignados.length})
                  </h4>
                  {modalAsignacion.reclusosAsignados.length === 0 ? (
                    <p className="text-xs text-gray-400 italic p-4 bg-gray-50 rounded-xl border border-dashed border-gray-200">
                      Esta celda no tiene internos asignados en este momento.
                    </p>
                  ) : (
                    <div className="space-y-2.5">
                      {modalAsignacion.reclusosAsignados.map(recluso => (
                        <div key={recluso.id} className="flex items-center justify-between p-3 border border-gray-100 hover:border-gray-200 rounded-xl hover:bg-gray-50/50 transition-all shadow-sm">
                          <div className="flex items-center gap-3">
                            <div className="w-9 h-9 bg-blue-50 text-blue-600 rounded-full flex items-center justify-center font-bold">
                              {recluso.firstName.charAt(0)}{recluso.firstLastname.charAt(0)}
                            </div>
                            <div>
                              <p className="text-sm font-bold text-gray-800">{recluso.firstName} {recluso.firstLastname}</p>
                              <p className="text-xs text-gray-400">C.I. {recluso.cedula}</p>
                            </div>
                          </div>
                          <button
                            onClick={() => setReclusoExpediente(recluso)}
                            className="text-xs text-blue-600 hover:text-blue-800 hover:underline font-bold"
                          >
                            Ver Expediente
                          </button>
                        </div>
                      ))}
                    </div>
                  )}
                </div>

                {/* Asignar nuevo interno */}
                {modalAsignacion.reclusosAsignados.length < modalAsignacion.capacidad ? (
                  <div className="pt-3.5 border-t border-gray-150">
                    <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2.5">
                      Asignar nuevo interno a la celda
                    </h4>
                    {reclusosSinCelda.length === 0 ? (
                      <p className="text-xs text-amber-600 p-3 bg-amber-50 rounded-xl border border-amber-100 flex items-center gap-2">
                        <AlertTriangle className="w-4.5 h-4.5 flex-shrink-0" />
                        No hay reclusos sin celda asignada en el sistema.
                      </p>
                    ) : (
                      <div className="space-y-2 max-h-44 overflow-y-auto p-1.5 border border-gray-200 rounded-xl bg-gray-50/50">
                        {reclusosSinCelda.map(recluso => (
                          <label
                            key={recluso.id}
                            className={`flex items-center justify-between p-2.5 rounded-lg cursor-pointer border transition-all ${reclusoSeleccionado === recluso.id
                              ? 'bg-blue-50 border-blue-400 shadow-sm'
                              : 'bg-white border-transparent hover:border-gray-200'
                              }`}
                          >
                            <div className="flex items-center gap-2.5">
                              <input
                                type="radio"
                                name="recluso-asignar"
                                value={recluso.id}
                                checked={reclusoSeleccionado === recluso.id}
                                onChange={e => setReclusoSeleccionado(e.target.value)}
                                className="w-4 h-4 text-blue-600 border-gray-300 focus:ring-blue-500"
                              />
                              <div>
                                <p className="text-xs font-bold text-gray-800">{recluso.firstName} {recluso.firstLastname}</p>
                                <p className="text-[10px] text-gray-400">C.I. {recluso.cedula}</p>
                              </div>
                            </div>
                            <span className="text-[9px] bg-amber-50 border border-amber-200 px-2 py-0.5 rounded text-amber-700 font-bold uppercase tracking-wider">Sin celda</span>
                          </label>
                        ))}
                      </div>
                    )}
                  </div>
                ) : (
                  <div className="p-3.5 bg-red-50 border border-red-150 rounded-xl text-xs text-red-800 flex items-center gap-2">
                    <AlertTriangle className="w-4.5 h-4.5 text-red-500 flex-shrink-0" />
                    <span>Esta celda se encuentra al 100% de su capacidad. No se pueden asignar más internos.</span>
                  </div>
                )}
              </div>

              {/* Footer */}
              <div className="flex gap-3 px-6 py-4.5 bg-gray-50 border-t border-gray-150">
                <button
                  onClick={() => {
                    setModalAsignacion(null)
                    setReclusoSeleccionado('')
                  }}
                  className="flex-1 px-4 py-2.5 border border-gray-300 text-gray-700 rounded-xl hover:bg-gray-100 transition-colors text-xs font-bold uppercase tracking-wider"
                >
                  Cancelar
                </button>
                {modalAsignacion.reclusosAsignados.length < modalAsignacion.capacidad && (
                  <button
                    onClick={confirmarAsignacion}
                    disabled={!reclusoSeleccionado}
                    className="flex-1 px-4 py-2.5 bg-gray-900 text-white rounded-xl hover:bg-gray-800 transition-all disabled:bg-gray-300 disabled:cursor-not-allowed text-xs font-bold uppercase tracking-wider shadow-sm"
                  >
                    Confirmar asignación
                  </button>
                )}
              </div>
            </div>
          </div>
        )}

        {/* ── Modal de Expediente Detallado (Dossier) ── */}
        {reclusoExpediente && (
          <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center z-60 transition-all duration-300">
            <div className="bg-white rounded-2xl shadow-2xl w-full max-w-2xl mx-4 max-h-[85vh] overflow-y-auto border border-gray-200">

              {/* Header */}
              <div className="flex items-center justify-between p-6 border-b border-gray-150 bg-gray-55/30">
                <div className="flex items-center gap-3.5">
                  {reclusoExpediente.photoUrl ? (
                    <img
                      src={reclusoExpediente.photoUrl}
                      alt="Fotografía"
                      className="w-14 h-14 rounded-full object-cover border border-gray-200"
                    />
                  ) : (
                    <div className="w-14 h-14 bg-gray-100 rounded-full flex items-center justify-center border border-gray-200 shadow-inner">
                      <User className="w-7 h-7 text-gray-500" />
                    </div>
                  )}
                  <div>
                    <h3 className="text-xl font-bold text-gray-900 leading-tight">
                      {reclusoExpediente.firstName} {reclusoExpediente.secondName || ''}{' '}
                      {reclusoExpediente.firstLastname} {reclusoExpediente.secondLastname || ''}
                    </h3>
                    <p className="text-xs text-gray-400 font-semibold uppercase tracking-wider mt-0.5">Cédula: {reclusoExpediente.cedula}</p>
                  </div>
                </div>
                <button
                  onClick={() => setReclusoExpediente(null)}
                  className="p-2 hover:bg-gray-100 rounded-xl transition-colors text-gray-400 hover:text-gray-600 border border-transparent hover:border-gray-200"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>

              {/* Body */}
              <div className="p-6 space-y-5">

                {/* Estado Tag */}
                <div className="flex items-center gap-3">
                  <span className={`px-3 py-1 rounded-full text-xs font-bold uppercase tracking-wider ${reclusoExpediente.status === 'ACTIVO_CON_CELDA'
                    ? 'bg-emerald-100 text-emerald-800 border border-emerald-200'
                    : 'bg-amber-100 text-amber-800 border border-amber-250'
                    }`}>
                    {reclusoExpediente.status === 'ACTIVO_CON_CELDA' ? 'Activo - Asignado' : 'Pendiente Asignación'}
                  </span>
                  {reclusoExpediente.cellIdentifier && (
                    <span className="text-sm font-medium text-gray-550">
                      Celda asignada: <strong className="text-gray-800">Celda {reclusoExpediente.cellIdentifier}</strong>
                    </span>
                  )}
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {/* Datos Personales */}
                  <div className="bg-gray-50 rounded-xl p-4.5 border border-gray-150 shadow-sm space-y-3">
                    <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider border-b border-gray-200/60 pb-1.5">Datos personales</h4>
                    <div className="space-y-2 text-xs">
                      <div className="flex justify-between border-b border-gray-100 pb-1">
                        <span className="text-gray-500">Fecha de nacimiento:</span>
                        <span className="font-semibold text-gray-800">{reclusoExpediente.birthDate || '—'}</span>
                      </div>
                      <div className="flex justify-between border-b border-gray-100 pb-1">
                        <span className="text-gray-500">Edad:</span>
                        <span className="font-semibold text-gray-800">{calculateAge(reclusoExpediente.birthDate)} años</span>
                      </div>
                      <div className="flex justify-between border-b border-gray-100 pb-1">
                        <span className="text-gray-500">Complexión:</span>
                        <span className="font-semibold text-gray-800">{reclusoExpediente.bodyBuild || '—'}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-500">Estatura / Peso:</span>
                        <span className="font-semibold text-gray-800">
                          {reclusoExpediente.heightCm ? `${reclusoExpediente.heightCm} cm` : '—'} / {reclusoExpediente.weightKg ? `${reclusoExpediente.weightKg} kg` : '—'}
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* Información Judicial */}
                  <div className="bg-gray-50 rounded-xl p-4.5 border border-gray-150 shadow-sm space-y-3">
                    <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider border-b border-gray-200/60 pb-1.5">Información judicial</h4>
                    <div className="space-y-2 text-xs">
                      <div className="flex justify-between border-b border-gray-100 pb-1">
                        <span className="text-gray-500">Delito:</span>
                        <span className="font-semibold text-gray-800 truncate max-w-[160px]" title={reclusoExpediente.crime}>{reclusoExpediente.crime || '—'}</span>
                      </div>
                      <div className="flex justify-between border-b border-gray-100 pb-1">
                        <span className="text-gray-500">N° de expediente:</span>
                        <span className="font-semibold text-gray-800">{reclusoExpediente.caseNumber || '—'}</span>
                      </div>
                      <div className="flex justify-between border-b border-gray-100 pb-1">
                        <span className="text-gray-500">Fecha de ingreso:</span>
                        <span className="font-semibold text-gray-800">{reclusoExpediente.admissionDate || '—'}</span>
                      </div>
                      <div className="flex justify-between">
                        <span className="text-gray-500">Condena total:</span>
                        <span className="font-semibold text-gray-800">
                          {reclusoExpediente.sentenceYears || 0} años, {reclusoExpediente.sentenceMonths || 0} meses
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Señas Particulares */}
                {reclusoExpediente.distinguishingMarks && (
                  <div className="bg-gray-50 rounded-xl p-4 border border-gray-150">
                    <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">Señas particulares</h4>
                    <p className="text-xs text-gray-700 leading-relaxed font-semibold">{reclusoExpediente.distinguishingMarks}</p>
                  </div>
                )}

                {/* Pertenencias */}
                {reclusoExpediente.belongings && reclusoExpediente.belongings.length > 0 && (
                  <div className="bg-gray-50 rounded-xl p-4 border border-gray-150">
                    <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2 border-b border-gray-200/50 pb-1.5">Pertenencias registradas</h4>
                    <div className="space-y-1.5">
                      {reclusoExpediente.belongings.map((b, i) => (
                        <div key={i} className="flex justify-between text-xs border-b border-gray-100/60 pb-1 last:border-0">
                          <span className="text-gray-700 font-semibold">📦 {b.description}</span>
                          <span className="text-gray-500">Cantidad: <strong className="text-gray-800">{b.quantity}</strong></span>
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>

              {/* Footer */}
              <div className="px-6 pb-6 pt-3 border-t border-gray-100 bg-gray-50/50">
                <button
                  onClick={() => setReclusoExpediente(null)}
                  className="w-full py-3 bg-gray-900 text-white rounded-xl hover:bg-gray-800 font-bold transition-colors text-xs uppercase tracking-wider shadow-sm"
                >
                  Cerrar Expediente
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </SidebarLayout>
  );
}
