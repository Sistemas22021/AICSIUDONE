package com.nexocriminal.ia;

import com.nexocriminal.ia.dto.MensajeChat;
import com.nexocriminal.ia.dto.RespuestaIA;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Cliente Mock que simula respuestas de Claude para modo demo.
 * Se usa cuando la API real de Anthropic no está disponible
 * por restricciones operativas externas (billing, geo-bloqueo, etc).
 */
@Component
@Slf4j
public class MockClaudeClient {

    private final Random random = new Random();

    public RespuestaIA preguntar(String systemPrompt, List<MensajeChat> mensajes) {
        log.info("[MOCK IA] Generando respuesta simulada (modo demo)");

        try {
            Thread.sleep(800 + random.nextInt(1200));
        } catch (InterruptedException ignored) {}

        String ultimaPregunta = mensajes.isEmpty() ? "" :
                mensajes.get(mensajes.size() - 1).getContent().toLowerCase();

        String respuesta = generarRespuesta(ultimaPregunta);

        return RespuestaIA.builder()
                .contenido(respuesta)
                .modelo("claude-sonnet-4-5-demo")
                .tokensEntrada(estimarTokens(systemPrompt + ultimaPregunta))
                .tokensSalida(estimarTokens(respuesta))
                .durationMs(800L + random.nextInt(1200))
                .build();
    }

    public RespuestaIA preguntar(String systemPrompt, String prompt) {
        log.info("[MOCK IA] Generando respuesta simulada (modo demo)");

        try {
            Thread.sleep(800 + random.nextInt(1200));
        } catch (InterruptedException ignored) {}

        String respuesta = generarRespuesta(prompt.toLowerCase());

        return RespuestaIA.builder()
                .contenido(respuesta)
                .modelo("claude-sonnet-4-5-demo")
                .tokensEntrada(estimarTokens(systemPrompt + prompt))
                .tokensSalida(estimarTokens(respuesta))
                .durationMs(800L + random.nextInt(1200))
                .build();
    }

    private String generarRespuesta(String texto) {

        if (contiene(texto, "reporte ejecutivo", "informe", "ejecutivo", "resumen ejecutivo")) {
            return generarReporteEjecutivo();
        }

        if (contiene(texto, "zonas geográficas", "zonas geograficas", "zonas prioritarias",
                "predecí", "predeci", "búsqueda", "busqueda", "última ubicación")) {
            return generarPrediccionZonas();
        }

        if (contiene(texto, "patrones", "similitudes", "modus operandi", "demográficos",
                "casos activos", "patrón delictivo", "patron delictivo")) {
            return generarAnalisisSimilitud();
        }

        if (contiene(texto, "alerta", "alertas", "criticas", "críticas", "criticos", "críticos")) {
            return generarRespuestaAlertas();
        }

        if (contiene(texto, "estado", "panorama", "resumen del sistema", "cuántas", "cuantas")) {
            return generarRespuestaEstado();
        }

        if (contiene(texto, "hola", "buenas", "saludos", "buenos dias", "buenos días", "buenas tardes")) {
            return generarSaludo();
        }

        return generarRespuestaGenerica();
    }

    private String generarReporteEjecutivo() {
        return """
                **REPORTE EJECUTIVO**

                ---

                ## Resumen Ejecutivo

                El presente reporte sintetiza la información operativa relevante del caso bajo análisis. Se identifican patrones críticos que requieren atención inmediata por parte de las unidades de inteligencia.

                ## Datos del Caso

                - **Tipo de incidente**: Caso bajo investigación activa
                - **Estado**: Pendiente de confirmación operativa
                - **Nivel de riesgo**: Alto
                - **Entidades involucradas**: Múltiples (consultar detalle en sistema)

                ## Análisis

                El análisis de los datos disponibles revela conexiones no evidentes entre las entidades registradas. El motor Red Thread ha detectado vínculos que sugieren la presencia de una red organizada operando en la zona.

                **Hallazgos principales:**
                1. Convergencia geográfica de eventos en una ventana temporal corta.
                2. Coincidencias en el modus operandi de sucesos previamente registrados.
                3. Presencia de intermediarios en el círculo social del sospechoso.

                ## Recomendaciones

                1. **Acción inmediata**: Confirmar las alertas de nivel crítico generadas por el sistema.
                2. **Mediano plazo**: Profundizar la investigación sobre las ubicaciones marcadas como sospechosas.
                3. **Seguimiento**: Monitorear avistamientos de los vehículos vinculados durante las próximas 72 horas.
                4. **Coordinación**: Articular acciones con las brigadas de inteligencia y patrullaje operativo.

                ---

                *Reporte generado por el módulo de inteligencia de Nexo Criminal.*
                """;
    }

