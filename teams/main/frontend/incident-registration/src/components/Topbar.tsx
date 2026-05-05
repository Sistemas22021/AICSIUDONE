/**
 * Renderiza la barra superior del sistema.
 * - Provee botón de guardado del expediente.
 * - Indicar estados: guardando, éxito.
 */
interface TopbarProps {
    saving: boolean;
    success: boolean;
    onSave: () => void;
}

export function Topbar({ saving, success, onSave }: TopbarProps) {
    return (
        <div className="topbar">
            <div className="breadcrumb">
                SIEGC
                <svg width="14" height="14" viewBox="0 0 14 14" fill="none"
                     stroke="currentColor" strokeWidth="1.5" style={{ opacity: 0.4 }}>
                    <path d="M5 3l4 4-4 4" />
                </svg>
                <span>Nuevo Expediente</span>
            </div>

            <div className="topbar-actions">
                <button
                    className={`btn-primary${success ? " success" : ""}`}
                    onClick={onSave}
                    disabled={saving}
                >
                    {saving ? "Guardando…" : success ? "✓ Guardado" : "Guardar Expediente"}
                </button>
            </div>
        </div>
    );
}