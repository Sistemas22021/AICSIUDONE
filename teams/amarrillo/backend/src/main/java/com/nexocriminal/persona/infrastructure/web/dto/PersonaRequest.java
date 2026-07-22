package com.nexocriminal.persona.infrastructure.web.dto;

import com.nexocriminal.domain.persona.RolPersona;
import com.nexocriminal.validacion.anotaciones.CedulaVenezolana;
import com.nexocriminal.validacion.anotaciones.TelefonoVenezolano;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/** DTO de entrada para crear/actualizar persona. */
public class PersonaRequest {

    @NotBlank
    @CedulaVenezolana
    private String documento;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    private String alias;
    private LocalDate fechaNacimiento;
    private RolPersona rol;

    @TelefonoVenezolano
    private String telefono;

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

    public RolPersona getRol() { return rol; }
    public void setRol(RolPersona rol) { this.rol = rol; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}