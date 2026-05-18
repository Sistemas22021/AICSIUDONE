package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;

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

    @Column(name = "relacion_con_hecho")
    private String relacionConHecho;

    // Methods
    public void actualizarDatos(Map<String, Object> nuevosDatos) {
        if (nuevosDatos.containsKey("telefono"))
            this.telefono = (String) nuevosDatos.get("telefono");
        if (nuevosDatos.containsKey("direccion"))
            this.direccion = (String) nuevosDatos.get("direccion");
        if (nuevosDatos.containsKey("relacionConHecho"))
            this.relacionConHecho = (String) nuevosDatos.get("relacionConHecho");
    }

    public boolean validarDatosObligatorios() {
        return this.nombre != null && !this.nombre.isBlank()
                && this.identificacion != null && !this.identificacion.isBlank();
    }

    public void registrarRelacion(String relacion) {
        this.relacionConHecho = relacion;
    }
}
