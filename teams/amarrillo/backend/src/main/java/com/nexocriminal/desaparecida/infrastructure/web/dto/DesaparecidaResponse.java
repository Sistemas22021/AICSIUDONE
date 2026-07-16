package com.nexocriminal.desaparecida.infrastructure.web.dto;

import com.nexocriminal.desaparecida.domain.model.Desaparecida;
import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.FotoDesaparecida;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida para desaparecida. Expone los campos del dominio + las fotos
 * (de la entidad JPA) + la ultima ubicacion anidada, manteniendo lo que el front consume.
 */
public class DesaparecidaResponse {

    public static class UbicacionNodo {
        private final Long id;
        private final String direccion;
        private final double latitud;
        private final double longitud;
        public UbicacionNodo(Long id, String direccion, double latitud, double longitud) {
            this.id = id; this.direccion = direccion; this.latitud = latitud; this.longitud = longitud;
        }
        public Long getId() { return id; }
        public String getDireccion() { return direccion; }
        public double getLatitud() { return latitud; }
        public double getLongitud() { return longitud; }
    }

    private final Long id;
    private final String documento;
    private final String nombre;
    private final String apellido;
    private final String alias;
    private final LocalDate fechaNacimiento;
    private final String genero;
    private final Integer estaturaCm;
    private final Integer pesoKg;
    private final String contextura;
    private final String colorCabello;
    private final String colorOjos;
    private final String señasParticulares;
    private final String ropaAlDesaparecer;
    private final String fotoUrl;
    private final List<FotoDesaparecida> fotos;
    private final LocalDateTime fechaDesaparicion;
    private final UbicacionNodo ultimaUbicacion;
    private final String circunstancias;
    private final String reportanteNombre;
    private final String reportanteTelefono;
    private final String reportanteRelacion;
    private final EstadoDesaparicion estado;
    private final PrioridadDesaparicion prioridad;
    private final String analisisIA;
    private final String zonasBusquedaIA;
    private final LocalDateTime fechaResolucion;
    private final LocalDateTime creadoEn;
    private final LocalDateTime actualizadoEn;

    public DesaparecidaResponse(Desaparecida d, List<FotoDesaparecida> fotos, UbicacionNodo ultimaUbicacion) {
        this.id = d.getId();
        this.documento = d.getDocumento();
        this.nombre = d.getNombre();
        this.apellido = d.getApellido();
        this.alias = d.getAlias();
        this.fechaNacimiento = d.getFechaNacimiento();
        this.genero = d.getGenero();
        this.estaturaCm = d.getEstaturaCm();
        this.pesoKg = d.getPesoKg();
        this.contextura = d.getContextura();
        this.colorCabello = d.getColorCabello();
        this.colorOjos = d.getColorOjos();
        this.señasParticulares = d.getSenasParticulares();
        this.ropaAlDesaparecer = d.getRopaAlDesaparecer();
        this.fotoUrl = d.getFotoUrl();
        this.fotos = fotos;
        this.fechaDesaparicion = d.getFechaDesaparicion();
        this.ultimaUbicacion = ultimaUbicacion;
        this.circunstancias = d.getCircunstancias();
        this.reportanteNombre = d.getReportanteNombre();
        this.reportanteTelefono = d.getReportanteTelefono();
        this.reportanteRelacion = d.getReportanteRelacion();
        this.estado = d.getEstado();
        this.prioridad = d.getPrioridad();
        this.analisisIA = d.getAnalisisIA();
        this.zonasBusquedaIA = d.getZonasBusquedaIA();
        this.fechaResolucion = d.getFechaResolucion();
        this.creadoEn = d.getCreadoEn();
        this.actualizadoEn = d.getActualizadoEn();
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
    public String getSeñasParticulares() { return señasParticulares; }
    public String getRopaAlDesaparecer() { return ropaAlDesaparecer; }
    public String getFotoUrl() { return fotoUrl; }
    public List<FotoDesaparecida> getFotos() { return fotos; }
    public LocalDateTime getFechaDesaparicion() { return fechaDesaparicion; }
    public UbicacionNodo getUltimaUbicacion() { return ultimaUbicacion; }
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