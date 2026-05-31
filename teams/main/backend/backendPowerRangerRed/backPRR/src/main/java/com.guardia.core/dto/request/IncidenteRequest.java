package com.guardia.core.dto.request;

import lombok.Data;
import java.util.List;

/**
 * DTO que recibe exactamente el JSON que envía el frontend (crearPayloadIncidente.ts).
 *
 * Estructura que llega del frontend:
 * {
 *   "tipoRegistro": "denuncia_formal",
 *   "ubicacion": { "municipio":"...", "sector":"...", "direccion":"...",
 *                  "referencia":"...", "coordenadas": {"lat":0.0, "lng":0.0} },
 *   "delitos": [ { "tipoDelito":"...", "subtipoDelito":"...",
 *                  "fechaHecho":"2025-01-01", "horaInicio":"08:00",
 *                  "horaFin":"09:00", "hechoEnCurso":false } ],
 *   "descripcion": "...",
 *   "involucrados": [ { "tipo":"victima", "nombre":"...", "identificacion":"...",
 *                        "nacionalidad":"...", "telefono":"...", "direccion":"..." } ],
 *   "esDenunciaFormal": true,
 *   "denunciante": { "nombre":"...", "identificacion":"...", "telefono":"...",
 *                    "nacionalidad":"...", "direccion":"...", "relacionConCrimen":"..." },
 *   "reporte": { "fechaReporte":"...", "horaReporte":"...",
 *                "agenteRegistrador":"...", "investigador":"..." },
 *   "fotos": []
 * }
 */
@Data
public class IncidenteRequest {

    private String tipoRegistro;

    private UbicacionFrontRequest ubicacion;

    private List<DelitoFrontRequest> delitos;

    private String descripcion;

    private List<InvolucradoFrontRequest> involucrados;

    private Boolean esDenunciaFormal;

    private DenuncianteFrontRequest denunciante;

    private ReporteFrontRequest reporte;

    private List<Object> fotos; // no se persiste en esta versión

    // ─── DTOs internos (coinciden exactamente con el JSON del frontend) ────────

    @Data
    public static class UbicacionFrontRequest {
        private String municipio;
        private String sector;
        private String direccion;
        private String referencia;
        private CoordenadaFrontRequest coordenadas; // puede ser null si no se capturó GPS
    }

    @Data
    public static class CoordenadaFrontRequest {
        private Double lat;
        private Double lng;
    }

    @Data
    public static class DelitoFrontRequest {
        private String tipoDelito;      // nombre del tipo
        private String subtipoDelito;   // nombre del subtipo
        private String fechaHecho;      // "yyyy-MM-dd"
        private String horaInicio;      // "HH:mm"
        private String horaFin;         // "HH:mm" o null
        private boolean hechoEnCurso;
    }

    @Data
    public static class InvolucradoFrontRequest {
        private String tipo;            // "victima", "sospechoso", etc.
        private String nombre;          // nombre completo
        private String identificacion;  // cédula
        private String nacionalidad;
        private String telefono;
        private String direccion;
    }

    @Data
    public static class DenuncianteFrontRequest {
        private String nombre;
        private String identificacion;
        private String telefono;
        private String nacionalidad;
        private String direccion;
        private String relacionConCrimen; // el frontend usa este nombre
    }

    @Data
    public static class ReporteFrontRequest {
        private String fechaReporte;
        private String horaReporte;
        private String agenteRegistrador;
        private String investigador;
    }
}
