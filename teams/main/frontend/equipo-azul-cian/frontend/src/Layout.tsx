import React from 'react';

// Custom inline SVG icons matching the wireframes
export const IconShield: React.FC = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
  </svg>
);

export const IconHome: React.FC = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
    <polyline points="9 22 9 12 15 12 15 22"/>
  </svg>
);

export const IconIncident: React.FC = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
    <line x1="12" y1="9" x2="12" y2="13"/>
    <line x1="12" y1="17" x2="12.01" y2="17"/>
  </svg>
);

export const IconCar: React.FC = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <rect x="1" y="3" width="15" height="13" rx="2" ry="2"/>
    <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"/>
    <circle cx="5.5" cy="18.5" r="2.5"/>
    <circle cx="18.5" cy="18.5" r="2.5"/>
  </svg>
);

export const IconMap: React.FC = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <polygon points="3 6 9 3 15 6 21 3 21 18 15 21 9 18 3 21"/>
    <line x1="9" y1="3" x2="9" y2="18"/>
    <line x1="15" y1="6" x2="15" y2="21"/>
  </svg>
);

export const IconLink: React.FC = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"/>
    <path d="M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"/>
  </svg>
);

export const IconUser: React.FC = () => (
  <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
    <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
    <circle cx="12" cy="7" r="4"/>
  </svg>
);

interface LayoutProps {
  children: React.ReactNode;
  currentView: string;
  onViewChange: (view: string) => void;
}

interface MenuItem {
  id: string;
  label: string;
  icon: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children, currentView, onViewChange }) => {
  const menuItems: MenuItem[] = [
    { id: 'inicio', label: 'Inicio', icon: <IconHome /> },
    { id: 'incidentes', label: 'Incidentes', icon: <IconIncident /> },
    { id: 'patrullas', label: 'Patrullas', icon: <IconCar /> },
    { id: 'mapa', label: 'Mapa', icon: <IconMap /> },
    { id: 'asignaciones', label: 'Asignaciones', icon: <IconLink /> },
  ];

  return (
    <div style={styles.container}>
      {/* Sidebar */}
      <aside style={styles.sidebar}>
        <div style={styles.logoContainer}>
          <div style={styles.logoBox}>
            <IconShield />
          </div>
          <div>
            <h1 style={styles.logoTitle}>Centro de Mando</h1>
            <span style={styles.logoSub}>Panel de Control</span>
          </div>
        </div>

        <nav style={styles.nav}>
          {menuItems.map((item) => {
            const isActive = currentView === item.id;
            return (
              <button
                key={item.id}
                onClick={() => onViewChange(item.id)}
                style={{
                  ...styles.navButton,
                  ...(isActive ? styles.navButtonActive : {}),
                }}
              >
                <span style={{
                  ...styles.navIcon,
                  color: isActive ? 'var(--color-blue)' : 'var(--text-muted)'
                }}>
                  {item.icon}
                </span>
                <span style={isActive ? styles.navTextActive : styles.navText}>
                  {item.label}
                </span>
              </button>
            );
          })}
        </nav>

        <div style={styles.sidebarFooter}>
          <span>v1.0 — Prototipo Demo</span>
        </div>
      </aside>

      {/* Main Content wrapper */}
      <div style={styles.mainWrapper}>
        {/* Header */}
        <header style={styles.header}>
          <h2 style={styles.headerTitle}>
            Sistema de Gestión de Incidentes y Patrullaje Policial
          </h2>
          <div style={styles.headerRight}>
            <div style={styles.statusIndicator}>
              <span style={styles.statusDot}></span>
              <span style={styles.statusText}>En línea</span>
            </div>
            <div style={styles.profileBox}>
              <IconUser />
            </div>
          </div>
        </header>

        {/* Content Area */}
        <main style={styles.content}>
          {children}
        </main>
      </div>
    </div>
  );
};

const styles: Record<string, React.CSSProperties> = {
  container: {
    display: 'flex',
    minHeight: '100vh',
    backgroundColor: 'var(--bg-dark)',
  },
  sidebar: {
    width: '260px',
    backgroundColor: 'var(--bg-sidebar)',
    borderRight: '1px solid var(--border)',
    display: 'flex',
    flexDirection: 'column',
    padding: '24px 0',
    flexShrink: 0,
  },
  logoContainer: {
    display: 'flex',
    alignItems: 'center',
    padding: '0 24px',
    marginBottom: '40px',
    gap: '12px',
  },
  logoBox: {
    width: '40px',
    height: '40px',
    borderRadius: '8px',
    backgroundColor: 'var(--color-blue)',
    color: '#ffffff',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  logoTitle: {
    fontSize: '1rem',
    fontWeight: '600',
    color: '#ffffff',
    margin: 0,
    lineHeight: '1.2',
  },
  logoSub: {
    fontSize: '0.75rem',
    color: 'var(--text-muted)',
  },
  nav: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
    padding: '0 16px',
    flexGrow: 1,
  },
  navButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    padding: '12px 16px',
    borderRadius: '8px',
    border: 'none',
    backgroundColor: 'transparent',
    cursor: 'pointer',
    textAlign: 'left',
    width: '100%',
    transition: 'all 0.2s ease',
  },
  navButtonActive: {
    backgroundColor: 'var(--bg-active)',
  },
  navIcon: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  navText: {
    fontSize: '0.95rem',
    fontWeight: '500',
    color: 'var(--text-muted)',
  },
  navTextActive: {
    fontSize: '0.95rem',
    fontWeight: '600',
    color: '#ffffff',
  },
  sidebarFooter: {
    padding: '0 24px',
    fontSize: '0.75rem',
    color: 'rgba(255, 255, 255, 0.2)',
  },
  mainWrapper: {
    display: 'flex',
    flexDirection: 'column',
    flexGrow: 1,
    overflowX: 'hidden',
  },
  header: {
    height: '70px',
    backgroundColor: 'var(--bg-sidebar)',
    borderBottom: '1px solid var(--border)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '0 32px',
    flexShrink: 0,
  },
  headerTitle: {
    fontSize: '1.05rem',
    fontWeight: '500',
    color: '#ffffff',
    margin: 0,
  },
  headerRight: {
    display: 'flex',
    alignItems: 'center',
    gap: '24px',
  },
  statusIndicator: {
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  statusDot: {
    width: '8px',
    height: '8px',
    borderRadius: '50%',
    backgroundColor: 'var(--color-green)',
    boxShadow: '0 0 8px var(--color-green)',
  },
  statusText: {
    fontSize: '0.85rem',
    color: 'var(--color-green)',
    fontWeight: '500',
  },
  profileBox: {
    width: '36px',
    height: '36px',
    borderRadius: '50%',
    backgroundColor: 'rgba(255, 255, 255, 0.05)',
    border: '1px solid var(--border)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    color: 'var(--text-muted)',
    cursor: 'pointer',
  },
  content: {
    flexGrow: 1,
    padding: '32px',
    overflowY: 'auto',
    backgroundColor: 'var(--bg-dark)',
  },
};

export default Layout;
