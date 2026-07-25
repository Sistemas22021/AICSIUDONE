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
        Persona victima = resolverOVictima(req);
        Vehiculo vehiculoCreado = crearVehiculoRobado(req, victima);
        Suceso sucesoCreado = crearSucesoRobo(req, vehiculoCreado, victima);
        vincularTestigos(req, sucesoCreado);
        return sucesoCreado;
    }

    private Persona resolverOVictima(RoboCompletoRequest req) {
        if (req.victimaId != null) {
            return personaService.obtener(req.victimaId);
        }
        Persona nueva = new Persona();
        nueva.setDocumento(req.victimaDocumento);
        nueva.setNombre(req.victimaNombre);
        nueva.setApellido(req.victimaApellido);
        nueva.setTelefono(req.victimaTelefono);
        nueva.setAlias(req.victimaAlias);
        nueva.setRol(RolPersona.VICTIMA);
        return personaService.crear(nueva);
    }

    private Vehiculo crearVehiculoRobado(RoboCompletoRequest req, Persona propietario) {
        Vehiculo v = new Vehiculo();
        v.setPlaca(req.placa);
        v.setMarca(req.marca);
        v.setModelo(req.modelo);
        v.setAnio(req.anio);
        v.setColor(req.color);
        v.setChasis(req.chasis);
        v.setDeclaracion(req.declaracion);
        v.setEstado(EstadoVehiculo.ROBADO);
        v.setPropietario(propietario);
        return vehiculoService.crear(v);
    }

    private Suceso crearSucesoRobo(RoboCompletoRequest req, Vehiculo vehiculo, Persona victima) {
        Suceso s = Suceso.builder()
                .tipo(TipoSuceso.ROBO_VEHICULO)
                .fechaHora(req.fechaHora != null ? req.fechaHora : LocalDateTime.now())
                .modusOperandi(req.modusOperandi)
                .descripcion(req.descripcion)
                .vehiculo(vehiculo)
                .victima(victima)
                .build();
        if (req.ubicacionId != null) {
            Ubicacion u = new Ubicacion();
            u.setId(req.ubicacionId);
            s.setUbicacion(u);
        }
        return sucesoService.crear(s);
    }

    private void vincularTestigos(RoboCompletoRequest req, Suceso suceso) {
        if (req.testigos == null) return;
        for (RoboCompletoRequest.TestigoData t : req.testigos) {
            Persona testigo = t.id != null
                    ? personaService.obtener(t.id)
                    : crearPersonaTestigo(t);
            testigoRepository.save(SucesoTestigo.builder()
                    .sucesoId(suceso.getId())
                    .personaId(testigo.getId())
                    .build());
        }
    }

    private Persona crearPersonaTestigo(RoboCompletoRequest.TestigoData t) {
        Persona p = new Persona();
        p.setDocumento(t.documento);
        p.setNombre(t.nombre);
        p.setApellido(t.apellido);
        p.setTelefono(t.telefono);
        p.setRol(RolPersona.TESTIGO);
        return personaService.crear(p);
    }
}