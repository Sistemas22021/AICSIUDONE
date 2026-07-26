package com.nexocriminal.robo;

import com.nexocriminal.domain.persona.Persona;
import com.nexocriminal.domain.persona.PersonaService;
import com.nexocriminal.domain.persona.RolPersona;
import com.nexocriminal.domain.suceso.Suceso;
import com.nexocriminal.domain.suceso.SucesoService;
import com.nexocriminal.domain.suceso.TipoSuceso;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import com.nexocriminal.domain.vehiculo.EstadoVehiculo;
import com.nexocriminal.domain.vehiculo.Vehiculo;
import com.nexocriminal.domain.vehiculo.VehiculoService;
import com.nexocriminal.testigo.SucesoTestigo;
import com.nexocriminal.testigo.SucesoTestigoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta el registro completo de un robo de vehiculo en UNA transaccion:
 * crea (o usa) la victima/propietario, crea el vehiculo, crea el suceso y
 * vincula los testigos. Si cualquier paso falla, se revierte todo (rollback).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RoboCompletoService {

    private final PersonaService personaService;
    private final VehiculoService vehiculoService;
    private final SucesoService sucesoService;
    private final SucesoTestigoRepository testigoRepository;

    public Suceso registrar(RoboCompletoRequest req) {

        // 1. Resolver victima/propietario: usar existente o crear nueva
        Persona victima;
        if (req.victimaId != null) {
            victima = personaService.obtener(req.victimaId);
        } else {
            Persona nueva = new Persona();
            nueva.setDocumento(req.victimaDocumento);
            nueva.setNombre(req.victimaNombre);
            nueva.setApellido(req.victimaApellido);
            nueva.setTelefono(req.victimaTelefono);
            nueva.setAlias(req.victimaAlias);
            nueva.setRol(RolPersona.VICTIMA);
            victima = personaService.crear(nueva);
        }

        // 2. Crear el vehiculo, con la victima como propietario, estado ROBADO
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(req.placa);
        vehiculo.setMarca(req.marca);
        vehiculo.setModelo(req.modelo);
        vehiculo.setAnio(req.anio);
        vehiculo.setColor(req.color);
        vehiculo.setChasis(req.chasis);
        vehiculo.setDeclaracion(req.declaracion);
        vehiculo.setEstado(EstadoVehiculo.ROBADO);
        vehiculo.setPropietario(victima);
        Vehiculo vehiculoCreado = vehiculoService.crear(vehiculo);

        // 3. Crear el suceso de tipo ROBO_VEHICULO
        Suceso suceso = Suceso.builder()
                .tipo(TipoSuceso.ROBO_VEHICULO)
                .fechaHora(req.fechaHora != null ? req.fechaHora : LocalDateTime.now())
                .modusOperandi(req.modusOperandi)
                .descripcion(req.descripcion)
                .vehiculo(vehiculoCreado)
                .victima(victima)
                .build();
        if (req.ubicacionId != null) {
            Ubicacion u = new Ubicacion();
            u.setId(req.ubicacionId);
            suceso.setUbicacion(u);
        }
        Suceso sucesoCreado = sucesoService.crear(suceso);

        // 4. Vincular testigos: crear los nuevos, y registrar la relacion
        if (req.testigos != null) {
            for (RoboCompletoRequest.TestigoData t : req.testigos) {
                Persona testigo;
                if (t.id != null) {
                    testigo = personaService.obtener(t.id);
                } else {
                    Persona nuevoTestigo = new Persona();
                    nuevoTestigo.setDocumento(t.documento);
                    nuevoTestigo.setNombre(t.nombre);
                    nuevoTestigo.setApellido(t.apellido);
                    nuevoTestigo.setTelefono(t.telefono);
                    nuevoTestigo.setRol(RolPersona.TESTIGO);
                    testigo = personaService.crear(nuevoTestigo);
                }
                testigoRepository.save(SucesoTestigo.builder()
                        .sucesoId(sucesoCreado.getId())
                        .personaId(testigo.getId())
                        .build());
            }
        }

        return sucesoCreado;
    }
}