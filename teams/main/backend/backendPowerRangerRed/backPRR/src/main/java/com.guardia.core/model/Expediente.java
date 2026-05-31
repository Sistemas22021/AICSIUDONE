package com.guardia.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.*;
import com.guardia.core.model.enums.EstadoExpediente;

@Entity
@Table(name = "expedientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expediente {

    // Explicit getters/setters to avoid Lombok dependency issues in some build environments
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Folio visible al usuario
    @Column(unique = true)
    private String folio;

    // Campo legacy por compatibilidad
    @Column(name = "numero_unico")
    private String numeroUnico;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaSellado;

    private String descripcionHecho;

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
    @JoinColumn(name = "localizacion_id")
    private Localizacion localizacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "denunciante_id")
    private Denunciante denunciante;

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Escena> escenas = new ArrayList<>();


    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Involucrado> involucrados = new ArrayList<>();

    // Método helper fundamental para asegurar la bidireccionalidad
    public void vincularInvolucrado(Involucrado involucrado) {
        if (involucrado != null) {
            this.involucrados.add(involucrado);
            involucrado.setExpediente(this); // IMPORTANTE: Le asigna este expediente al involucrado
        }
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "expediente_modus_operandi",
            joinColumns = @JoinColumn(name = "expediente_id"),
            inverseJoinColumns = @JoinColumn(name = "modus_operandi_id")
    )
    private List<ModusOperandi> modusOperandiList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private EstadoExpediente estadoExpediente = EstadoExpediente.BORRADOR;

    private Boolean esDenunciaFormal;

    // Legacy location fields (kept for backward compatibility)
    private String municipio;
    private String sector;
    private String referencia;

    // Delitos embebidos (legacy)
    @ElementCollection
    @CollectionTable(name = "expediente_delitos", joinColumns = @JoinColumn(name = "expediente_id"))
    private List<DelitoEnExpediente> delitos = new ArrayList<>();

    // Methods demanded by service implementation
    public boolean validarDatos() {
        // Validación simple: descripción, tipo delito y localización o municipio
        if (this.descripcionHecho == null || this.descripcionHecho.isBlank()) return false;
        if (this.tipoDelito == null) return false;
        if (this.localizacion == null && (this.municipio == null || this.municipio.isBlank())) return false;
        return true;
    }

    public void cambiarEstado(EstadoExpediente nuevoEstado) {
        this.estadoExpediente = nuevoEstado;
    }

    public void vincularEscena(Escena escena) {
        if (escena != null) {
            this.escenas.add(escena);
            escena.setExpediente(this);
        }
    }

    public void sellarUsuario(Usuario agente) {
        this.selladoPor = agente;
        this.fechaSellado = LocalDateTime.now();
        this.estadoExpediente = EstadoExpediente.PROCESADO_Y_SELLADO;
    }

    public void asignarFechaHecho(LocalDateTime fecha) {
        this.fechaHecho = fecha;
    }

    public String getNumeroUnico() { return this.numeroUnico; }

    public void setFechaSellado(LocalDateTime fechaSellado) { this.fechaSellado = fechaSellado; }

    public void setSelladoPor(Usuario selladoPor) { this.selladoPor = selladoPor; }

    public void setTipoDelito(TipoDelito tipoDelito) { this.tipoDelito = tipoDelito; }

    public void setSubtipoDelito(SubtipoDelito subtipoDelito) { this.subtipoDelito = subtipoDelito; }

    public Boolean getEsDenunciaFormal() { return this.esDenunciaFormal; }
    public void setEsDenunciaFormal(Boolean esDenunciaFormal) { this.esDenunciaFormal = esDenunciaFormal; }

    public void setDelitos(List<DelitoEnExpediente> delitos) { this.delitos = delitos; }

    public String getMunicipio() { return this.municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

}
