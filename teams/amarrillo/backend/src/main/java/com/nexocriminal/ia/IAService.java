package com.nexocriminal.ia;

import com.nexocriminal.domain.alerta.Alerta;
import com.nexocriminal.domain.alerta.AlertaRepository;
import com.nexocriminal.domain.desaparecida.PersonaDesaparecida;
import com.nexocriminal.domain.desaparecida.PersonaDesaparecidaRepository;
import com.nexocriminal.domain.desaparecida.PersonaDesaparecidaService;
import com.nexocriminal.domain.persona.PersonaService;
import com.nexocriminal.domain.suceso.Suceso;
import com.nexocriminal.domain.suceso.SucesoService;
import com.nexocriminal.domain.ubicacion.Ubicacion;
import com.nexocriminal.domain.ubicacion.UbicacionService;
import com.nexocriminal.domain.vehiculo.VehiculoService;
import com.nexocriminal.ia.dto.MensajeChat;
import com.nexocriminal.ia.dto.RespuestaIA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IAService {

    private final ClaudeClient claudeClient;
    private final PersonaService personaService;
    private final VehiculoService vehiculoService;
    private final UbicacionService ubicacionService;
    private final SucesoService sucesoService;
    private final PersonaDesaparecidaService desaparecidaService;
    private final PersonaDesaparecidaRepository desaparecidaRepository;
    private final AlertaRepository alertaRepository;
    private final com.nexocriminal.modus.ModusOperandiService modusService;

    private static final String SYSTEM_BASE = """
            Eres un asistente especializado en inteligencia criminal del sistema Nexo Criminal.
            Trabajas como apoyo a investigadores y analistas.
            Sé directo, profesional y técnicamente preciso. Usa terminología policial cuando corresponda.
            Si te preguntan algo fuera del dominio de inteligencia criminal, redirigí al usuario.
            Responde siempre en español rioplatense profesional.
            No inventes datos: si no tienes información, decilo claramente.
            """;

    /**
     * 1. Chat conversacional con contexto.
     */
    public RespuestaIA chat(List<MensajeChat> historial, String pregunta, boolean incluirContexto) {
        StringBuilder system = new StringBuilder(SYSTEM_BASE);

        if (incluirContexto) {
            system.append("\n\n=== CONTEXTO DEL SISTEMA ===\n");
            system.append(generarContextoGeneral());
        }

        historial.add(new MensajeChat("user", pregunta));

        return claudeClient.preguntar(system.toString(), historial);
    }

    /**
     * 2. Predicción de zonas de búsqueda para una persona desaparecida.
     */
    public RespuestaIA predecirZonasBusqueda(Long desaparecidaId) {
        PersonaDesaparecida pd = desaparecidaService.obtener(desaparecidaId);

        StringBuilder prompt = new StringBuilder();
        prompt.append("Analizá el siguiente caso de desaparición y sugerí 3-5 zonas geográficas prioritarias para realizar la búsqueda.\n\n");

        prompt.append("CASO:\n");
        prompt.append("- Nombre: ").append(pd.getNombre()).append(" ").append(pd.getApellido()).append("\n");
        prompt.append("- Documento: ").append(pd.getDocumento()).append("\n");
        if (pd.getFechaNacimiento() != null) {
            prompt.append("- Fecha nacimiento: ").append(pd.getFechaNacimiento()).append("\n");
        }
        if (pd.getGenero() != null) prompt.append("- Género: ").append(pd.getGenero()).append("\n");
        prompt.append("- Fecha desaparición: ").append(pd.getFechaDesaparicion()).append("\n");
        prompt.append("- Días desaparecida: ").append(
                java.time.Duration.between(pd.getFechaDesaparicion(), LocalDateTime.now()).toDays()
        ).append("\n");
        prompt.append("- Prioridad: ").append(pd.getPrioridad()).append("\n");

        if (pd.getUltimaUbicacion() != null) {
            prompt.append("\nÚLTIMA UBICACIÓN CONOCIDA:\n");
            prompt.append("- Dirección: ").append(pd.getUltimaUbicacion().getDireccion()).append("\n");
            prompt.append("- Coordenadas: ").append(pd.getUltimaUbicacion().getLatitud())
                    .append(", ").append(pd.getUltimaUbicacion().getLongitud()).append("\n");
            prompt.append("- Tipo de zona: ").append(pd.getUltimaUbicacion().getTipo()).append("\n");
        }

        if (pd.getCircunstancias() != null) {
            prompt.append("\nCIRCUNSTANCIAS:\n").append(pd.getCircunstancias()).append("\n");
        }

        if (pd.getRopaAlDesaparecer() != null) {
            prompt.append("\nROPA AL DESAPARECER: ").append(pd.getRopaAlDesaparecer()).append("\n");
        }

        if (pd.getUltimaUbicacion() != null) {
            List<PersonaDesaparecida> cercanas = desaparecidaRepository.findEnRadio(
                    pd.getUltimaUbicacion().getLatitud(),
                    pd.getUltimaUbicacion().getLongitud(),
                    5000
            ).stream().filter(p -> !p.getId().equals(pd.getId())).toList();

            if (!cercanas.isEmpty()) {
                prompt.append("\nOTRAS DESAPARICIONES EN UN RADIO DE 5KM (").append(cercanas.size()).append("):\n");
                cercanas.stream().limit(5).forEach(c ->
                        prompt.append("- ").append(c.getNombre()).append(" ").append(c.getApellido())
                                .append(" (").append(c.getEstado()).append(", ")
                                .append(c.getFechaDesaparicion().toLocalDate()).append(")\n")
                );
            }
        }

        if (pd.getUltimaUbicacion() != null) {
            List<Ubicacion> nodosSospechosos = ubicacionService.listar().stream()
                    .filter(u -> Boolean.TRUE.equals(u.getNodoSospechoso()))
                    .toList();

            if (!nodosSospechosos.isEmpty()) {
                prompt.append("\nUBICACIONES SOSPECHOSAS REGISTRADAS EN EL SISTEMA (")
                        .append(nodosSospechosos.size()).append("):\n");
                nodosSospechosos.stream().limit(5).forEach(u ->
                        prompt.append("- ").append(u.getDireccion()).append(" (").append(u.getTipo()).append(")\n")
                );
            }
        }

        prompt.append("\n\nTAREA:\n");
        prompt.append("Para cada zona sugerida proporcioná:\n");
        prompt.append("1. Descripción de la zona y radio aproximado\n");
        prompt.append("2. Justificación basada en los datos del caso\n");
        prompt.append("3. Nivel de prioridad (Alta/Media/Baja)\n");
        prompt.append("4. Acciones recomendadas (entrevistas, cámaras, patrullaje)\n");
        prompt.append("\nFormato: numerá las zonas y usá lenguaje claro y profesional. ");
        prompt.append("No inventes ubicaciones específicas que no estén en los datos provistos.");

        RespuestaIA respuesta = claudeClient.preguntar(SYSTEM_BASE, prompt.toString());
        desaparecidaService.guardarZonasBusquedaIA(desaparecidaId, respuesta.getContenido());

        return respuesta;
    }

    /**
     * 3. Análisis de similitud entre desapariciones.
     */
    public RespuestaIA analizarSimilitudDesapariciones() {
        List<PersonaDesaparecida> activas = desaparecidaService.listar().stream()
                .filter(p -> "BUSCADA".equals(p.getEstado().name()))
                .toList();

        if (activas.size() < 2) {
            return RespuestaIA.builder()
                    .contenido("Se necesitan al menos 2 casos activos para realizar el análisis. Actualmente hay " + activas.size() + ".")
                    .modelo("local")
                    .build();
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("Analizá los siguientes casos de personas desaparecidas activos y detectá patrones, ");
        prompt.append("similitudes en modus operandi, características demográficas comunes o cualquier ");
        prompt.append("conexión que sugiera la posibilidad de un patrón delictivo.\n\n");

        prompt.append("CASOS ACTIVOS (").append(activas.size()).append("):\n\n");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < activas.size(); i++) {
            PersonaDesaparecida p = activas.get(i);
            prompt.append("== Caso ").append(i + 1).append(" ==\n");
            prompt.append("Nombre: ").append(p.getNombre()).append(" ").append(p.getApellido()).append("\n");
            if (p.getFechaNacimiento() != null) {
                int edad = LocalDateTime.now().getYear() - p.getFechaNacimiento().getYear();
                prompt.append("Edad aprox: ").append(edad).append(" años\n");
            }
            if (p.getGenero() != null) prompt.append("Género: ").append(p.getGenero()).append("\n");
            prompt.append("Fecha desaparición: ").append(p.getFechaDesaparicion().format(fmt)).append("\n");
            if (p.getUltimaUbicacion() != null) {
                prompt.append("Última ubicación: ").append(p.getUltimaUbicacion().getDireccion())
                        .append(" (").append(p.getUltimaUbicacion().getTipo()).append(")\n");
            }
            if (p.getCircunstancias() != null && !p.getCircunstancias().isBlank()) {
                prompt.append("Circunstancias: ").append(p.getCircunstancias()).append("\n");
            }
            if (p.getRopaAlDesaparecer() != null && !p.getRopaAlDesaparecer().isBlank()) {
                prompt.append("Ropa: ").append(p.getRopaAlDesaparecer()).append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("TAREA:\n");
        prompt.append("1. Identificá patrones demográficos (edad, género, contextura).\n");
        prompt.append("2. Detectá similitudes en circunstancias, vestimenta o comportamiento.\n");
        prompt.append("3. Analizá patrones geográficos o temporales.\n");
        prompt.append("4. Indicá si hay indicios de un posible modus operandi recurrente.\n");
        prompt.append("5. Recomendá próximos pasos para la investigación.\n\n");
        prompt.append("Sé objetivo, evitá especulaciones sin base, y resaltá tanto las coincidencias como las diferencias importantes.");

        return claudeClient.preguntar(SYSTEM_BASE, prompt.toString());
    }

    /**
     * 4. Generador de reporte ejecutivo.
     */
    public RespuestaIA generarReporte(String tipo, Long id) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generá un reporte ejecutivo profesional sobre el siguiente caso. ");
        prompt.append("Formato: estructura clara con secciones (Resumen ejecutivo, Datos del caso, Análisis, Recomendaciones).\n\n");

        switch (tipo.toLowerCase()) {
            case "desaparecida" -> {
                PersonaDesaparecida pd = desaparecidaService.obtener(id);
                prompt.append("CASO DE PERSONA DESAPARECIDA:\n");
                prompt.append("- Nombre: ").append(pd.getNombre()).append(" ").append(pd.getApellido()).append("\n");
                prompt.append("- Documento: ").append(pd.getDocumento()).append("\n");
                prompt.append("- Fecha desaparición: ").append(pd.getFechaDesaparicion()).append("\n");
                prompt.append("- Estado: ").append(pd.getEstado()).append("\n");
                prompt.append("- Prioridad: ").append(pd.getPrioridad()).append("\n");
                if (pd.getUltimaUbicacion() != null) {
                    prompt.append("- Última ubicación: ").append(pd.getUltimaUbicacion().getDireccion()).append("\n");
                }
                if (pd.getCircunstancias() != null) {
                    prompt.append("- Circunstancias: ").append(pd.getCircunstancias()).append("\n");
                }
                if (pd.getReportanteNombre() != null) {
                    prompt.append("- Reportante: ").append(pd.getReportanteNombre())
                            .append(" (").append(pd.getReportanteRelacion()).append(")\n");
                }
            }
            case "suceso" -> {
                Suceso s = sucesoService.obtener(id);
                prompt.append("SUCESO:\n");
                prompt.append("- ID: EV-").append(String.format("%04d", s.getId())).append("\n");
                prompt.append("- Tipo: ").append(s.getTipo()).append("\n");
                prompt.append("- Fecha: ").append(s.getFechaHora()).append("\n");
                if (s.getModusOperandi() != null) prompt.append("- Modus operandi: ").append(s.getModusOperandi()).append("\n");
                if (s.getVehiculo() != null) {
                    prompt.append("- Vehículo: ").append(s.getVehiculo().getPlaca()).append(" - ")
                            .append(s.getVehiculo().getMarca()).append(" ").append(s.getVehiculo().getModelo()).append("\n");
                }
                if (s.getVictima() != null) {
                    prompt.append("- Víctima: ").append(s.getVictima().getNombre()).append(" ")
                            .append(s.getVictima().getApellido()).append("\n");
                }
                if (s.getUbicacion() != null) {
                    prompt.append("- Ubicación: ").append(s.getUbicacion().getDireccion()).append("\n");
                }
                if (s.getDescripcion() != null) prompt.append("- Descripción: ").append(s.getDescripcion()).append("\n");
            }
            case "alerta" -> {
                Alerta a = alertaRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Alerta no encontrada"));
                prompt.append("ALERTA:\n");
                prompt.append("- ID: NX-").append(String.format("%05d", a.getId())).append("\n");
                prompt.append("- Tipo: ").append(a.getTipo()).append("\n");
                prompt.append("- Nivel de riesgo: ").append(a.getNivelRiesgo()).append("\n");
                prompt.append("- Estado: ").append(a.getEstado()).append("\n");
                prompt.append("- Título: ").append(a.getTitulo()).append("\n");
                if (a.getDescripcion() != null) prompt.append("- Descripción: ").append(a.getDescripcion()).append("\n");
            }
            default -> throw new IllegalArgumentException("Tipo no soportado: " + tipo);
        }

        prompt.append("\n\nGenerá el reporte usando un tono institucional, profesional y conciso. ");
        prompt.append("Incluí una sección de recomendaciones concretas para los investigadores.");

        return claudeClient.preguntar(SYSTEM_BASE, prompt.toString());
    }

    /**
     * 5. Clasifica una descripción de hecho en un código de modus operandi del catálogo.
     * La IA SOLO puede elegir entre los códigos existentes; si la respuesta no es
     * un código válido, cae a OTRO. Esto garantiza que el motor pueda matchear.
     */
    public java.util.Map<String, String> clasificarModus(String descripcion) {
        var catalogo = modusService.listarActivos();

        if (descripcion == null || descripcion.isBlank()) {
            return java.util.Map.of("codigo", "OTRO", "etiqueta", "Otro");
        }

        // Construir la lista de códigos válidos para el prompt y para validar
        StringBuilder lista = new StringBuilder();
        java.util.Set<String> codigosValidos = new java.util.HashSet<>();
        for (var m : catalogo) {
            codigosValidos.add(m.getCodigo());
            lista.append("- ").append(m.getCodigo())
                 .append(": ").append(m.getEtiqueta());
            if (m.getDescripcion() != null && !m.getDescripcion().isBlank()) {
                lista.append(" (").append(m.getDescripcion()).append(")");
            }
            lista.append("\n");
        }

        String system = """
                Sos un clasificador de modus operandi para un sistema de inteligencia criminal.
                Tu única tarea es leer la descripción de un hecho delictivo y elegir el código
                de modus operandi que mejor lo describe, de una lista cerrada.
                Respondé ÚNICAMENTE con el código exacto (en mayúsculas, tal como aparece en la lista),
                sin explicación, sin puntuación, sin texto adicional.
                Si ninguno encaja con razonable certeza, respondé OTRO.
                """;

        String prompt = "DESCRIPCIÓN DEL HECHO:\n" + descripcion + "\n\n" +
                "CÓDIGOS DISPONIBLES:\n" + lista + "\n" +
                "Respondé solo con el código que corresponde.";

        String codigo;
        try {
            RespuestaIA r = claudeClient.preguntar(system, prompt);
            codigo = r.getContenido() != null ? r.getContenido().trim().toUpperCase() : "";
            // Limpiar: dejar solo el primer token tipo CODIGO (letras, números, guion bajo)
            var matcher = java.util.regex.Pattern.compile("[A-Z0-9_]+").matcher(codigo);
            codigo = matcher.find() ? matcher.group() : "";
        } catch (Exception e) {
            log.warn("Error al clasificar modus con IA: {}", e.getMessage());
            codigo = "";
        }

        // VALIDACIÓN CLAVE: el código debe existir en el catálogo. Si no, OTRO.
        if (!codigosValidos.contains(codigo)) {
            codigo = "OTRO";
        }

        // Buscar la etiqueta para devolverla
        final String codigoFinal = codigo;
        String etiqueta = catalogo.stream()
                .filter(m -> m.getCodigo().equals(codigoFinal))
                .map(m -> m.getEtiqueta())
                .findFirst()
                .orElse("Otro");

        return java.util.Map.of("codigo", codigoFinal, "etiqueta", etiqueta);
    }

    /**
     * Genera un texto de contexto general del sistema para el chat.
     */
    private String generarContextoGeneral() {
        StringBuilder ctx = new StringBuilder();

        try {
            int personas = personaService.listar().size();
            int vehiculos = vehiculoService.listar().size();
            int ubicaciones = ubicacionService.listar().size();
            int sucesos = sucesoService.listar().size();
            int desaparecidas = desaparecidaService.listar().size();
            int alertasActivas = alertaRepository.findAll().size();

            ctx.append("Estado actual del sistema Nexo Criminal:\n");
            ctx.append("- Personas registradas: ").append(personas).append("\n");
            ctx.append("- Vehículos registrados: ").append(vehiculos).append("\n");
            ctx.append("- Ubicaciones: ").append(ubicaciones).append("\n");
            ctx.append("- Sucesos: ").append(sucesos).append("\n");
            ctx.append("- Personas desaparecidas: ").append(desaparecidas).append("\n");
            ctx.append("- Alertas en sistema: ").append(alertasActivas).append("\n");

            List<Alerta> criticas = alertaRepository.findAll().stream()
                    .filter(a -> "CRITICO".equals(a.getNivelRiesgo().name()))
                    .limit(3)
                    .toList();
            if (!criticas.isEmpty()) {
                ctx.append("\nÚltimas alertas críticas:\n");
                criticas.forEach(a ->
                        ctx.append("- ").append(a.getTitulo()).append("\n"));
            }

            List<PersonaDesaparecida> desaparecidasCriticas = desaparecidaService.listar().stream()
                    .filter(p -> "BUSCADA".equals(p.getEstado().name()))
                    .filter(p -> "CRITICA".equals(p.getPrioridad().name()))
                    .limit(3)
                    .toList();
            if (!desaparecidasCriticas.isEmpty()) {
                ctx.append("\nCasos críticos de desaparición activos:\n");
                desaparecidasCriticas.forEach(p ->
                        ctx.append("- ").append(p.getNombre()).append(" ").append(p.getApellido())
                                .append(" (").append(p.getDocumento()).append(")\n"));
            }

        } catch (Exception e) {
            log.warn("No se pudo cargar contexto general: {}", e.getMessage());
            ctx.append("(No se pudo cargar el contexto en este momento)\n");
        }

        return ctx.toString();
    }

    /**
     * Analiza un suceso y determina la prioridad para el sistema de patrullas (Cian).
     * Devuelve exactamente uno de: LOW, MEDIUM, HIGH.
     */
    public String calcularPrioridadIncidente(Long sucesoId) {
        Suceso s = sucesoService.obtener(sucesoId);

        String system = """
                Eres un clasificador de prioridad para despacho de patrullas policiales.
                Analiza un suceso criminal y devuelve su prioridad de atención.
                Respondé ÚNICAMENTE con una palabra en mayúsculas: LOW, MEDIUM o HIGH.
                No agregues explicación ni ningún otro texto.
                Criterio: HIGH para hechos violentos, en curso o con riesgo a personas;
                MEDIUM para delitos contra la propiedad ya consumados; LOW para hechos menores o antiguos.
                """;

        StringBuilder prompt = new StringBuilder();
        prompt.append("Clasificá la prioridad de este suceso:\n");
        prompt.append("- Tipo: ").append(s.getTipo()).append("\n");
        if (s.getDescripcion() != null) prompt.append("- Descripción: ").append(s.getDescripcion()).append("\n");
        if (s.getModusOperandi() != null) prompt.append("- Modus operandi: ").append(s.getModusOperandi()).append("\n");
        prompt.append("\nRespondé solo con LOW, MEDIUM o HIGH.");

        RespuestaIA r = claudeClient.preguntar(system, prompt.toString());
        String salida = r.getContenido() != null ? r.getContenido().trim().toUpperCase() : "";

        // Normalizar: quedarnos solo con una de las tres etiquetas validas
        if (salida.contains("HIGH")) return "HIGH";
        if (salida.contains("LOW")) return "LOW";
        return "MEDIUM"; // valor seguro por defecto
    }

    /**
     * Calcula la prioridad de una desaparición para el despacho de patrullas (Cian).
     * Devuelve LOW, MEDIUM o HIGH.
     */
    public String calcularPrioridadDesaparicion(Long desaparecidaId) {
        PersonaDesaparecida pd = desaparecidaService.obtener(desaparecidaId);

        String system = """
                Sos un clasificador de prioridad para despacho de patrullas policiales.
                Analizás un caso de persona desaparecida y devolvés su prioridad de atención.
                Respondé ÚNICAMENTE con una palabra en mayúsculas: LOW, MEDIUM o HIGH.
                No agregues explicación ni ningún otro texto.
                Criterio: HIGH para desapariciones recientes, menores de edad o con circunstancias
                de riesgo; MEDIUM para casos activos sin riesgo inmediato evidente; LOW para casos
                antiguos o archivados.
                """;

        StringBuilder prompt = new StringBuilder();
        prompt.append("Clasificá la prioridad de esta desaparición:\n");
        prompt.append("- Nombre: ").append(pd.getNombre()).append(" ").append(pd.getApellido()).append("\n");
        prompt.append("- Prioridad interna registrada: ").append(pd.getPrioridad()).append("\n");
        prompt.append("- Estado: ").append(pd.getEstado()).append("\n");
        prompt.append("- Fecha desaparición: ").append(pd.getFechaDesaparicion()).append("\n");
        if (pd.getCircunstancias() != null) prompt.append("- Circunstancias: ").append(pd.getCircunstancias()).append("\n");
        prompt.append("\nRespondé solo con LOW, MEDIUM o HIGH.");

        RespuestaIA r = claudeClient.preguntar(system, prompt.toString());
        String salida = r.getContenido() != null ? r.getContenido().trim().toUpperCase() : "";
        if (salida.contains("HIGH")) return "HIGH";
        if (salida.contains("LOW")) return "LOW";
        return "MEDIUM";
    }

    /**
     * Procesa el texto de un testimonio (transcripción de Naranja) y extrae
     * campos estructurados usando IA. Devuelve un Map con los campos que la IA
     * pudo identificar. Si la IA falla, devuelve un mapa con error.
     */
    public java.util.Map<String, String> extraerCamposTestimonio(String texto) {
        if (texto == null || texto.isBlank()) {
            return java.util.Map.of("error", "El texto del testimonio está vacío");
        }

        String system = """
                Sos un analista de inteligencia criminal. Te dan la transcripción de un
                testimonio (texto plano) y tu tarea es extraer datos estructurados de un
                posible suceso criminal.
                Respondé ÚNICAMENTE con un objeto JSON válido, sin texto antes ni después,
                sin markdown, con exactamente estas claves (usá cadena vacía si no aplica):
                {
                  "tipoSugerido": "ROBO_VEHICULO | AVISTAMIENTO | TRANSACCION | DESAPARICION | OTRO",
                  "modusOperandi": "breve descripción del método usado, si se menciona",
                  "descripcion": "resumen objetivo de los hechos relatados",
                  "ubicacionMencionada": "lugar o dirección que se mencione, si hay",
                  "personasMencionadas": "nombres o alias mencionados, separados por coma"
                }
                No inventes datos: si algo no está en el testimonio, dejá la cadena vacía.
                """;

        String prompt = "TRANSCRIPCIÓN DEL TESTIMONIO:\n" + texto +
                "\n\nExtraé los campos y respondé solo con el JSON.";

        try {
            RespuestaIA r = claudeClient.preguntar(system, prompt);
            String salida = r.getContenido() != null ? r.getContenido().trim() : "";

            // Blindaje: extraer solo el bloque JSON (desde la primera { hasta la última })
            int ini = salida.indexOf('{');
            int fin = salida.lastIndexOf('}');
            if (ini < 0 || fin < 0 || fin <= ini) {
                return java.util.Map.of("error", "La IA no devolvió un JSON válido",
                        "respuestaCruda", salida);
            }
            String json = salida.substring(ini, fin + 1);

            // Parsear el JSON a un Map<String,String>
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(json);

            java.util.Map<String, String> campos = new java.util.HashMap<>();
            node.fieldNames().forEachRemaining(k ->
                    campos.put(k, node.get(k).asText("")));
            return campos;

        } catch (Exception e) {
            log.warn("Error al extraer campos del testimonio: {}", e.getMessage());
            return java.util.Map.of("error", "No se pudo procesar el testimonio: " + e.getMessage());
        }
    }
}