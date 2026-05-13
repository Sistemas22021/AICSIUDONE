package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "delito")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TipoDelito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;
}
