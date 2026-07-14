package com.guardia.core.dto.ai;

/**
 * Esquema de salida estructurada que Spring AI (ChatClient#entity) exige al
 * modelo GPT-4 al analizar un expediente contra sus casos similares (HU2,
 * CA4: "características comunes, posible firma del perpetrador, consistencia
 * de horario o zona, y nivel de confianza estimado").
 *
 * <p>No se usa {@code record} aquí por capricho: al pasarse como
 * {@code .entity(AnalisisMoIA.class)}, Spring AI genera automáticamente el
 * JSON Schema a partir de los componentes del record y valida/parsea la
 * respuesta del modelo contra él.</p>
 */
public record AnalisisMoIA(
        String caracteristicasComunes,
        String posibleFirma,
        String consistenciaHorarioZona,
        String resumen,
        Double nivelConfianza
) {}