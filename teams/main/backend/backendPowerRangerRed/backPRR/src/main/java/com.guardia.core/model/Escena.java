package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "escena")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Escena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estado_checklist")
    private String estadoChecklist;

    @Column(name = "inicio_proceso")
    private LocalDateTime inicioProceso;

    @Column(name = "cierre_proceso")
    private LocalDateTime cierreProceso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "levantada_por_id")
    private Usuario levantadaPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id")
    private Expediente expediente;

    @OneToMany(mappedBy = "escena", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Evidencia> evidencias;

    @OneToMany(mappedBy = "escena", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EscenaNegativa> escenasNegativas;

    // Methods
    public void iniciarChecklist() {
        this.estadoChecklist = "INICIADO";
        this.inicioProceso = LocalDateTime.now();
    }

    public void completarPaso(EscenaNegativa item) {
        if (item != null) {
            this.escenasNegativas.add(item);
            item.setEscena(this);
        }
    }

    public void registrarTimestampPaso() {
        // Puede registrar en log de auditoría o campo adicional según necesidad
    }

    public boolean validarSecuencia() {
        return this.estadoChecklist != null && this.inicioProceso != null;
    }

    public void cerrar() {
        this.estadoChecklist = "CERRADO";
        this.cierreProceso = LocalDateTime.now();
    }

    public void bloquearEdicion() {
        this.estadoChecklist = "BLOQUEADO";
    }

    // Explicit accessors
    public Long getId() { return this.id; }
    public String getEstadoChecklist() { return this.estadoChecklist; }
    public java.time.LocalDateTime getInicioProceso() { return this.inicioProceso; }
    public java.time.LocalDateTime getCierreProceso() { return this.cierreProceso; }
    public Usuario getLevantadaPor() { return this.levantadaPor; }
    public Expediente getExpediente() { return this.expediente; }
    public List<Evidencia> getEvidencias() { return this.evidencias; }
    public List<EscenaNegativa> getEscenasNegativas() { return this.escenasNegativas; }

    public void setExpediente(Expediente expediente) { this.expediente = expediente; }
    public void setLevantadaPor(Usuario levantadaPor) { this.levantadaPor = levantadaPor; }
}
