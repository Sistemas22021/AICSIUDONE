import React, { useState, useEffect } from 'react';
import { fetchWithRetry } from '../utils/fetchWithRetry';

type IncidentStatus = 'ACTIVE' | 'IN_PROGRESS' | 'CLOSED';
type PatrolStatus = 'AVAILABLE' | 'EN_ROUTE' | 'BUSY' | 'OUT_OF_SERVICE';

interface Incident {
  id: number;
  type: string;
  description: string;
  latitude: number;
  longitude: number;
  status: IncidentStatus;
  priority: string;
  createdAt?: string;
  updatedAt?: string;
}

interface Patrol {
  id: number;
  code: string;
  officerName: string;
  status: PatrolStatus;
  lastUpdated?: string;
}

interface Assignment {
  id: number;
  incident: Incident;
  patrol: Patrol;
  assignedAt?: string;
  finishedAt?: string;
}

interface IncidentSummary {
  active: number;
  inProgress: number;
  closed: number;
  total: number;
}

interface ActivityEvent {
  id: string;
  timeStr: string;
  text: string;
  color: 'red' | 'blue' | 'green';
  dateObj: Date;
}

// Custom SVGs for a modern, premium look
const IconAlert: React.FC = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
    <line x1="12" y1="9" x2="12" y2="13"/>
    <line x1="12" y1="17" x2="12.01" y2="17"/>
  </svg>
);

const IconClock: React.FC = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <circle cx="12" cy="12" r="10"/>
    <polyline points="12 6 12 12 16 14"/>
  </svg>
);

const IconCheck: React.FC = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/>
    <polyline points="22 4 12 14.01 9 11.01"/>
  </svg>
);

const IconPatrolCar: React.FC = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <rect x="2" y="6" width="15" height="11" rx="2" ry="2"/>
    <path d="M17 10h4a2 2 0 0 1 2 2v3a1 1 0 0 1-1 1h-5v-6z"/>
    <circle cx="6" cy="19" r="2.5"/>
    <circle cx="16" cy="19" r="2.5"/>
    <path d="M7 6V4a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v2"/>
  </svg>
);

const IconActivityPulse: React.FC = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
    <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/>
  </svg>
);

const formatTime = (dateStr?: string): string => {
  if (!dateStr) return '--:--';
  try {
    const d = new Date(dateStr);
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    return `${hours}:${minutes}`;
  } catch {
    return '--:--';
  }
};

const API_BASE = 'http://localhost:8080/api';

