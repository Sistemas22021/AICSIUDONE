package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subtipo_delito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad que representa un subtipo de delito y su asociación con el tipo padre.
 */
public class SubtipoDelito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_delito_id", nullable = false)
    private TipoDelito tipoDelito;

    // Methods
    public boolean validarCorrespondenciaConTipo(TipoDelito tipo) {
        return this.tipoDelito != null && this.tipoDelito.equals(tipo);
    }
}
