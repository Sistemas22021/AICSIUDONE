package com.nexocriminal.persona.infrastructure.web.dto;

import com.nexocriminal.domain.persona.RolPersona;
import com.nexocriminal.persona.domain.model.Persona;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** DTO de salida. Mantiene los mismos campos que el frontend ya consume. */
public class PersonaResponse {

    private final Long id;
    private final String documento;
    private final String nombre;
    private final String apellido;
    private final String alias;
    private final LocalDate fechaNacimiento;
    private final RolPersona rol;
    private final String telefono;
    private final String estado;
    private final LocalDateTime creadoEn;

    public PersonaResponse(Persona p) {
        this.id = p.getId();
        this.documento = p.getDocumento();
        this.nombre = p.getNombre();
        this.apellido = p.getApellido();
        this.alias = p.getAlias();
        this.fechaNacimiento = p.getFechaNacimiento();
        this.rol = p.getRol();
        this.telefono = p.getTelefono();
        this.estado = p.getEstado();
        this.creadoEn = p.getCreadoEn();
    }

    public Long getId() { return id; }
    public String getDocumento() { return documento; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getAlias() { return alias; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public RolPersona getRol() { return rol; }
    public String getTelefono() { return telefono; }
    public String getEstado() { return estado; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}