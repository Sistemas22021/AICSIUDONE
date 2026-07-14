package com.guardia.core.service;

import com.guardia.core.dto.ai.AnalisisMoIA;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.ExpedienteSimilarMO;
import com.guardia.core.model.PropuestaModusOperandi;
import com.guardia.core.model.enums.EstadoPropuestaMO;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.PropuestaModusOperandiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
/**
 * Implementación del pipeline de detección automática de Modus Operandi (HU2).
 *
 * <p>Deliberadamente NO se anota la orquestación completa con
 * {@code @Transactional}: cada paso (guardar embedding, guardar propuesta)
 * usa su propia transacción corta a través de los métodos de
 * {@code JpaRepository}, para no mantener una conexión de base de datos
 * abierta durante la llamada HTTP —potencialmente lenta— al modelo de OpenAI.</p>
 */
public class DeteccionModusOperandiServiceImpl implements DeteccionModusOperandiService {

    private static final Logger log = LoggerFactory.getLogger(DeteccionModusOperandiServiceImpl.class);

    private final ExpedienteRepository expedienteRepository;
    private final PropuestaModusOperandiRepository propuestaRepository;
    private final EmbeddingModel embeddingModel;
    private final ChatClient chatClient;

    @Value("${zac.mo.top-k:5}")
    private int topK;

    @Value("${zac.mo.umbral-similitud-candidato:70.0}")
    private double umbralSimilitudCandidato;

    @Value("${spring.ai.ollama.embedding.options.model:nomic-embed-text}")
    private String nombreModeloEmbedding;

    @Value("${spring.ai.ollama.chat.options.model:llama3.2}")
    private String nombreModeloChat;

    public DeteccionModusOperandiServiceImpl(ExpedienteRepository expedienteRepository,
                                             PropuestaModusOperandiRepository propuestaRepository,
                                             EmbeddingModel embeddingModel,
                                             ChatModel chatModel) {
        this.expedienteRepository = expedienteRepository;
        this.propuestaRepository = propuestaRepository;
        this.embeddingModel = embeddingModel;
        this.chatClient = ChatClient.create(chatModel);
    }

