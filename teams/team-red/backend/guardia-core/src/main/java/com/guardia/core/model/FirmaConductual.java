package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "firma_conductual")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FirmaConductual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "comportamiento_pre_delictivo", columnDefinition = "TEXT")
    private String comportamientoPreDelictivo;

    @Column(name = "metodo_aproximacion", columnDefinition = "TEXT")
    private String metodoAproximacion;

    @Column(name = "metodo_ataque", columnDefinition = "TEXT")
    private String metodoAtaque;

    @Column(name = "comportamiento_post_delictivo", columnDefinition = "TEXT")
    private String comportamientoPostDelictivo;

    @Column(name = "elementos_distintivos", columnDefinition = "TEXT")
    private String elementosDistintivos;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private Boolean vigente;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analista_id", nullable = false)
    private Usuario analista;

    public boolean tieneContenido() {
        return !(esVacio(comportamientoPreDelictivo)
                && esVacio(metodoAproximacion)
                && esVacio(metodoAtaque)
                && esVacio(comportamientoPostDelictivo)
                && esVacio(elementosDistintivos));
    }

    public void marcarHistorica() {
        this.vigente = false;
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }
}
//Necesario para el push