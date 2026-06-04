package com.guardia.core.controller;
import com.guardia.core.dto.request.*;
import com.guardia.core.dto.response.ExpedienteResponse;
import com.guardia.core.exception.ApiResponse;
import com.guardia.core.service.ExpedienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adaptador: recibe el payload del frontend (POST /api/v1/incidentes)
 * y lo transforma al formato que espera ExpedienteService.
 *
 * Frontend llama:  POST /api/v1/incidentes     con IncidenteRequest
 * Backend tiene:   POST /api/expedientes/registrar  con ExpedienteRequest
 *
 * Este controller resuelve la diferencia sin tocar el servicio ni el modelo.
 */
@RestController
@RequestMapping("/api/v1/incidentes")
@RequiredArgsConstructor
public class IncidenteController {

    private final ExpedienteService expedienteService;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * POST /api/v1/incidentes
     * Recibe el JSON del frontend, lo convierte a ExpedienteRequest y delega al servicio.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ExpedienteResponse>> registrar(
            @Valid @RequestBody IncidenteRequest incidente) {

        ExpedienteRequest expediente = mapToExpedienteRequest(incidente);
        ExpedienteResponse response  = expedienteService.crear(expediente);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Incidente registrado exitosamente.", response));
    }

    // ─── Mapping ─────────────────────────────────────────────────────────────

    private ExpedienteRequest mapToExpedienteRequest(IncidenteRequest src) {
        ExpedienteRequest dest = new ExpedienteRequest();

        dest.setUbicacion(mapUbicacion(src.getUbicacion()));
        dest.setDelitos(mapDelitos(src.getDelitos()));
        dest.setDescripcion(src.getDescripcion());
        dest.setEsDenunciaFormal(Boolean.TRUE.equals(src.getEsDenunciaFormal()));
        dest.setVictimas(mapVictimas(src.getInvolucrados()));

        if (src.getDenunciante() != null) {
            dest.setDenunciante(mapDenunciante(src.getDenunciante()));
        }

        return dest;
    }

    private UbicacionRequest mapUbicacion(IncidenteRequest.UbicacionFrontRequest src) {
        UbicacionRequest u = new UbicacionRequest();
        u.setMunicipio(src.getMunicipio());
        u.setSector(src.getSector());
        u.setDireccion(src.getDireccion());
        u.setReferencia(src.getReferencia());

        if (src.getCoordenadas() != null) {
            CoordenadasRequest c = new CoordenadasRequest();
            // Frontend envía {lat, lng} → Backend espera {latitud, longitud}
            c.setLatitud(src.getCoordenadas().getLat());
            c.setLongitud(src.getCoordenadas().getLng());
            u.setCoordenadas(c);
        } else {
            // Si no hay GPS, usar 0,0 como coordenadas neutras
            CoordenadasRequest c = new CoordenadasRequest();
            c.setLatitud(0.0);
            c.setLongitud(0.0);
            u.setCoordenadas(c);
        }

        return u;
    }

    private List<DelitoRequest> mapDelitos(List<IncidenteRequest.DelitoFrontRequest> src) {
        if (src == null) return List.of();
        return src.stream().map(d -> {
            DelitoRequest r = new DelitoRequest();

            // Frontend envía tipoDelito (label) → Backend espera "delito" (String)
            r.setDelito(d.getTipoDelito());

            // Frontend envía subtipoDelito (nombre) → Backend espera SubDelitoRequest
            SubDelitoRequest sub = new SubDelitoRequest();
            sub.setNombre(d.getSubtipoDelito());
            r.setSubDelito(sub);

            // Parseo de fechas — formato "yyyy-MM-dd"
            r.setFechaHecho(LocalDate.parse(d.getFechaHecho(), DATE_FMT));

            // Parseo de horas — formato "HH:mm"
            r.setHoraInicioHecho(parseTime(d.getHoraInicio()));
            r.setHoraFin(d.isHechoEnCurso() ? null : parseTime(d.getHoraFin()));
            r.setHechoEnCurso(d.isHechoEnCurso());

            return r;
        }).collect(Collectors.toList());
    }

    private List<InvolucradosRequest> mapVictimas(List<IncidenteRequest.InvolucradoFrontRequest> src) {
        if (src == null) return List.of();
        return src.stream().map(inv -> {
            InvolucradosRequest v = new InvolucradosRequest();

            // El frontend envía nombre completo en un solo campo.
            // Intentamos separar apellido (última palabra) del nombre.
            String[] partes = splitNombreApellido(inv.getNombre());
            v.setNombre(partes[0]);
            v.setApellido(partes[1]);

            v.setCedula(inv.getIdentificacion());
            v.setTelefono(inv.getTelefono());
            v.setNacionalidad(inv.getNacionalidad());
            v.setDireccion(inv.getDireccion());

            return v;
        }).collect(Collectors.toList());
    }

    private DenuncianteRequest mapDenunciante(IncidenteRequest.DenuncianteFrontRequest src) {
        DenuncianteRequest d = new DenuncianteRequest();
        d.setIdentificacion(src.getIdentificacion());
        d.setNombre(src.getNombre());
        d.setNacionalidad(src.getNacionalidad());
        d.setTelefono(src.getTelefono());
        d.setDireccion(src.getDireccion());
        // Frontend usa relacionConCrimen → Backend usa relacionConHecho
        d.setRelacionConHecho(src.getRelacionConCrimen());
        return d;
    }

    // ─── Utilidades ──────────────────────────────────────────────────────────

    private LocalTime parseTime(String hora) {
        if (hora == null || hora.isBlank()) return null;
        try {
            return LocalTime.parse(hora, TIME_FMT);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Separa "Juan Pérez" → ["Juan", "Pérez"].
     * Si solo hay una palabra, apellido queda vacío.
     */
    private String[] splitNombreApellido(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) return new String[]{"", ""};
        int ultimo = nombreCompleto.lastIndexOf(' ');
        if (ultimo < 0) return new String[]{nombreCompleto, ""};
        return new String[]{
                nombreCompleto.substring(0, ultimo).trim(),
                nombreCompleto.substring(ultimo + 1).trim()
        };
    }
}
