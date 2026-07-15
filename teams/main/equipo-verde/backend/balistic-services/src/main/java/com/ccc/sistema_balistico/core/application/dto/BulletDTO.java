package com.ccc.sistema_balistico.core.application.dto;

import com.ccc.sistema_balistico.core.domain.enums.PercussionType;
import com.ccc.sistema_balistico.core.domain.enums.TwistDirection;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulletDTO {

    private Long idBullet;

    @NotNull(message = "Expedient Required")
    private String caseFile;

    @NotNull(message = "lands And Grooves Required")
    private Long landsAndGrooves;

    @NotNull(message = "Percussion Type Required")
    private PercussionType percussionType;

    @NotNull(message = "Twist Direction Required")
    private TwistDirection twistDirection;

    @NotNull(message = "Caliber Required")
    private Long caliber;

    private List<String> images;

    private String manufacturer;

    private LocalDateTime createdAt;
}
