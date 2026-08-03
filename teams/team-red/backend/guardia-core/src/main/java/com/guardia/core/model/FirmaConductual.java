package com.guardia.core.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "firmas_conductuales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
/**
 * Firma conductual de un expediente: patrones de comportamiento del autor
 * antes, durante y después del hecho, independientes del Modus Operandi
 * (HU "Registrar firma conductual del caso").
 *
 * <p>Versionada igual que {@link PropuestaModusOperandi} (version/vigente):
 * cada registro o edición crea una fila nueva y marca la anterior como no
 * vigente, para conservar historial completo con trazabilidad.</p>
 */
public class FirmaConductual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediente_id", nullable = false)
    private Expediente expediente;

    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    @Builder.Default
    private boolean vigente = true;

    @Column(name = "comportamiento_pre_delictivo", columnDefinition = "TEXT")
    private String comportamientoPreDelictivo;

    @Column(name = "metodo_aproximacion", columnDefinition = "TEXT")
    private String metodoAproximacion;

    @Column(name = "metodo_ataque", columnDefinition = "TEXT")
    private String metodoAtaque;

    @Column(name = "comportamiento_post_delictivo", columnDefinition = "TEXT")
    private String comportamientoPostDelictivo;

    @Column(name = "elementos_distintivos", columnDefinition = "TEXT")
    private String elementosDistintivos;

    @Column(name = "embedding")
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 3072)
    private float[] embedding;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analista_id", nullable = false)
    private Usuario analista;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    /** true si al menos uno de los 5 campos tiene contenido (regla de negocio de la HU). */
    public boolean tieneAlMenosUnCampo() {
        return noEsVacio(comportamientoPreDelictivo) || noEsVacio(metodoAproximacion)
                || noEsVacio(metodoAtaque) || noEsVacio(comportamientoPostDelictivo)
                || noEsVacio(elementosDistintivos);
    }

    private boolean noEsVacio(String valor) {
        return valor != null && !valor.isBlank();
    }
}