    @Override
    @Async
    public void analizarPatrones(Long expedienteId) {
        log.info("[MO] Iniciando análisis para expediente {}", expedienteId);

        try {
            Expediente expediente = expedienteRepository.findById(expedienteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Expediente", expedienteId));

            if (expediente.getDescripcionHecho() == null || expediente.getDescripcionHecho().isBlank()) {
                log.warn("[MO] Expediente {} no tiene descripcionHecho; se omite el análisis de MO.",
                        expediente.getFolio());
                return;
            }

            // CA1: generar y persistir el embedding de la descripción del hecho.
            float[] embedding = embeddingModel.embed(expediente.getDescripcionHecho());
            expediente.setEmbedding(embedding);
            expedienteRepository.save(expediente);

            // CA de HU3: un MO ya revisado por un experto no puede ser sobreescrito
            // automáticamente por una nueva corrida del análisis.
            PropuestaModusOperandi vigenteActual = propuestaRepository
                    .findByExpedienteIdAndVigenteTrue(expedienteId)
                    .orElse(null);
            if (vigenteActual != null && vigenteActual.isRevisadoPorExperto()) {
                log.info("[MO] Expediente {} ya tiene un MO revisado por experto; no se regenera.",
                        expediente.getFolio());
                return;
            }

            // CA2: comparar el embedding contra expedientes anteriores y recuperar los más similares.
            List<Object[]> candidatos = expedienteRepository.buscarSimilaresPorEmbedding(
                    expedienteId, embedding, PageRequest.of(0, topK));

            List<ExpedienteSimilarMO> similares = candidatos.stream()
                    .map(fila -> {
                        Expediente similar = (Expediente) fila[0];
                        double distancia = ((Number) fila[1]).doubleValue();
                        return new ExpedienteSimilarMO(similar.getId(), similar.getFolio(),
                                similitudPorcentaje(distancia));
                    })
                    .filter(s -> s.getSimilitudPorcentaje() >= umbralSimilitudCandidato)
                    .toList();

            int siguienteVersion = vigenteActual == null ? 1 : vigenteActual.getVersion() + 1;
            if (vigenteActual != null) {
                vigenteActual.setVigente(false);
                propuestaRepository.save(vigenteActual);
            }

            // CA6: si no hay coincidencias suficientes, registrar "MO sin coincidencias previas".
            if (similares.isEmpty()) {
                PropuestaModusOperandi sinCoincidencias = PropuestaModusOperandi.builder()
                        .expediente(expediente)
                        .version(siguienteVersion)
                        .vigente(true)
                        .estado(EstadoPropuestaMO.SIN_COINCIDENCIAS)
                        .resumenGenerado("MO sin coincidencias previas")
                        .nivelConfianza(0.0)
                        .modeloEmbedding(nombreModeloEmbedding)
                        .fechaGeneracion(LocalDateTime.now())
                        .revisadoPorExperto(false)
                        .build();
                propuestaRepository.save(sinCoincidencias);
                log.info("[MO] Expediente {}: sin coincidencias previas.", expediente.getFolio());
                return;
            }

            // CA3-CA4: consultar al LLM qué tienen en común los casos similares.
            AnalisisMoIA resultadoIA = generarAnalisisConIA(expediente, similares);

            PropuestaModusOperandi propuesta = PropuestaModusOperandi.builder()
                    .expediente(expediente)
                    .version(siguienteVersion)
                    .vigente(true)
                    .estado(EstadoPropuestaMO.PENDIENTE)
                    .caracteristicasComunes(resultadoIA.caracteristicasComunes())
                    .posibleFirma(resultadoIA.posibleFirma())
                    .consistenciaHorarioZona(resultadoIA.consistenciaHorarioZona())
                    .resumenGenerado(resultadoIA.resumen())
                    .nivelConfianza(resultadoIA.nivelConfianza())
                    .modeloEmbedding(nombreModeloEmbedding)
                    .modeloChat(nombreModeloChat)
                    .fechaGeneracion(LocalDateTime.now())
                    .revisadoPorExperto(false)
                    .expedientesSimilares(similares)
                    .build();
            propuestaRepository.save(propuesta);

            log.info("[MO] Expediente {}: propuesta generada con {} casos similares y confianza {}.",
                    expediente.getFolio(), similares.size(), resultadoIA.nivelConfianza());

            // NOTA: la generación de la alerta interna (HU6) se conecta aquí en una
            // iteración futura, escuchando la creación de una PropuestaModusOperandi
            // con >=2 similares y nivelConfianza por encima del umbral. Fuera de
            // alcance de esta entrega (HU2/HU3).

        } catch (Exception ex) {
            log.error("[MO] Error analizando Modus Operandi para expediente id={}", expedienteId, ex);
        }
    }

    @Override
    public double compararExpedientes(Expediente a, Expediente b) {
        if (a == null || b == null || a.getId() == null || b.getId() == null) {
            return 0.0;
        }
        Double distancia = expedienteRepository.calcularDistanciaCoseno(a.getId(), b.getId());
        return distancia == null ? 0.0 : similitudPorcentaje(distancia);
    }

    private AnalisisMoIA generarAnalisisConIA(Expediente expediente, List<ExpedienteSimilarMO> similares) {
        String contextoCasosSimilares = similares.stream()
                .map(s -> "Folio %s (similitud %.1f%%)".formatted(s.getFolio(), s.getSimilitudPorcentaje()))
                .collect(java.util.stream.Collectors.joining("\n"));

        String prompt = """
            Eres un analista criminal experto en identificación de patrones de Modus Operandi (MO).

            Caso nuevo (folio %s):
            %s

            Casos previos recuperados por búsqueda semántica, por nivel de similitud:
            %s

            Analiza qué características tienen en común estos casos: forma de actuar, posible firma
            del perpetrador, y consistencia de horario o zona geográfica. Estima un nivel de confianza
            de 0 a 100 sobre qué tan sólido es el patrón encontrado.

            ⚠️ IMPORTANTE: Responde ÚNICAMENTE con un objeto JSON válido y completo. NO agregues texto adicional.
            El JSON debe tener esta estructura exacta:
            {
              "caracteristicasComunes": "texto",
              "posibleFirma": "texto",
              "consistenciaHorarioZona": "texto",
              "resumen": "texto",
              "nivelConfianza": 75.0
            }
            Asegúrate de cerrar todas las llaves y comillas correctamente.
            """.formatted(expediente.getFolio(), expediente.getDescripcionHecho(), contextoCasosSimilares);

        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .entity(AnalisisMoIA.class);
        } catch (Exception e) {
            log.error("[MO] Error al llamar a Ollama: {}", e.getMessage());
            // Retornar un análisis por defecto
            return new AnalisisMoIA(
                    "No se pudo generar el análisis automáticamente",
                    "No determinado",
                    "No determinado",
                    "Error en la generación del análisis",
                    0.0
            );
        }
    }

    /** Convierte distancia coseno pgvector (0=idénticos, 2=opuestos) a similitud % acotada [0,100]. */
    private double similitudPorcentaje(double distanciaCoseno) {
        double similitud = (1 - distanciaCoseno) * 100;
        return Math.max(0.0, Math.min(100.0, similitud));
    }
}