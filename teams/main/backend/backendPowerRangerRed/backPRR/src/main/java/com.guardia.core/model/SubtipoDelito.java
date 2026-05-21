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

    // Explicit accessors
    public Long getId() { return this.id; }
    public String getNombre() { return this.nombre; }
    public String getDescripcion() { return this.descripcion; }
    public TipoDelito getTipoDelito() { return this.tipoDelito; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setTipoDelito(TipoDelito tipo) { this.tipoDelito = tipo; }
}
