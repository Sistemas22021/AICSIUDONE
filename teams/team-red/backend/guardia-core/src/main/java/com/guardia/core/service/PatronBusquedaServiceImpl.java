package com.guardia.core.service;

import com.guardia.core.dto.request.PatronBusquedaRequest;
import com.guardia.core.dto.response.PatronBusquedaResultado;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.FirmaConductual;
import com.guardia.core.model.PropuestaModusOperandi;
import com.guardia.core.model.enums.EstadoPropuestaMO;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.FirmaConductualRepository;
import com.guardia.core.repository.PropuestaModusOperandiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
/**
 * Implementación de la búsqueda de patrones (HU "Buscar patrones por MO y
 * firma conductual"). Genera el embedding del/los texto(s) de búsqueda y
 * consulta, vía pgvector (cosine_distance), el índice vectorial de MO
 * validado y/o de firma conductual, combinando los resultados cuando ambos
 * criterios están presentes.
 */
public class PatronBusquedaServiceImpl implements PatronBusquedaService {

    /** Sólo estos dos estados representan un MO realmente validado por un experto (CA1). */
    private static final List<EstadoPropuestaMO> ESTADOS_MO_VALIDADO =
            List.of(EstadoPropuestaMO.APROBADA, EstadoPropuestaMO.CORREGIDA);

    private final PropuestaModusOperandiRepository propuestaRepository;
    private final FirmaConductualRepository firmaConductualRepository;
    private final ExpedienteRepository expedienteRepository;
    private final EmbeddingModel embeddingModel;

    /** Candidatos recuperados por cada criterio antes de combinar/limitar (CA6: rendimiento). */
    @Value("${zac.patrones.top-k-por-criterio:50}")
    private int topKPorCriterio;

    /** Tope máximo de resultados que el cliente puede solicitar con "limite". */
    @Value("${zac.patrones.limite-maximo:100}")
    private int limiteMaximo;

    @Override
    public List<PatronBusquedaResultado> buscar(PatronBusquedaRequest request) {
        String textoMO = limpiar(request.textoMO());
        String textoFirma = limpiar(request.textoFirmaConductual());

        // CA1: al menos un criterio de búsqueda es obligatorio.
        if (textoMO == null && textoFirma == null) {
            throw new BusinessException(
                    "Debe indicar al menos un criterio de búsqueda: MO validado, firma conductual, o ambos.");
        }

        int limite = (request.limite() == null || request.limite() <= 0)
                ? 20
                : Math.min(request.limite(), limiteMaximo);

        Map<Long, Double> puntajesMO = textoMO == null ? Map.of() : buscarPorMOValidado(textoMO);
        Map<Long, Double> puntajesFirma = textoFirma == null ? Map.of() : buscarPorFirmaConductual(textoFirma);

        Map<Long, Double> combinados = combinarPuntajes(puntajesMO, puntajesFirma);

        // CA4: ordenar de forma descendente por similitud, antes de aplicar el límite.
        List<Long> idsOrdenados = combinados.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limite)
                .map(Map.Entry::getKey)
                .toList();

        if (idsOrdenados.isEmpty()) {
            return List.of();
        }

        Map<Long, Expediente> expedientesPorId = expedienteRepository.findAllById(idsOrdenados).stream()
                .collect(Collectors.toMap(Expediente::getId, e -> e));

        return idsOrdenados.stream()
                .map(id -> expedientesPorId.get(id))
                .filter(Objects::nonNull)
                .map(e -> toResultado(e, combinados.get(e.getId())))
                .toList();
    }

    /** CA1/CA2: búsqueda semántica sobre el MO ya validado (APROBADA/CORREGIDA). */
    private Map<Long, Double> buscarPorMOValidado(String texto) {
        float[] embedding = embeddingModel.embed(texto);
        List<Object[]> filas = propuestaRepository.buscarPorEmbeddingMOValidado(
                embedding, ESTADOS_MO_VALIDADO, PageRequest.of(0, topKPorCriterio));

        Map<Long, Double> puntajes = new LinkedHashMap<>();
        for (Object[] fila : filas) {
            PropuestaModusOperandi propuesta = (PropuestaModusOperandi) fila[0];
            double distancia = ((Number) fila[1]).doubleValue();
            if (propuesta.getExpediente() != null) {
                puntajes.put(propuesta.getExpediente().getId(), similitudPorcentaje(distancia));
            }
        }
        return puntajes;
    }

    /** CA1/CA2: búsqueda semántica sobre la firma conductual vigente de cada expediente. */
    private Map<Long, Double> buscarPorFirmaConductual(String texto) {
        float[] embedding = embeddingModel.embed(texto);
        List<Object[]> filas = firmaConductualRepository.buscarPorEmbedding(
                embedding, PageRequest.of(0, topKPorCriterio));

        Map<Long, Double> puntajes = new LinkedHashMap<>();
        for (Object[] fila : filas) {
            FirmaConductual firma = (FirmaConductual) fila[0];
            double distancia = ((Number) fila[1]).doubleValue();
            if (firma.getExpediente() != null) {
                puntajes.put(firma.getExpediente().getId(), similitudPorcentaje(distancia));
            }
        }
        return puntajes;
    }

    /**
     * Combina los puntajes de ambos criterios (CA1: "ambos combinados"). Si un
     * expediente aparece en los dos conjuntos, se promedia con peso igual; si
     * sólo aparece en uno (porque el otro criterio no fue solicitado, o el
     * expediente no tiene ese dato indexado todavía), se usa el puntaje
     * disponible tal cual, para no penalizar coincidencias fuertes en un solo
     * criterio.
     */
    private Map<Long, Double> combinarPuntajes(Map<Long, Double> puntajesMO, Map<Long, Double> puntajesFirma) {
        Set<Long> todosLosIds = new LinkedHashSet<>();
        todosLosIds.addAll(puntajesMO.keySet());
        todosLosIds.addAll(puntajesFirma.keySet());

        Map<Long, Double> combinados = new LinkedHashMap<>();
        for (Long id : todosLosIds) {
            Double puntajeMO = puntajesMO.get(id);
            Double puntajeFirma = puntajesFirma.get(id);
            double puntajeFinal = (puntajeMO != null && puntajeFirma != null)
                    ? (puntajeMO + puntajeFirma) / 2.0
                    : (puntajeMO != null ? puntajeMO : puntajeFirma);
            combinados.put(id, puntajeFinal);
        }
        return combinados;
    }

    /** Convierte distancia coseno pgvector (0=idénticos, 2=opuestos) a similitud % acotada [0,100]. */
    private double similitudPorcentaje(double distanciaCoseno) {
        double similitud = (1 - distanciaCoseno) * 100;
        return Math.max(0.0, Math.min(100.0, similitud));
    }

    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

    private PatronBusquedaResultado toResultado(Expediente e, double similitud) {
        return new PatronBusquedaResultado(
                e.getId(),
                e.getFolio(),
                e.getTipoDelito() != null ? e.getTipoDelito().getNombre() : null,
                e.getFechaHecho(),
                Math.round(similitud * 10.0) / 10.0,
                e.getCreadoPor() != null ? e.getCreadoPor().getFullName() : "Sin asignar");
    }
}