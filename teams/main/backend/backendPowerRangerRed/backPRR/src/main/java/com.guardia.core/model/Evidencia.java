package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidencia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evidencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_item")
    private String numeroItem;

    @Column(nullable = false)
    private String tipo;

    @Column
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escena_id")
    private Escena escena;

    // Methods
    public Evidencia registrarEvidencia(Escena escena, String tipo, String descripcion) {
        this.escena = escena;
        this.tipo = tipo;
        this.descripcion = descripcion;
        return this;
    }

    public void asignarNumero(String numero) {
        this.numeroItem = numero;
    }

    public void firmarLevantamiento(Usuario investigador) {
        // Lógica de firma/auditoría a implementar (ej: guardar en log de auditoría)
    }

    public boolean validarIntegridad() {
        return this.numeroItem != null && !this.numeroItem.isBlank()
                && this.tipo != null && !this.tipo.isBlank()
                && this.escena != null;
    }

    public void vincularEscena(Escena escena) {
        this.escena = escena;
    }
}