    private String generarPrediccionZonas() {
        return """
                **Análisis de Zonas Prioritarias para Búsqueda**

                Basándome en los datos del caso, las circunstancias de la desaparición y los patrones observados en casos similares, sugiero las siguientes zonas en orden de prioridad:

                ---

                ### 🔴 Zona 1 — PRIORIDAD ALTA

                **Descripción**: Radio de 800 metros desde la última ubicación conocida.

                **Justificación**: En las primeras 48 horas posteriores a una desaparición, estudios estadísticos indican que un alto porcentaje de las personas son localizadas a menos de 1 km del último punto conocido. Esta zona debe priorizarse en las primeras horas operativas.

                **Acciones recomendadas**:
                - Patrullaje a pie con apoyo de unidades vecinales.
                - Revisión de cámaras de seguridad de comercios cercanos.
                - Entrevistas a comerciantes y residentes habituales del área.

                ---

                ### 🟠 Zona 2 — PRIORIDAD ALTA

                **Descripción**: Terminales de transporte público en radio de 3 km.

                **Justificación**: Si la desaparición involucró desplazamiento (voluntario o forzado), las terminales de transporte son puntos estratégicos de tránsito.

                **Acciones recomendadas**:
                - Solicitar grabaciones de cámaras de las terminales identificadas.
                - Revisar registros de pasajeros si están disponibles.
                - Consultar a personal operativo de las terminales.

                ---

                ### 🟡 Zona 3 — PRIORIDAD MEDIA

                **Descripción**: Galpones, terrenos baldíos y ubicaciones marcadas como sospechosas en radio de 5 km.

                **Justificación**: El análisis de clusters previos del sistema sugiere correlación con ubicaciones marcadas como nodos sospechosos.

                **Acciones recomendadas**:
                - Inspección coordinada con apoyo de fiscalía.
                - Contraste con avistamientos vehiculares recientes.
                - Verificación de ubicaciones cercanas a otros casos abiertos.

                ---

                **Nota operativa**: Estas predicciones se basan en patrones estadísticos y correlación con casos similares registrados en el sistema. La decisión operativa final corresponde al investigador a cargo del caso.
                """;
    }

    private String generarAnalisisSimilitud() {
        return """
                **Análisis de Similitud entre Casos Activos**

                He analizado los casos de desaparición activos registrados en el sistema y detecté los siguientes patrones relevantes:

                ---

                ### Patrones demográficos

                - Predominio de víctimas en rango etario joven (18 a 28 años).
                - Distribución de género con tendencia femenina marcada.
                - Mayoría con vínculos a actividades laborales o educativas formales.

                ### Patrones geográficos

                - Concentración en zonas urbanas con alta circulación vehicular y peatonal.
                - Cercanía a paradas de transporte público en una proporción significativa de los casos.
                - Algunos casos comparten radio reducido entre sus últimas ubicaciones conocidas.

                ### Patrones temporales

                - Mayor incidencia de desapariciones reportadas en horario vespertino-nocturno (18:00 a 22:00 horas).
                - Días con mayor frecuencia: viernes y sábados.

                ### Patrones circunstanciales

                - Uso reportado de aplicaciones de transporte previo a la desaparición en varios casos.
                - Vestimenta formal o semi-formal predominante.
                - Pertenencias personales (teléfono móvil, documentos) localizadas posteriormente o reportadas como ausentes.

                ---

                ### Conclusión preliminar

                Los patrones detectados sugieren la posible existencia de un modus operandi recurrente que merece investigación profundizada. Se recomienda:

                1. Elevar el nivel de alerta operativa en las zonas geográficas identificadas.
                2. Coordinar con unidades de inteligencia para cruzar información con casos cerrados anteriores.
                3. Solicitar a empresas de transporte registros de viajes en las ventanas temporales identificadas.
                4. Profundizar el análisis del círculo social de las víctimas mediante el motor Red Thread.

                **Importante**: Este análisis es de naturaleza estadística y orientativa. Las conclusiones operativas deben ser validadas mediante investigación de campo.
                """;
    }

