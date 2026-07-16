package com.nexocriminal.vehiculo.domain.port;

import java.util.Optional;

/**
 * Puerto de lectura de personas. El modulo vehiculo solo necesita leer datos
 * minimos del propietario para mostrarlos, sin acoplarse al dominio Persona.
 */
public interface PersonaReaderPort {

    Optional<PropietarioData> findById(Long id);

    /** Datos minimos del propietario que el modulo vehiculo necesita exponer. */
    record PropietarioData(Long id, String nombre, String apellido, String documento) {}
}