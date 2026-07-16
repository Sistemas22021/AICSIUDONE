package com.nexocriminal.modus;

import jakarta.persistence.*;
import lombok.*;

/**
 * Catálogo de modus operandi. Tabla con varias filas (una por categoría).
 * El 'codigo' es lo que se guarda en el suceso y lo que el motor matchea
 * (estable, mayúsculas, sin espacios). La 'etiqueta' es lo que ve el usuario.
 */
@Entity
@Table(name = "modus_operandi")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ModusOperandi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código estable para el motor, ej: ENGANO_VICTIMA. Único. */
    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    /** Etiqueta legible para el usuario, ej: "Engaño a la víctima". */
    @Column(nullable = false, length = 120)
    private String etiqueta;

    /** Descripción opcional de la categoría. */
    @Column(length = 400)
    private String descripcion;

    /** Si está activo (aparece en el dropdown). Permite ocultar sin borrar. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;
}