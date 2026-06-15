package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "modus_operandi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad que representa un modus operandi (patrón de actuación).
 * Puede relacionarse con múltiples expedientes para análisis de patrones.
 */
public class ModusOperandi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descripcion_analitica")
    private String descripcionAnalitica;

    @Column(name = "patron_detectado")
    private String patronDetectado;

    @Column(name = "nivel_confianza")
    private String nivelConfianza;

    @ManyToMany(mappedBy = "modusOperandiList", fetch = FetchType.LAZY)
    private List<Expediente> expedientes = new ArrayList<>();

    // Methods
    public void analizarPatrones(List<Expediente> expedientes) {
        this.expedientes = expedientes;
        // Lógica de análisis de patrones a implementar
    }

    public double compararExpedientes(Expediente a, Expediente b) {
        // Lógica de comparación de expedientes a implementar
        return 0.0;
    }

    public void calcularNivelConfianza() {
        // Lógica de cálculo de confianza a implementar
    }

    public void generarAlerta(String criterio) {
        // Lógica de generación de alertas a implementar
    }

    public void agregarPatron(String patron) {
        this.patronDetectado = patron;
    }
}