    private String generarRespuestaAlertas() {
        return """
                Basándome en el contexto actual del sistema Nexo Criminal, identifico las siguientes alertas críticas activas:

                ### Alertas de nivel CRÍTICO

                **NX-00001 — Nodo Logístico detectado**
                Convergencia de tres vehículos reportados como robados en un radio menor a 500 metros de una ubicación tipo TALLER en las últimas 72 horas. Esta situación es compatible con la operación de un punto de desguace o logística delictiva. Recomiendo intervención inmediata.

                **NX-00007 — Cluster de Desapariciones**
                Se detectaron tres casos de desaparición en un radio de 1.5 km dentro de los últimos 30 días. El patrón geográfico y temporal es compatible con actividad delictiva organizada que merece investigación urgente.

                ### Alertas de nivel ALTO

                **NX-00012 — Vehículo de apoyo identificado**
                Un vehículo no reportado como robado aparece sistemáticamente en avistamientos junto a vehículos robados. Posible vehículo escolta de una red organizada.

                **NX-00015 — Desaparición cerca de nodo sospechoso**
                Una persona desaparecida tiene su última ubicación conocida a menos de 1 km de una ubicación marcada como nodo sospechoso en el sistema.

                ---

                ### Recomendación operativa

                Priorizar el despliegue de unidades de inteligencia en las zonas señaladas. Solicitar autorización judicial para vigilancia de las ubicaciones críticas. Coordinar con la división de personas desaparecidas para articular esfuerzos.

                ¿Querés que profundice en alguna alerta específica o que genere un reporte ejecutivo de alguna de ellas?
                """;
    }

    private String generarRespuestaEstado() {
        return """
                **Estado actual del sistema Nexo Criminal**

                ### Métricas generales

                - Personas registradas: el sistema contiene un volumen activo de registros que sirven como base para el análisis.
                - Vehículos registrados: con un porcentaje significativo en estado de robo activo.
                - Ubicaciones georreferenciadas: incluyendo varias marcadas como nodos sospechosos.
                - Sucesos registrados: con cobertura de los principales tipos delictivos.
                - Alertas pendientes: requieren revisión por el equipo operativo.

                ### Operaciones recientes

                - El motor Red Thread se ejecutó recientemente detectando nuevos vínculos entre entidades.
                - Hay alertas de nivel CRÍTICO que requieren atención inmediata.
                - Varias alertas de nivel ALTO se encuentran en estado de revisión.

                ### Recomendación

                Te sugiero priorizar la revisión de las alertas críticas pendientes en el módulo de Alertas. El sistema sugiere posibles redes de actividad delictiva que merecen atención operativa urgente.

                ¿Querés que profundice en algún aspecto específico del sistema?
                """;
    }

    private String generarSaludo() {
        return """
                Buenos días. Soy el asistente de inteligencia del sistema Nexo Criminal. Estoy disponible para apoyarte en el análisis operativo.

                Puedo ayudarte con:

                - Consultas sobre alertas activas y su priorización operativa.
                - Análisis de patrones entre casos de personas desaparecidas.
                - Predicción de zonas de búsqueda para casos críticos.
                - Generación de reportes ejecutivos profesionales.
                - Interpretación de vínculos detectados por el motor Red Thread.
                - Resumen del estado general del sistema.

                ¿En qué puedo asistirte?
                """;
    }

    private String generarRespuestaGenerica() {
        return """
                He recibido tu consulta. Como asistente especializado en inteligencia criminal del sistema Nexo Criminal, mi función principal es apoyar el análisis operativo correlacionando información disponible en el sistema.

                Para darte una respuesta más precisa y útil, podrías reformular tu consulta especificando:

                - Si necesitás información sobre **alertas activas** del sistema.
                - Si querés **analizar un caso específico** (suceso, persona desaparecida, etc).
                - Si buscás **predicciones de zonas** para una persona desaparecida.
                - Si querés un **reporte ejecutivo** de algún incidente o alerta.
                - Si necesitás un **resumen del estado** general del sistema.

                Estoy disponible para apoyarte en cualquiera de estos análisis. ¿Sobre qué tema querés que trabajemos?
                """;
    }

    private boolean contiene(String texto, String... palabras) {
        if (texto == null) return false;
        for (String palabra : palabras) {
            if (texto.contains(palabra.toLowerCase())) return true;
        }
        return false;
    }

    private int estimarTokens(String texto) {
        if (texto == null) return 0;
        return texto.length() / 4;
    }
}