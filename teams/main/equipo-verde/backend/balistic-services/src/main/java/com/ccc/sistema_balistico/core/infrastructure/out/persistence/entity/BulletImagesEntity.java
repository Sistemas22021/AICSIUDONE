package com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BulletImagesEntity {

    @Id
    private UUID uuidBulletImages;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_bullet")
    private BulletEntity idBullet;

    private String pathImage;

    private String hashImage;

}
