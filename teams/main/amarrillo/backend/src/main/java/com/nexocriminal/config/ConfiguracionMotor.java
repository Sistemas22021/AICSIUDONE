package com.nexocriminal.config;

import jakarta.persistence.*;
import lombok.*;

/**
 * Configuracion de umbrales del motor Red Thread. Tabla de una sola fila (id=1).
 * Los valores DEFAULT_* son los fundamentales: el boton "restaurar defaults"
 * reescribe la fila con estos valores.
 */
@Entity
@Table(name = "configuracion_motor")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ConfiguracionMotor {

    // ===== Valores fundamentales (defaults para el boton restaurar) =====
    public static final int DEF_NODO_RADIO_METROS = 500;
    public static final int DEF_NODO_VENTANA_HORAS = 72;
    public static final int DEF_NODO_MIN_VEHICULOS = 3;
    public static final int DEF_ESCOLTA_VENTANA_MINUTOS = 2;
    public static final int DEF_ESCOLTA_MIN_COINCIDENCIAS = 3;
    public static final double DEF_MODUS_UMBRAL = 0.75;
    public static final int DEF_CLUSTER_RADIO_METROS = 1500;
    public static final int DEF_CLUSTER_MIN = 3;
    public static final int DEF_CLUSTER_VENTANA_DIAS = 30;
    public static final int DEF_CLUSTER_RADIO_NODO_SOSPECHOSO = 1000;

    @Id
    private Long id;  // siempre 1 (fila unica)

    @Column(nullable = false) private Integer nodoRadioMetros;
    @Column(nullable = false) private Integer nodoVentanaHoras;
    @Column(nullable = false) private Integer nodoMinVehiculos;
    @Column(nullable = false) private Integer escoltaVentanaMinutos;
    @Column(nullable = false) private Integer escoltaMinCoincidencias;
    @Column(nullable = false) private Double modusUmbral;
    @Column(nullable = false) private Integer clusterRadioMetros;
    @Column(nullable = false) private Integer clusterMin;
    @Column(nullable = false) private Integer clusterVentanaDias;
    @Column(nullable = false) private Integer clusterRadioNodoSospechoso;

    /** Construye una config con todos los valores fundamentales. */
    public static ConfiguracionMotor defaults() {
        return ConfiguracionMotor.builder()
                .id(1L)
                .nodoRadioMetros(DEF_NODO_RADIO_METROS)
                .nodoVentanaHoras(DEF_NODO_VENTANA_HORAS)
                .nodoMinVehiculos(DEF_NODO_MIN_VEHICULOS)
                .escoltaVentanaMinutos(DEF_ESCOLTA_VENTANA_MINUTOS)
                .escoltaMinCoincidencias(DEF_ESCOLTA_MIN_COINCIDENCIAS)
                .modusUmbral(DEF_MODUS_UMBRAL)
                .clusterRadioMetros(DEF_CLUSTER_RADIO_METROS)
                .clusterMin(DEF_CLUSTER_MIN)
                .clusterVentanaDias(DEF_CLUSTER_VENTANA_DIAS)
                .clusterRadioNodoSospechoso(DEF_CLUSTER_RADIO_NODO_SOSPECHOSO)
                .build();
    }

    /** Copia los valores editables desde otra config (sin tocar el id). */
    public void copiarValoresDe(ConfiguracionMotor o) {
        this.nodoRadioMetros = o.nodoRadioMetros;
        this.nodoVentanaHoras = o.nodoVentanaHoras;
        this.nodoMinVehiculos = o.nodoMinVehiculos;
        this.escoltaVentanaMinutos = o.escoltaVentanaMinutos;
        this.escoltaMinCoincidencias = o.escoltaMinCoincidencias;
        this.modusUmbral = o.modusUmbral;
        this.clusterRadioMetros = o.clusterRadioMetros;
        this.clusterMin = o.clusterMin;
        this.clusterVentanaDias = o.clusterVentanaDias;
        this.clusterRadioNodoSospechoso = o.clusterRadioNodoSospechoso;
    }
}