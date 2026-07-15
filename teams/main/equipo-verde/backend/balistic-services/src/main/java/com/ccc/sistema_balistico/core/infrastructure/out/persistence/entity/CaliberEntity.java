package com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CaliberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCaliber;
    private String name;

    @OneToMany(mappedBy = "caliberEntity")
    private List<BulletEntity> bulletEntities;
    private Boolean isDelete;
}
