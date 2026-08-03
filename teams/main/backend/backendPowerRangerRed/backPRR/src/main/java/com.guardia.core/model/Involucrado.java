package com.guardia.core.model;

import com.guardia.core.model.enums.TipoRol;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "expediente_involucrados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Involucrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String apellido;

    @Column(nullable = false)
    private String identificacion;

    private String telefono;
    private String nacionalidad;
    private String direccion;
    private String fotografiaURL;

    // Relación ManyToOne hacia Expediente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    // Tabla auxiliar solo para los roles (Enums) de este involucrado
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "involucrado_roles",
            joinColumns = @JoinColumn(name = "involucrado_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "rol")
    private List<TipoRol> roles;
    
}