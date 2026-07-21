package com.guardia.core.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import com.guardia.core.model.enums.EstadoExpediente;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "expedientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad principal que representa un expediente de investigación.
 * Agrupa folio, estado, escenas, involucrados, delitos y metadatos de integridad.
 */
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

    @Column(name = "hash_integridad", length = 64)
    private String hashIntegridad;

    @Column(name = "agente_sellador_info", length = 500)
    private String agenteSelladorInfo;

    @Column(columnDefinition = "TEXT")
    private String descripcionHecho;

    @Column(name = "embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 768)
    private float[] embedding;

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
    @JoinColumn(name = "caso_id")
    private Caso caso;

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Escena> escenas = new ArrayList<>();

    @OneToMany(mappedBy = "expediente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Involucrado> involucrados = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "expediente_modus_operandi", joinColumns = @JoinColumn(name = "expediente_id"), inverseJoinColumns = @JoinColumn(name = "modus_operandi_id"))
    @Builder.Default
    private List<ModusOperandi> modusOperandiList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoExpediente estadoExpediente = EstadoExpediente.BORRADOR;

    private Boolean esDenunciaFormal;

    private String municipio;
    private String sector;
    private String referencia;

    @ElementCollection
    @CollectionTable(name = "expediente_delitos", joinColumns = @JoinColumn(name = "expediente_id"))
    @Builder.Default
    private List<DelitoEnExpediente> delitos = new ArrayList<>();

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

    public void agregarInvolucrado(Involucrado involucrado) {
        involucrados.add(involucrado);
        involucrado.setExpediente(this);
    }


    public void asignarFechaHecho(LocalDateTime fecha) {
        this.fechaHecho = fecha;
    }

}