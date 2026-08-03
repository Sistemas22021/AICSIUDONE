package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "denunciante")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Denunciante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String identificacion; // antes cedula

    @Column(nullable = false)
    private String nombre;

    @Column
    private String direccion;

    @Column
    private String telefono;

    @Column
    private String nacionalidad;

    @Column(name = "relacion_con_hecho")
    private String relacionConHecho; // antes relacionConVictima

    // Explicit accessors for environments where Lombok may not run
    public Long getId() { return this.id; }
    public String getIdentificacion() { return this.identificacion; }
    public String getNombre() { return this.nombre; }
    public String getDireccion() { return this.direccion; }
    public String getTelefono() { return this.telefono; }
    public String getNacionalidad() { return this.nacionalidad; }
    public String getRelacionConHecho() { return this.relacionConHecho; }

    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    public void setRelacionConHecho(String relacion) { this.relacionConHecho = relacion; }

    public void registrarRelacion(String relacion) { this.relacionConHecho = relacion; }


}
