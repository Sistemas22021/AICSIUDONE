package com.guardia.core.model;

import com.guardia.core.model.enums.EstadoExpediente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expediente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String folio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoExpediente estadoExpediente;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_sellado")
    private LocalDateTime fechaSellado;

    @Column(name = "descripcion_hecho")
    private String descripcionHecho;

    @Column(name = "fecha_hecho")
    private LocalDateTime fechaHecho;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id")
    private Usuario creadoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sellado_por_id")
    private Usuario selladoPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_delito_id")
    private TipoDelito tipoDelito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtipo_delito_id")
    private SubtipoDelito subtipoDelito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "denunciante_id")
    private Denunciante denunciante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localizacion_id")
    private Localizacion localizacion;

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Escena> escenas;

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Victima> victimas;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "expediente_modus_operandi",
            joinColumns = @JoinColumn(name = "expediente_id"),
            inverseJoinColumns = @JoinColumn(name = "modus_operandi_id")
    )
    private List<ModusOperandi> modusOperandiList;

    // Methods
    public String generarFolio() {
        this.folio = "EXP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return this.folio;
    }

    public boolean validarDatos() {
        return this.descripcionHecho != null && !this.descripcionHecho.isBlank()
                && this.tipoDelito != null
                && this.localizacion != null;
    }

    public void sellarUsuario(Usuario agente) {
        this.selladoPor = agente;
        this.fechaSellado = LocalDateTime.now();
        this.estadoExpediente = EstadoExpediente.PROCESADO_Y_SELLADO;
    }

    public void cambiarEstado(EstadoExpediente nuevoEstado) {
        this.estadoExpediente = nuevoEstado;
    }

    public void asignarInvestigador(Usuario investigador) {
        // Lógica de asignación a implementar según modelo de roles
    }

    public void vincularEscena(Escena escena) {
        this.escenas.add(escena);
        escena.setExpediente(this);
    }

    public void asignarFechaHecho(LocalDateTime fecha) {
        this.fechaHecho = fecha;
    }

    @PrePersist
    public void prePersist() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.estadoExpediente == null) {
            this.estadoExpediente = EstadoExpediente.BORRADOR;
        }
        if (this.folio == null || this.folio.isBlank()) {
            generarFolio();
        }
    }
}
