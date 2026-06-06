package com.guardia.core.model;

import com.guardia.core.model.enums.TipoRol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "involucrados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Involucrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String identificacion;

    private String nacionalidad;

    @Column(name = "numero_telefono")
    private String numeroTelefono;

    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id")
    private Expediente expediente;

    @Column(name = "relacion_con_hecho")
    private String relacionConHecho;

}

