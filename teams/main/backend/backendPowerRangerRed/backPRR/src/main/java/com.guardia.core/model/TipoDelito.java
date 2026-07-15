package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "tipo_delito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Entidad que representa un tipo de delito. Puede contener subtipos.
 * Usada para clasificar delitos en expedientes.
 */
public class TipoDelito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @Column(name = "requiere_subtipo", nullable = false)
    private Boolean requiereSubtipo;

    @OneToMany(mappedBy = "tipoDelito", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SubtipoDelito> subtipos;

    // Methods
    public void agregarSubtipo(SubtipoDelito subtipo) {
        this.subtipos.add(subtipo);
        subtipo.setTipoDelito(this);
    }

    public List<SubtipoDelito> obtenerSubtipos() {
        return this.subtipos;
    }

    public boolean validarSubtipo(SubtipoDelito subtipo) {
        return this.subtipos.contains(subtipo);
    }

    public boolean esSubtipoObligatorio() {
        return Boolean.TRUE.equals(this.requiereSubtipo);
    }
}
