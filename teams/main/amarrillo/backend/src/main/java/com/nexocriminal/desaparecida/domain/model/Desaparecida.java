package com.nexocriminal.desaparecida.domain.model;

import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de dominio de Persona Desaparecida. POJO puro.
 * Las fotos NO se modelan aca: son infraestructura (archivos) y las maneja
 * el servicio de fotos. La ultima ubicacion se referencia solo por id.
 */
public class Desaparecida {

    private final Long id;
    private String documento;
    private String nombre;
    private String apellido;
    private String alias;
    private LocalDate fechaNacimiento;
    private String genero;
    private Integer estaturaCm;
    private Integer pesoKg;
    private String contextura;
    private String colorCabello;
    private String colorOjos;
    private String senasParticulares;
    private String ropaAlDesaparecer;
    private String fotoUrl;
    private LocalDateTime fechaDesaparicion;
    private Long ultimaUbicacionId;
    private String circunstancias;
    private String reportanteNombre;
    private String reportanteTelefono;
    private String reportanteRelacion;
    private EstadoDesaparicion estado;
    private PrioridadDesaparicion prioridad;
    private String analisisIA;
    private String zonasBusquedaIA;
    private LocalDateTime fechaResolucion;
    private final LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public Desaparecida(Long id, String documento, String nombre, String apellido, String alias,
                        LocalDate fechaNacimiento, String genero, Integer estaturaCm, Integer pesoKg,
                        String contextura, String colorCabello, String colorOjos, String senasParticulares,
                        String ropaAlDesaparecer, String fotoUrl, LocalDateTime fechaDesaparicion,
                        Long ultimaUbicacionId, String circunstancias, String reportanteNombre,
                        String reportanteTelefono, String reportanteRelacion, EstadoDesaparicion estado,
                        PrioridadDesaparicion prioridad, String analisisIA, String zonasBusquedaIA,
                        LocalDateTime fechaResolucion, LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.documento = documento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.alias = alias;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.estaturaCm = estaturaCm;
        this.pesoKg = pesoKg;
        this.contextura = contextura;
        this.colorCabello = colorCabello;
        this.colorOjos = colorOjos;
        this.senasParticulares = senasParticulares;
        this.ropaAlDesaparecer = ropaAlDesaparecer;
        this.fotoUrl = fotoUrl;
        this.fechaDesaparicion = fechaDesaparicion;
        this.ultimaUbicacionId = ultimaUbicacionId;
        this.circunstancias = circunstancias;
        this.reportanteNombre = reportanteNombre;
        this.reportanteTelefono = reportanteTelefono;
        this.reportanteRelacion = reportanteRelacion;
        this.estado = estado;
        this.prioridad = prioridad;
        this.analisisIA = analisisIA;
        this.zonasBusquedaIA = zonasBusquedaIA;
        this.fechaResolucion = fechaResolucion;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    /** Crea una desaparecida nueva (sin id). */
    public static Desaparecida crear(String documento, String nombre, String apellido, String alias,
                                     LocalDate fechaNacimiento, String genero, Integer estaturaCm, Integer pesoKg,
                                     String contextura, String colorCabello, String colorOjos, String senasParticulares,
                                     String ropaAlDesaparecer, LocalDateTime fechaDesaparicion, Long ultimaUbicacionId,
                                     String circunstancias, String reportanteNombre, String reportanteTelefono,
                                     String reportanteRelacion, EstadoDesaparicion estado, PrioridadDesaparicion prioridad) {
        LocalDateTime ahora = LocalDateTime.now();
        return new Desaparecida(
                null, documento, nombre, apellido, alias, fechaNacimiento, genero, estaturaCm, pesoKg,
                contextura, colorCabello, colorOjos, senasParticulares, ropaAlDesaparecer, null,
                fechaDesaparicion, ultimaUbicacionId, circunstancias, reportanteNombre, reportanteTelefono,
                reportanteRelacion,
                estado != null ? estado : EstadoDesaparicion.BUSCADA,
                prioridad != null ? prioridad : PrioridadDesaparicion.MEDIA,
                null, null, null, ahora, ahora
        );
    }

    /** Regla de negocio: actualiza los datos editables. */
    public void actualizarDatos(String nombre, String apellido, String alias, LocalDate fechaNacimiento,
                                String genero, Integer estaturaCm, Integer pesoKg, String contextura,
                                String colorCabello, String colorOjos, String senasParticulares,
                                String ropaAlDesaparecer, LocalDateTime fechaDesaparicion, Long ultimaUbicacionId,
                                String circunstancias, String reportanteNombre, String reportanteTelefono,
                                String reportanteRelacion, EstadoDesaparicion estado, PrioridadDesaparicion prioridad) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.alias = alias;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.estaturaCm = estaturaCm;
        this.pesoKg = pesoKg;
        this.contextura = contextura;
        this.colorCabello = colorCabello;
        this.colorOjos = colorOjos;
        this.senasParticulares = senasParticulares;
        this.ropaAlDesaparecer = ropaAlDesaparecer;
        this.fechaDesaparicion = fechaDesaparicion;
        this.ultimaUbicacionId = ultimaUbicacionId;
        this.circunstancias = circunstancias;
        this.reportanteNombre = reportanteNombre;
        this.reportanteTelefono = reportanteTelefono;
        this.reportanteRelacion = reportanteRelacion;
        this.estado = estado;
        this.prioridad = prioridad;
        this.actualizadoEn = LocalDateTime.now();
        marcarResolucionSiCorresponde();
    }

    /** Regla de negocio: cambia el estado y registra fecha de resolucion si deja de estar buscada. */
    public void cambiarEstado(EstadoDesaparicion nuevoEstado) {
        this.estado = nuevoEstado;
        this.actualizadoEn = LocalDateTime.now();
        marcarResolucionSiCorresponde();
    }

    private void marcarResolucionSiCorresponde() {
        if (this.estado != EstadoDesaparicion.BUSCADA && this.fechaResolucion == null) {
            this.fechaResolucion = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getAlias() { return alias; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getGenero() { return genero; }
    public Integer getEstaturaCm() { return estaturaCm; }
    public Integer getPesoKg() { return pesoKg; }
    public String getContextura() { return contextura; }
    public String getColorCabello() { return colorCabello; }
    public String getColorOjos() { return colorOjos; }
    public String getSenasParticulares() { return senasParticulares; }
    public String getRopaAlDesaparecer() { return ropaAlDesaparecer; }
    public String getFotoUrl() { return fotoUrl; }
    public LocalDateTime getFechaDesaparicion() { return fechaDesaparicion; }
    public Long getUltimaUbicacionId() { return ultimaUbicacionId; }
    public String getCircunstancias() { return circunstancias; }
    public String getReportanteNombre() { return reportanteNombre; }
    public String getReportanteTelefono() { return reportanteTelefono; }
    public String getReportanteRelacion() { return reportanteRelacion; }
    public EstadoDesaparicion getEstado() { return estado; }
    public PrioridadDesaparicion getPrioridad() { return prioridad; }
    public String getAnalisisIA() { return analisisIA; }
    public String getZonasBusquedaIA() { return zonasBusquedaIA; }
    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}