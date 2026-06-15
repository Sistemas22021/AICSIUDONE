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
/**
 * Entidad que representa un denunciante (persona que realiza la denuncia).
 * Almacena identificación, contacto y relación con el hecho.
 */
public class Denunciante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identificación única del denunciante (p. ej. cédula o pasaporte).
     * Valor único en la tabla y usado para búsquedas por identificación.
     */
    @Column(nullable = false, unique = true)
    private String identificacion; // antes cedula

    /**
     * Nombre completo del denunciante.
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Dirección postal o física del denunciante.
     */
    @Column
    private String direccion;

    /**
     * Número telefónico de contacto del denunciante.
     */
    @Column
    private String telefono;

    /**
     * Nacionalidad del denunciante (por compatibilidad con el frontend).
     */
    @Column
    private String nacionalidad;

    /**
     * Descripción de la relación del denunciante con el hecho (p. ej. "testigo", "víctima").
     */
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

    /**
     * Registra/actualiza la relación del denunciante con el hecho.
     * @param relacion cadena descriptiva de la relación (por ejemplo "testigo" o "víctima").
     */
    public void registrarRelacion(String relacion) { this.relacionConHecho = relacion; }


}
