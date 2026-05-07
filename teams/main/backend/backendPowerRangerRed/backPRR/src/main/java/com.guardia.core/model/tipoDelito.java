package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "tipo delito")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class tipoDelito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;
}
