package com.nexocriminal.integracion;

import com.nexocriminal.domain.persona.Persona;
import com.nexocriminal.domain.persona.PersonaService;
import com.nexocriminal.ia.IAService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Integración con el Equipo Naranja (testimonios).
 * Naranja transcribe el audio y nos envía el texto plano; nuestra IA
 * extrae campos estructurados y los devuelve para que un analista los revise.
 */
@Tag(name = "Testimonios (Naranja)", description = "Recibe transcripciones del Equipo Naranja y extrae campos con IA")
@RestController
@RequestMapping("/api/v1/testimonios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TestimonioController {

    private final IAService iaService;
    private final PersonaService personaService;

    @Operation(summary = "Procesar testimonio",
               description = "Recibe {documento, texto, fecha} desde Naranja, extrae campos del testimonio con IA " +
                       "y vincula la persona por documento si existe. NO crea nada automáticamente: devuelve los datos para revisión.")
    @PostMapping("/procesar")
    public ResponseEntity<?> procesar(@RequestBody Map<String, String> body) {
        String documento = body.get("documento");
        String texto = body.get("texto");
        String fecha = body.get("fecha");

        if (texto == null || texto.isBlank()) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "El campo 'texto' es obligatorio"));
        }

        // 1. Buscar la persona por documento (si vino y existe)
        Map<String, Object> personaInfo = new HashMap<>();
        if (documento != null && !documento.isBlank()) {
            Optional<Persona> personaOpt = personaService.buscarPorDocumento(documento);
            if (personaOpt.isPresent()) {
                Persona p = personaOpt.get();
                personaInfo.put("encontrada", true);
                personaInfo.put("id", p.getId());
                personaInfo.put("nombre", p.getNombre());
                personaInfo.put("apellido", p.getApellido());
                personaInfo.put("documento", p.getDocumento());
                personaInfo.put("rol", p.getRol() != null ? p.getRol().name() : null);
            } else {
                personaInfo.put("encontrada", false);
                personaInfo.put("documentoBuscado", documento);
            }
        } else {
            personaInfo.put("encontrada", false);
            personaInfo.put("motivo", "No se envió documento");
        }

        // 2. La IA extrae los campos del testimonio
        Map<String, String> camposExtraidos = iaService.extraerCamposTestimonio(texto);

        // 3. Devolver todo junto para que el analista revise antes de guardar
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("persona", personaInfo);
        respuesta.put("camposExtraidos", camposExtraidos);
        respuesta.put("fecha", fecha);
        respuesta.put("textoOriginal", texto);

        return ResponseEntity.ok(respuesta);
    }
}