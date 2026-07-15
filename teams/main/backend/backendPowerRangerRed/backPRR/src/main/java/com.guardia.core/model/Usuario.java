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
/**
 * Entidad que representa un usuario del sistema (investigador/agente).
 * Incluye credenciales, correo y relaciones con expedientes y escenas.
 */
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "analista")
    private List<FirmaConductual> firmasRegistradas; //Para el push

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
}
