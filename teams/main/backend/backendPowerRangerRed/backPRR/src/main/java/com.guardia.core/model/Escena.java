package com.guardia.core.model;

import com.guardia.core.model.enums.PasoChecklist;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Comparator;

@Entity
@Table(name = "escena")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad que representa una escena de levantamiento asociada a un expediente.
 * Gestiona checklist, evidencias, timestamps y estado del proceso.
 */
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

    @Enumerated(EnumType.STRING)
    @Column(name = "paso_actual")
    private PasoChecklist pasoActual;

    @OneToMany(mappedBy = "escena", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EscenaNegativa> escenasNegativas;

    @OneToMany(mappedBy = "escena", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EscenaChecklist> checklist = new ArrayList<>();

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

    public void registrarTimestampPaso(EscenaChecklist paso, boolean esCierre) {
        if (paso == null) return;
        if (esCierre) {
            paso.setFechaCierre(LocalDateTime.now());
        } else {
            paso.setFechaInicio(LocalDateTime.now());
        }
    }

    public boolean validarSecuencia() {
        if (checklist == null || checklist.isEmpty()) return false;
        List<EscenaChecklist> ordenados = checklist.stream()
                .sorted(Comparator.comparingInt(EscenaChecklist::getOrden))
                .toList();
        for (int i = 1; i < ordenados.size(); i++) {
            if (Boolean.TRUE.equals(ordenados.get(i).getCompletado()) &&
                    !Boolean.TRUE.equals(ordenados.get(i - 1).getCompletado())) {
                return false; // hay un paso completado con anterior incompleto
            }
        }
        return true;
    }

    public void cerrar() {
        this.estadoChecklist = "CERRADO";
        this.cierreProceso = LocalDateTime.now();
    }

    public void bloquearEdicion() {
        this.estadoChecklist = "BLOQUEADO";
    }
}
