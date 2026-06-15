package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "escena_negativa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad que representa un registro de escena negativa (inspección sin hallazgos).
 * Contiene el elemento buscado, área inspeccionada, resultado y observaciones. 
 */
public class EscenaNegativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "elemento_buscado")
    private String elementoBuscado;

    @Column(name = "area_inspeccionada")
    private String areaInspeccionada;

    @Column
    private String resultado;

    @Column
    private String observacion;

    @Column(name = "sin_elementos_negativos", nullable = false)
    private Boolean sinElementosNegativos = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escena_id")
    private Escena escena;

    public void registrarElementoBuscado(String elemento) {
        this.elementoBuscado = elemento;
    }

    public void registrarResultadoNoEncontrado(String area, String observacion) {
        this.areaInspeccionada = area;
        this.observacion = observacion;
        this.resultado = "NO_ENCONTRADO";
    }

    public void agregarObservacion(String obs) {
        if (this.observacion == null) {
            this.observacion = obs;
        } else {
            this.observacion = this.observacion + " | " + obs;
        }
    }

    public boolean validarRegistro() {
        return this.elementoBuscado != null && !this.elementoBuscado.isBlank()
                && this.areaInspeccionada != null && !this.areaInspeccionada.isBlank();
    }

    public void marcarSinElementosNegativos() {
        this.sinElementosNegativos = true;
        this.elementoBuscado = "SIN_ELEMENTOS_NEGATIVOS";
        this.areaInspeccionada = "N/A";
        this.resultado = "SIN_HALLAZGOS";
        this.observacion = "El investigador confirmó que no hay elementos negativos a reportar.";
    }
}
