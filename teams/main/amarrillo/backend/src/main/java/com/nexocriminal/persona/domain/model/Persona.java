package com.nexocriminal.persona.domain.model;

import com.nexocriminal.domain.persona.RolPersona;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Modelo de dominio de Persona. POJO puro, sin JPA ni Spring.
 */
public class Persona {

    private final Long id;
    private String documento;
    private String nombre;
    private String apellido;
    private String alias;
    private LocalDate fechaNacimiento;
    private RolPersona rol;
    private String telefono;
    private String estado;
    private final LocalDateTime creadoEn;
    private LocalDateTime actualizadoEn;

    public Persona(Long id, String documento, String nombre, String apellido, String alias,
                   LocalDate fechaNacimiento, RolPersona rol, String telefono, String estado,
                   LocalDateTime creadoEn, LocalDateTime actualizadoEn) {
        this.id = id;
        this.documento = documento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.alias = alias;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.telefono = telefono;
        this.estado = estado;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    /** Crea una persona nueva (sin id). */
    public static Persona crear(String documento, String nombre, String apellido, String alias,
                                LocalDate fechaNacimiento, RolPersona rol, String telefono) {
        LocalDateTime ahora = LocalDateTime.now();
        return new Persona(
                null, documento, nombre, apellido, alias, fechaNacimiento, rol, telefono,
                "ACTIVO", ahora, ahora
        );
    }

    /** Regla de negocio: actualiza datos editables (el documento no se cambia). */
    public void actualizarDatos(String nombre, String apellido, String alias,
                                LocalDate fechaNacimiento, RolPersona rol, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.alias = alias;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.telefono = telefono;
        this.actualizadoEn = LocalDateTime.now();
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
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
}