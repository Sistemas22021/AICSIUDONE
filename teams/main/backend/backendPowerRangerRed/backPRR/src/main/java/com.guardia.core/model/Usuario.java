package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String identificacion;

    @Column(nullable = false)
    private String credenciales;

    @Column(nullable = false, unique = true)
    private String correo;

    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<Expediente> expedientesCreados;

    @OneToMany(mappedBy = "selladoPor", fetch = FetchType.LAZY)
    private List<Expediente> expedientesSellados;

    @OneToMany(mappedBy = "levantadaPor", fetch = FetchType.LAZY)
    private List<Escena> escenasLevantadas;

    // Methods
    public boolean autenticar(String credenciales) {
        return this.credenciales != null && this.credenciales.equals(credenciales);
    }

    public boolean verificarAcceso(String permiso) {
        // Lógica de permisos a implementar según roles del sistema
        return permiso != null && !permiso.isBlank();
    }

    public Expediente crearExpediente() {
        Expediente expediente = new Expediente();
        expediente.setCreadoPor(this);
        return expediente;
    }

    public Expediente consultarExpediente(String folio) {
        return expedientesCreados.stream()
                .filter(e -> folio.equals(e.getFolio()))
                .findFirst()
                .orElse(null);
    }

    public void registrarActividad(String accion) {
        // Lógica de auditoría a implementar
    }

    // Explicit accessors to avoid Lombok issues
    public Long getId() { return this.id; }
    public String getNombre() { return this.nombre; }
    public String getIdentificacion() { return this.identificacion; }
    public String getCredenciales() { return this.credenciales; }
    public String getCorreo() { return this.correo; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
    public void setCredenciales(String credenciales) { this.credenciales = credenciales; }
    public void setCorreo(String correo) { this.correo = correo; }

}
