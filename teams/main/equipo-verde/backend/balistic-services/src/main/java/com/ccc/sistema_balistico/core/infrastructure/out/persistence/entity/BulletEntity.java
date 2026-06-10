package com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity;


import com.ccc.sistema_balistico.core.domain.enums.PercussionType;
import com.ccc.sistema_balistico.core.domain.enums.TwistDirection;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBullet;

    private String caseFile;

    private Long landsAndGrooves;

    @Enumerated(EnumType.STRING)
    private PercussionType percussionType;

    @Enumerated(EnumType.STRING)
    private TwistDirection twistDirection;

    @ManyToOne(fetch = FetchType.LAZY)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @JoinColumn(name = "id_caliber")
    private CaliberEntity caliberEntity;

    private String manufacturer;

    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "idBullet", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<BulletImagesEntity> imagePaths = new ArrayList<>();

    private Boolean isDelete = false ;




}
