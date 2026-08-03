package com.guardia.core.dto.request;

/**
 * Criterios de búsqueda de patrones (HU "Buscar patrones por MO y firma
 * conductual", CA1). {@code textoMO} y {@code textoFirmaConductual} son
 * ambos opcionales, pero al menos uno debe estar presente: la validación de
 * esa regla vive en {@code PatronBusquedaServiceImpl} porque Bean Validation
 * no expresa bien reglas "al menos uno de N" entre dos campos independientes.
 */
public record PatronBusquedaRequest(
        String textoMO,
        String textoFirmaConductual,
        Integer limite
) {}