package equipoBlanco.com.prison_service.modules.inmates.dto;

import lombok.*;
import java.util.UUID;

/**
 * DTO para la solicitud de cierre formal de una investigación de incidente interno.
 * <p>
 * Los campos {@code autopsiaProtocolo} y {@code fiscaliaExpediente} son
 * obligatorios por ley para poder cerrar el incidente en el sistema.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConcludeIncidentDto {

    /**
     * Tipo de conclusión oficial.
     * Valores válidos: HOMICIDIO, SUICIDIO, ACCIDENTAL, NATURAL
     */
    private String conclusionType;

    /** Descripción médica de la causa definitiva del fallecimiento. */
    private String causaMedicaDefinitiva;

    /**
     * Número de protocolo de autopsia emitido por el instituto forense/morgue.
     * OBLIGATORIO para cerrar el incidente.
     */
    private String autopsiaProtocolo;

    /**
     * Número de expediente de la Fiscalía.
     * OBLIGATORIO para cerrar el incidente.
     */
    private String fiscaliaExpediente;

    /**
     * ID del recluso identificado como responsable (aplica en HOMICIDIO).
     * Si se especifica y hay años adicionales, se le suman a su condena.
     */
    private UUID responsableInmateId;

    /**
     * Nombre del personal del penal identificado como responsable (aplica en HOMICIDIO).
     * Opcional — si no aplica, dejar en null.
     */
    private String responsablePersonal;

    /**
     * Indica que no hay responsable directo identificable (SUICIDIO / ACCIDENTAL).
     * Si es true, los campos responsable* se ignoran.
     */
    private Boolean responsableNoAplica;

    /**
     * Años adicionales de condena a imputar al responsable.
     * Solo aplica si {@code responsableInmateId} está presente.
     */
    private Integer additionalSentenceYears;

    /**
     * Meses adicionales de condena a imputar al responsable.
     * Solo aplica si {@code responsableInmateId} está presente.
     */
    private Integer additionalSentenceMonths;
}

