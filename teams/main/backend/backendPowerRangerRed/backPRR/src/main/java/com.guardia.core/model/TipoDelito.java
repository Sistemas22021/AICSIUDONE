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

    // Explicit accessors
    public Long getId() { return this.id; }
    public String getNombre() { return this.nombre; }
    public String getDescripcion() { return this.descripcion; }
    public Boolean getRequiereSubtipo() { return this.requiereSubtipo; }
    public List<SubtipoDelito> getSubtipos() { return this.subtipos; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setRequiereSubtipo(Boolean requiereSubtipo) { this.requiereSubtipo = requiereSubtipo; }
    public void setSubtipos(List<SubtipoDelito> subtipos) { this.subtipos = subtipos; }
}