const Inicio: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [summary, setSummary] = useState<IncidentSummary>({ active: 0, inProgress: 0, closed: 0, total: 0 });
  const [patrolsCount, setPatrolsCount] = useState<number>(0);
  const [recentActivity, setRecentActivity] = useState<ActivityEvent[]>([]);

  const fetchDashboardData = async () => {
    try {
      setError(null);
      const [resSummary, resPatrols, resIncidents, resAssignments] = await Promise.all([
        fetchWithRetry(`${API_BASE}/incidents/summary`),
        fetchWithRetry(`${API_BASE}/patrols`),
        fetchWithRetry(`${API_BASE}/incidents`),
        fetchWithRetry(`${API_BASE}/assignments`)
      ]);

      const summaryData: IncidentSummary = await resSummary.json();
      const patrolsData: Patrol[] = await resPatrols.json();
      const incidentsData: Incident[] = await resIncidents.json();
      const assignmentsData: Assignment[] = await resAssignments.json();

      setSummary(summaryData);

      // Conteo de patrullas AVAILABLE
      const availablePatrols = patrolsData.filter(p => p.status === 'AVAILABLE').length;
      setPatrolsCount(availablePatrols);

      // Compilación de actividad reciente
      const events: ActivityEvent[] = [];

      incidentsData.forEach((inc) => {
        const timeVal = inc.createdAt || inc.updatedAt;
        if (timeVal) {
          events.push({
            id: `inc-reg-${inc.id}`,
            timeStr: formatTime(inc.createdAt),
            text: `Incidente INC-${inc.id} registrado — ${inc.description}`,
            color: 'red',
            dateObj: new Date(timeVal)
          });
        }
        if (inc.status === 'CLOSED' && inc.updatedAt) {
          events.push({
            id: `inc-cls-${inc.id}`,
            timeStr: formatTime(inc.updatedAt),
            text: `Incidente INC-${inc.id} cerrado — ${inc.description}`,
            color: 'green',
            dateObj: new Date(inc.updatedAt)
          });
        }
      });

      assignmentsData.forEach((asg) => {
        const timeVal = asg.assignedAt;
        if (timeVal) {
          events.push({
            id: `asg-${asg.id}`,
            timeStr: formatTime(asg.assignedAt),
            text: `Patrulla ${asg.patrol?.code || 'PAT'} asignada a INC-${asg.incident?.id || 'INC'}`,
            color: 'blue',
            dateObj: new Date(timeVal)
          });
        }
      });

      // Ordenar cronológicamente descendente y limitar a las últimas 8 acciones
      const sortedEvents = events
        .sort((a, b) => b.dateObj.getTime() - a.dateObj.getTime())
        .slice(0, 8);

      setRecentActivity(sortedEvents);
    } catch (err) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Error al conectar con la API.');
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();

    // Polling cada 15 segundos para mantener actualizado el panel
    const interval = setInterval(() => {
      fetchDashboardData();
    }, 15000);

    return () => clearInterval(interval);
  }, []);

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
      <style>{`
        .metric-card {
          background-color: var(--bg-card);
          border: 1px solid var(--border);
          border-radius: 16px;
          padding: 24px;
          display: flex;
          align-items: center;
          gap: 20px;
          transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;
          position: relative;
          overflow: hidden;
        }
        .metric-card::before {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          width: 4px;
          height: 100%;
        }
        .metric-card:hover {
          transform: translateY(-4px);
          box-shadow: 0 12px 20px -10px rgba(0, 0, 0, 0.4);
          border-color: rgba(255, 255, 255, 0.1);
        }
        .metric-card-red::before { background-color: var(--color-red); }
        .metric-card-orange::before { background-color: var(--color-orange); }
        .metric-card-green::before { background-color: var(--color-green); }
        .metric-card-blue::before { background-color: var(--color-blue); }

        .activity-pulse-dot {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          display: inline-block;
          position: relative;
        }
        .activity-pulse-dot::after {
          content: '';
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          border-radius: 50%;
          animation: dot-pulse 2s infinite ease-in-out;
        }
        .dot-red { background-color: var(--color-red); }
        .dot-red::after { box-shadow: 0 0 0 4px rgba(239, 68, 68, 0.4); }
        .dot-orange { background-color: var(--color-orange); }
        .dot-orange::after { box-shadow: 0 0 0 4px rgba(245, 158, 11, 0.4); }
        .dot-green { background-color: var(--color-green); }
        .dot-green::after { box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.4); }
        .dot-blue { background-color: var(--color-blue); }
        .dot-blue::after { box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.4); }

        @keyframes dot-pulse {
          0% { transform: scale(1); opacity: 1; }
          100% { transform: scale(2.4); opacity: 0; }
        }

        .skeleton-block {
          background: linear-gradient(90deg, #151a2e 25%, #1f2742 50%, #151a2e 75%);
          background-size: 200% 100%;
          animation: loading-shimmer 1.5s infinite;
          border-radius: 8px;
        }
        @keyframes loading-shimmer {
          0% { background-position: 200% 0; }
          100% { background-position: -200% 0; }
        }
      `}</style>

      {/* Header */}
      <div style={{ marginBottom: '32px' }}>
        <h1 style={{ fontSize: '2.25rem', fontWeight: 700, letterSpacing: '-0.02em', marginBottom: '6px', color: '#ffffff' }}>
          Panel de Control
        </h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '1rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <span>Resumen operativo en</span>
          <span style={{ color: 'var(--color-blue)', fontWeight: 600, position: 'relative' }}>
            tiempo real
          </span>
          <span style={{ display: 'inline-flex', width: '6px', height: '6px', borderRadius: '50%', backgroundColor: 'var(--color-green)', boxShadow: '0 0 8px var(--color-green)' }}></span>
        </p>
      </div>

      {error && (
        <div style={{
          backgroundColor: 'rgba(239, 68, 68, 0.08)',
          border: '1px solid rgba(239, 68, 68, 0.2)',
          borderRadius: '12px',
          padding: '16px 20px',
          marginBottom: '24px',
          color: 'var(--color-red)',
          fontSize: '0.95rem',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <span>{error}</span>
          <button
            onClick={() => { setLoading(true); fetchDashboardData(); }}
            style={{ background: 'none', border: 'none', color: '#ffffff', cursor: 'pointer', textDecoration: 'underline', fontWeight: 500 }}
          >
            Reintentar
          </button>
        </div>
      )}

      {/* Metrics Row */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
        gap: '24px',
        marginBottom: '40px'
      }}>
        {loading ? (
          Array.from({ length: 4 }).map((_, idx) => (
            <div key={idx} className="skeleton-block" style={{ height: '112px' }} />
          ))
        ) : (
          <>
            {/* Incidentes Activos */}
            <div className="metric-card metric-card-red">
              <div style={{
                backgroundColor: 'rgba(239, 68, 68, 0.1)',
                color: 'var(--color-red)',
                borderRadius: '12px',
                width: '48px',
                height: '48px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexShrink: 0
              }}>
                <IconAlert />
              </div>
              <div>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem', fontWeight: 500, display: 'block', marginBottom: '4px' }}>
                  Incidentes Activos
                </span>
                <span style={{ fontSize: '1.75rem', fontWeight: 700, color: '#ffffff', lineHeight: 1.1 }}>
                  {summary.active}
                </span>
              </div>
            </div>

            {/* En Proceso */}
            <div className="metric-card metric-card-orange">
              <div style={{
                backgroundColor: 'rgba(245, 158, 11, 0.1)',
                color: 'var(--color-orange)',
                borderRadius: '12px',
                width: '48px',
                height: '48px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexShrink: 0
              }}>
                <IconClock />
              </div>
              <div>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem', fontWeight: 500, display: 'block', marginBottom: '4px' }}>
                  En Proceso
                </span>
                <span style={{ fontSize: '1.75rem', fontWeight: 700, color: '#ffffff', lineHeight: 1.1 }}>
                  {summary.inProgress}
                </span>
              </div>
            </div>

            {/* Cerrados */}
            <div className="metric-card metric-card-green">
              <div style={{
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                color: 'var(--color-green)',
                borderRadius: '12px',
                width: '48px',
                height: '48px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexShrink: 0
              }}>
                <IconCheck />
              </div>
              <div>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem', fontWeight: 500, display: 'block', marginBottom: '4px' }}>
                  Cerrados
                </span>
                <span style={{ fontSize: '1.75rem', fontWeight: 700, color: '#ffffff', lineHeight: 1.1 }}>
                  {summary.closed}
                </span>
              </div>
            </div>

            {/* Patrullas Disponibles */}
            <div className="metric-card metric-card-blue">
              <div style={{
                backgroundColor: 'rgba(37, 99, 235, 0.1)',
                color: 'var(--color-blue)',
                borderRadius: '12px',
                width: '48px',
                height: '48px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                flexShrink: 0
              }}>
                <IconPatrolCar />
              </div>
              <div>
                <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem', fontWeight: 500, display: 'block', marginBottom: '4px' }}>
                  Patrullas Disponibles
                </span>
                <span style={{ fontSize: '1.75rem', fontWeight: 700, color: '#ffffff', lineHeight: 1.1 }}>
                  {patrolsCount}
                </span>
              </div>
            </div>
          </>
        )}
      </div>

      {/* Recent Activity Section */}
      <div style={{
        backgroundColor: 'var(--bg-card)',
        border: '1px solid var(--border)',
        borderRadius: '16px',
        padding: '28px'
      }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '10px',
          marginBottom: '24px',
          color: '#ffffff'
        }}>
          <span style={{ color: 'var(--color-blue)', display: 'flex', alignItems: 'center' }}>
            <IconActivityPulse />
          </span>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 600 }}>Actividad Reciente</h2>
        </div>

        {loading ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {Array.from({ length: 4 }).map((_, idx) => (
              <div key={idx} className="skeleton-block" style={{ height: '48px', width: '100%' }} />
            ))}
          </div>
        ) : recentActivity.length === 0 ? (
          <div style={{ padding: '32px 0', textAlign: 'center', color: 'var(--text-muted)' }}>
            No hay actividad operativa registrada recientemente.
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            {recentActivity.map((event, index) => (
              <div
                key={event.id}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  padding: '16px 0',
                  borderBottom: index === recentActivity.length - 1 ? 'none' : '1px solid var(--border)'
                }}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                  <span className={`activity-pulse-dot dot-${event.color}`} />
                  <span style={{
                    color: 'var(--text-main)',
                    fontSize: '0.95rem',
                    fontWeight: 500,
                    lineHeight: '1.4'
                  }}>
                    {event.text}
                  </span>
                </div>
                <span style={{
                  color: 'var(--text-muted)',
                  fontSize: '0.875rem',
                  fontWeight: 500,
                  whiteSpace: 'nowrap',
                  marginLeft: '16px'
                }}>
                  {event.timeStr}
                </span>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default Inicio;
