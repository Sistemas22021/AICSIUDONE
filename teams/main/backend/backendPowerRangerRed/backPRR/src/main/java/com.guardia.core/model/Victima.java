package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.List;

@Entity
@Table(name = "victima")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Victima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String identificacion;

    @Column
    private String telefono;

    @Column
    private String nacionalidad;

    @Column
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id")
    private Expediente expediente;

    // Methods
    public void actualizarDatos(Map<String, Object> nuevosDatos) {
        if (nuevosDatos.containsKey("telefono"))
            this.telefono = (String) nuevosDatos.get("telefono");
        if (nuevosDatos.containsKey("direccion"))
            this.direccion = (String) nuevosDatos.get("direccion");
        if (nuevosDatos.containsKey("nombre"))
            this.nombre = (String) nuevosDatos.get("nombre");
    }

    public boolean validarIdentificacion() {
        return this.identificacion != null && !this.identificacion.isBlank();
    }

    // Explicit accessors for environments where Lombok annotation processing may not run
    public Long getId() { return this.id; }
    public String getNombre() { return this.nombre; }
    public String getIdentificacion() { return this.identificacion; }
    public String getTelefono() { return this.telefono; }
    public String getNacionalidad() { return this.nacionalidad; }
    public String getDireccion() { return this.direccion; }
    public Expediente getExpediente() { return this.expediente; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setNacionalidad(String nacionalidad) { this.nacionalidad = nacionalidad; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setExpediente(Expediente expediente) { this.expediente = expediente; }

}
