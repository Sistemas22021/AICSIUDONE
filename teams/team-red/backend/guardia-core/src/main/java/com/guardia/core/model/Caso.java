package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "casos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Caso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_caso", nullable = false, unique = true, length = 40)
    private String codigoCaso;

    @Column(name = "motivo", nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creado_por_id", nullable = false)
    private Usuario creadoPor;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alerta_origen_id")
    private AlertaPatron alertaOrigen;*/

    @OneToMany(mappedBy = "caso", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Expediente> expedientes = new ArrayList<>();

    public int cantidadExpedientes() {
        return expedientes == null ? 0 : expedientes.size();
    }
}