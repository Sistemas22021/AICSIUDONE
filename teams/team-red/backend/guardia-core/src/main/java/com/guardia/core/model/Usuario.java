package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad que representa un usuario del sistema (investigador/agente).
 * Mapea la tabla compartida `users`, la misma que usa el login SSO
 * (main/backend/auth-service). El id es UUID, no autoincremental.
 */
public class Usuario {

    @Id
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "profile_photo_url")
    private String profilePhotoUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @OneToMany(mappedBy = "creadoPor", fetch = FetchType.LAZY)
    private List<Expediente> expedientesCreados;

    @OneToMany(mappedBy = "selladoPor", fetch = FetchType.LAZY)
    private List<Expediente> expedientesSellados;

    @OneToMany(mappedBy = "levantadaPor", fetch = FetchType.LAZY)
    private List<Escena> escenasLevantadas;

    public boolean verificarAcceso(String permiso) {
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
}