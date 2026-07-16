package com.nexocriminal.suceso.infrastructure.web.dto;

import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.suceso.domain.model.Suceso;

import java.time.LocalDateTime;

/**
 * DTO de salida para suceso. Anida los nodos relacionados con los MISMOS campos
 * que el frontend ya consume (placa/marca/modelo, nombre/apellido, direccion).
 */
public class SucesoResponse {

    /** Nodo vehiculo: campos que el front lee (placa, marca, modelo). */
    public static class VehiculoNodo {
        private final Long id;
        private final String placa;
        private final String marca;
        private final String modelo;
        private final String estado;
        public VehiculoNodo(Long id, String placa, String marca, String modelo, String estado) {
            this.id = id; this.placa = placa; this.marca = marca; this.modelo = modelo; this.estado = estado;
        }
        public Long getId() { return id; }
        public String getPlaca() { return placa; }
        public String getMarca() { return marca; }
        public String getModelo() { return modelo; }
        public String getEstado() { return estado; }
    }

    /** Nodo victima: campos que el front lee (nombre, apellido, documento). */
    public static class VictimaNodo {
        private final Long id;
        private final String nombre;
        private final String apellido;
        private final String documento;
        public VictimaNodo(Long id, String nombre, String apellido, String documento) {
            this.id = id; this.nombre = nombre; this.apellido = apellido; this.documento = documento;
        }
        public Long getId() { return id; }
        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
        public String getDocumento() { return documento; }
    }

    /** Nodo ubicacion: campos que el front lee (direccion, latitud, longitud). */
    public static class UbicacionNodo {
        private final Long id;
        private final String direccion;
        private final double latitud;
        private final double longitud;
        public UbicacionNodo(Long id, String direccion, double latitud, double longitud) {
            this.id = id; this.direccion = direccion; this.latitud = latitud; this.longitud = longitud;
        }
        public Long getId() { return id; }
        public String getDireccion() { return direccion; }
        public double getLatitud() { return latitud; }
        public double getLongitud() { return longitud; }
    }

    private final Long id;
    private final TipoSuceso tipo;
    private final LocalDateTime fechaHora;
    private final String modusOperandi;
    private final String descripcion;
    private final VehiculoNodo vehiculo;
    private final VictimaNodo victima;
    private final UbicacionNodo ubicacion;
    private final UbicacionNodo ubicacionUltima;
    private final LocalDateTime creadoEn;

    public SucesoResponse(Suceso s, VehiculoNodo vehiculo, VictimaNodo victima,
                          UbicacionNodo ubicacion, UbicacionNodo ubicacionUltima) {
        this.id = s.getId();
        this.tipo = s.getTipo();
        this.fechaHora = s.getFechaHora();
        this.modusOperandi = s.getModusOperandi();
        this.descripcion = s.getDescripcion();
        this.vehiculo = vehiculo;
        this.victima = victima;
        this.ubicacion = ubicacion;
        this.ubicacionUltima = ubicacionUltima;
        this.creadoEn = s.getCreadoEn();
    }

    public Long getId() { return id; }
    public TipoSuceso getTipo() { return tipo; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public String getModusOperandi() { return modusOperandi; }
    public String getDescripcion() { return descripcion; }
    public VehiculoNodo getVehiculo() { return vehiculo; }
    public VictimaNodo getVictima() { return victima; }
    public UbicacionNodo getUbicacion() { return ubicacion; }
    public UbicacionNodo getUbicacionUltima() { return ubicacionUltima; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}