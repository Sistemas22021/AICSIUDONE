package com.nexocriminal.robo;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Datos completos para registrar un robo de vehiculo en una sola operacion:
 * vehiculo + victima/propietario (una sola persona) + testigos + datos del suceso.
 */
public class RoboCompletoRequest {

    // --- Datos del vehiculo ---
    public String placa;
    public String marca;
    public String modelo;
    public Integer anio;
    public String color;
    public String chasis;
    public String declaracion;

    // --- Victima / propietario ---
    // Si victimaId viene, se usa esa persona. Si no, se crea con los datos de abajo.
    public Long victimaId;
    public String victimaDocumento;
    public String victimaNombre;
    public String victimaApellido;
    public String victimaTelefono;
    public String victimaAlias;

    // --- Datos del suceso ---
    public LocalDateTime fechaHora;
    public String modusOperandi;
    public String descripcion;
    public Long ubicacionId;

    // --- Testigos ---
    // Cada testigo: si trae id, se usa; si no, se crea con sus datos.
    public List<TestigoData> testigos;

    public static class TestigoData {
        public Long id;
        public String documento;
        public String nombre;
        public String apellido;
        public String telefono;
    }
}