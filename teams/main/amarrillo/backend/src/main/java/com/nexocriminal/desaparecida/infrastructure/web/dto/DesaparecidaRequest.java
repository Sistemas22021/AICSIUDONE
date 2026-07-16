package com.nexocriminal.desaparecida.infrastructure.web.dto;

import com.nexocriminal.domain.desaparecida.EstadoDesaparicion;
import com.nexocriminal.domain.desaparecida.PrioridadDesaparicion;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de entrada para crear/actualizar desaparecida. La ultima ubicacion llega
 * como objeto anidado {"id": X} (como manda el frontend) o null.
 */
public class DesaparecidaRequest {

    public static class RefId {
        private Long id;
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

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
    private LocalDateTime fechaDesaparicion;
    private RefId ultimaUbicacion;
    private String circunstancias;
    private String reportanteNombre;
    private String reportanteTelefono;
    private String reportanteRelacion;
    private EstadoDesaparicion estado;
    private PrioridadDesaparicion prioridad;

    public Long ultimaUbicacionId() { return ultimaUbicacion != null ? ultimaUbicacion.getId() : null; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
    public Integer getEstaturaCm() { return estaturaCm; }
    public void setEstaturaCm(Integer estaturaCm) { this.estaturaCm = estaturaCm; }
    public Integer getPesoKg() { return pesoKg; }
    public void setPesoKg(Integer pesoKg) { this.pesoKg = pesoKg; }
    public String getContextura() { return contextura; }
    public void setContextura(String contextura) { this.contextura = contextura; }
    public String getColorCabello() { return colorCabello; }
    public void setColorCabello(String colorCabello) { this.colorCabello = colorCabello; }
    public String getColorOjos() { return colorOjos; }
    public void setColorOjos(String colorOjos) { this.colorOjos = colorOjos; }
    public String getSenasParticulares() { return senasParticulares; }
    public void setSenasParticulares(String senasParticulares) { this.senasParticulares = senasParticulares; }
    public String getRopaAlDesaparecer() { return ropaAlDesaparecer; }
    public void setRopaAlDesaparecer(String ropaAlDesaparecer) { this.ropaAlDesaparecer = ropaAlDesaparecer; }
    public LocalDateTime getFechaDesaparicion() { return fechaDesaparicion; }
    public void setFechaDesaparicion(LocalDateTime fechaDesaparicion) { this.fechaDesaparicion = fechaDesaparicion; }
    public RefId getUltimaUbicacion() { return ultimaUbicacion; }
    public void setUltimaUbicacion(RefId ultimaUbicacion) { this.ultimaUbicacion = ultimaUbicacion; }
    public String getCircunstancias() { return circunstancias; }
    public void setCircunstancias(String circunstancias) { this.circunstancias = circunstancias; }
    public String getReportanteNombre() { return reportanteNombre; }
    public void setReportanteNombre(String reportanteNombre) { this.reportanteNombre = reportanteNombre; }
    public String getReportanteTelefono() { return reportanteTelefono; }
    public void setReportanteTelefono(String reportanteTelefono) { this.reportanteTelefono = reportanteTelefono; }
    public String getReportanteRelacion() { return reportanteRelacion; }
    public void setReportanteRelacion(String reportanteRelacion) { this.reportanteRelacion = reportanteRelacion; }
    public EstadoDesaparicion getEstado() { return estado; }
    public void setEstado(EstadoDesaparicion estado) { this.estado = estado; }
    public PrioridadDesaparicion getPrioridad() { return prioridad; }
    public void setPrioridad(PrioridadDesaparicion prioridad) { this.prioridad = prioridad; }
}