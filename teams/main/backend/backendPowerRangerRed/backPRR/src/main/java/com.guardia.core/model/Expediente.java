package com.guardia.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Victima> victimas = new ArrayList<>();

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    // Explicit getters/setters to avoid Lombok dependency issues in some build environments
    public Long getId() { return this.id; }

    public String getFolio() { return this.folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public String getNumeroUnico() { return this.numeroUnico; }
    public void setNumeroUnico(String numeroUnico) { this.numeroUnico = numeroUnico; }

    public LocalDateTime getFechaCreacion() { return this.fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaSellado() { return this.fechaSellado; }
    public void setFechaSellado(LocalDateTime fechaSellado) { this.fechaSellado = fechaSellado; }

    public String getDescripcionHecho() { return this.descripcionHecho; }
    public void setDescripcionHecho(String descripcionHecho) { this.descripcionHecho = descripcionHecho; }

    public LocalDateTime getFechaHecho() { return this.fechaHecho; }
    public void setFechaHecho(LocalDateTime fechaHecho) { this.fechaHecho = fechaHecho; }

    public Usuario getCreadoPor() { return this.creadoPor; }
    public void setCreadoPor(Usuario creadoPor) { this.creadoPor = creadoPor; }

    public Usuario getSelladoPor() { return this.selladoPor; }
    public void setSelladoPor(Usuario selladoPor) { this.selladoPor = selladoPor; }

    public TipoDelito getTipoDelito() { return this.tipoDelito; }
    public void setTipoDelito(TipoDelito tipoDelito) { this.tipoDelito = tipoDelito; }

    public SubtipoDelito getSubtipoDelito() { return this.subtipoDelito; }
    public void setSubtipoDelito(SubtipoDelito subtipoDelito) { this.subtipoDelito = subtipoDelito; }

    public Localizacion getLocalizacion() { return this.localizacion; }
    public void setLocalizacion(Localizacion localizacion) { this.localizacion = localizacion; }

    public Denunciante getDenunciante() { return this.denunciante; }
    public void setDenunciante(Denunciante denunciante) { this.denunciante = denunciante; }

    public List<Escena> getEscenas() { return this.escenas; }
    public void setEscenas(List<Escena> escenas) { this.escenas = escenas; }

    public List<Victima> getVictimas() { return this.victimas; }
    public void setVictimas(List<Victima> victimas) { this.victimas = victimas; }

    public List<ModusOperandi> getModusOperandiList() { return this.modusOperandiList; }
    public void setModusOperandiList(List<ModusOperandi> modusOperandiList) { this.modusOperandiList = modusOperandiList; }

    public EstadoExpediente getEstadoExpediente() { return this.estadoExpediente; }
    public void setEstadoExpediente(EstadoExpediente estadoExpediente) { this.estadoExpediente = estadoExpediente; }

    public Boolean getEsDenunciaFormal() { return this.esDenunciaFormal; }
    public void setEsDenunciaFormal(Boolean esDenunciaFormal) { this.esDenunciaFormal = esDenunciaFormal; }

    public List<DelitoEnExpediente> getDelitos() { return this.delitos; }
    public void setDelitos(List<DelitoEnExpediente> delitos) { this.delitos = delitos; }

    public String getMunicipio() { return this.municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

}
