package com.ccc.sistema_balistico.core.infrastructure.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "usuarios_balisticos")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioBalisticoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String rol; // PERITO_BALISTICO, OFICIAL, CONSULTOR, ADMIN

    private Boolean isDelete;
}
