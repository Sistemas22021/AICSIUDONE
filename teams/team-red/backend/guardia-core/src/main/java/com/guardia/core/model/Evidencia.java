package com.guardia.core.model;
import com.guardia.core.HashStrategy;

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
/**
 * Entidad que representa una evidencia forense vinculada a una escena.
 * Maneja hash de integridad, timestamp y metadatos del investigador.
 */
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

    @Column(name = "hash_integridad")
    private String hashIntegridad;

    @Column(name = "timestamp_registro")
    private LocalDateTime timestampRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investigador_id")
    private Usuario investigador;

    // Methods
    public Evidencia registrarEvidencia(Escena escena, String tipo, String descripcion, Usuario investigador, HashStrategy hashStrategy, String hashArchivoCliente) {
        this.escena = escena;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.investigador = investigador;
        this.timestampRegistro = LocalDateTime.now();
        if (hashArchivoCliente != null && !hashArchivoCliente.isBlank()) {
            this.hashIntegridad = hashArchivoCliente;
        } else {
            String contenido = tipo + "|" + descripcion;
            this.hashIntegridad = hashStrategy.calcular(contenido);
        }
        return this;
    }

    public boolean verificarHash(HashStrategy hashStrategy) {
        if (this.hashIntegridad == null) return false;
        String contenido = this.tipo + "|" + this.descripcion;
        return this.hashIntegridad.equals(hashStrategy.calcular(contenido));
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
