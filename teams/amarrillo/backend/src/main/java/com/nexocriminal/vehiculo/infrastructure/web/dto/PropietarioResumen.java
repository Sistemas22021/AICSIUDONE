package com.nexocriminal.vehiculo.infrastructure.web.dto;

import com.nexocriminal.vehiculo.domain.port.PersonaReaderPort.PropietarioData;

/** Datos minimos del propietario que se anidan en la respuesta de vehiculo. */
public class PropietarioResumen {
    private final Long id;
    private final String nombre;
    private final String apellido;
    private final String documento;

    public PropietarioResumen(PropietarioData d) {
        this.id = d.id();
        this.nombre = d.nombre();
        this.apellido = d.apellido();
        this.documento = d.documento();
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDocumento() { return documento; }
}