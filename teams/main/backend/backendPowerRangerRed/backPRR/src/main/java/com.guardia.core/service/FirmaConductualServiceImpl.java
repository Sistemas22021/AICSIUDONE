package com.guardia.core.service;

import com.guardia.core.dto.request.ActualizarFirmaConductualRequest;
import com.guardia.core.dto.request.RegistrarFirmaConductualRequest;
import com.guardia.core.dto.response.FirmaConductualResponse;
import com.guardia.core.exception.BusinessException;
import com.guardia.core.exception.ResourceNotFoundException;
import com.guardia.core.model.Expediente;
import com.guardia.core.model.FirmaConductual;
import com.guardia.core.model.Usuario;
import com.guardia.core.repository.ExpedienteRepository;
import com.guardia.core.repository.FirmaConductualRepository;
import com.guardia.core.repository.UsuarioRepository;
import com.guardia.core.service.FirmaConductualService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de Firma Conductual.
 *
 * Proporciona funcionalidades para registrar, editar, consultar y mantener
 * historiales de las firmas conductuales asociadas a expedientes.
 *
 * Una firma conductual es un registro que documenta patrones de comportamiento
 * pre-delictivo, métodos de aproximación y ataque, comportamiento post-delictivo
 * y elementos distintivos de un sospechoso o delincuente.
 *
 * @author Team Ranger
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class FirmaConductualServiceImpl implements FirmaConductualService {

    private final FirmaConductualRepository firmaRepository;
    private final ExpedienteRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Registra una nueva firma conductual para un expediente específico.
     *
     * Este método crea un nuevo registro de firma conductual con la información
     * proporcionada en la solicitud. Valida que al menos un campo sea completado,
     * verifica la existencia del expediente y el analista responsable, y asigna
     * automáticamente un número de versión (comenzando desde 1).
     *
     * @param request Objeto {@link RegistrarFirmaConductualRequest} que contiene:
     *        - expedienteId: ID del expediente asociado
     *        - analistaId: ID del usuario analista responsable
     *        - comportamientoPreDelictivo: Descripción del comportamiento previo al delito
     *        - metodoAproximacion: Método utilizado para acercarse a la víctima
     *        - metodoAtaque: Estrategia utilizada para cometer el delito
     *        - comportamientoPostDelictivo: Conducta después de cometer el delito
     *        - elementosDistintivos: Características o marcas distintivas del perpetrador
     *
     * @return {@link "FirmaConductualResponse} con los datos de la firma registrada,
     *         incluyendo ID generado, versión y fecha de registro
     *
     * @throws ResourceNotFoundException si el expediente o el analista no existen
     * @throws BusinessException si ninguno de los campos de la firma es completado
     */
    @Override
    public FirmaConductualResponse registrar(RegistrarFirmaConductualRequest request) {

        validarCamposRequest(request);

        Expediente expediente = expedienteRepository.findById(request.getExpedienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Expediente", request.getExpedienteId()));

        Usuario analista = usuarioRepository.findById(request.getAnalistaId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", request.getAnalistaId()));

        // Si ya existe una firma vigente para este expediente, se marca como histórica
        // antes de registrar la nueva versión (mismo mecanismo de versionado que editar()).
        int version = 1;
        Optional<FirmaConductual> vigenteActual =
                firmaRepository.findByExpedienteIdAndVigenteTrue(expediente.getId());
        if (vigenteActual.isPresent()) {
            FirmaConductual anterior = vigenteActual.get();
            anterior.marcarHistorica();
            firmaRepository.save(anterior);
            version = anterior.getVersion() + 1;
        }

        FirmaConductual firma = FirmaConductual.builder()
                .expediente(expediente)
                .analista(analista)
                .comportamientoPreDelictivo(request.getComportamientoPreDelictivo())
                .metodoAproximacion(request.getMetodoAproximacion())
                .metodoAtaque(request.getMetodoAtaque())
                .comportamientoPostDelictivo(request.getComportamientoPostDelictivo())
                .elementosDistintivos(request.getElementosDistintivos())
                .fechaRegistro(LocalDateTime.now())
                .version(version)
                .vigente(true)
                .build();

        firmaRepository.save(firma);

        return convertirResponse(firma);
    }

    /**
     * Actualiza una firma conductual existente.
     *
     * Este método implementa un sistema de versionado donde la firma actual
     * se marca como histórica (no vigente) y se crea una nueva versión con
     * los datos actualizados. Esto permite mantener un historial completo
     * de cambios en la firma conductual.
     *
     * El analista y expediente permanecen iguales, solo se actualizan
     * los campos de comportamiento y elementos distintivos.
     *
     * @param firmaId ID de la firma conductual a editar (debe ser vigente)
     * @param request Objeto {@link ActualizarFirmaConductualRequest} que contiene:
     *        - comportamientoPreDelictivo: Nuevo comportamiento pre-delictivo
     *        - metodoAproximacion: Nuevo método de aproximación
     *        - metodoAtaque: Nuevo método de ataque
     *        - comportamientoPostDelictivo: Nuevo comportamiento post-delictivo
     *        - elementosDistintivos: Nuevos elementos distintivos
     *
     * @return {@link FirmaConductualResponse} con los datos de la nueva versión,
     *         con número de versión incrementado y marcada como vigente
     *
     * @throws ResourceNotFoundException si la firma no existe
     *
     * @see #registrar(RegistrarFirmaConductualRequest)
     */
    @Override
    public FirmaConductualResponse editar(Long firmaId,
                                          ActualizarFirmaConductualRequest request) {

        FirmaConductual actual = firmaRepository.findById(firmaId)
                .orElseThrow(() -> new ResourceNotFoundException("FirmaConductual", firmaId));

        actual.marcarHistorica();

        firmaRepository.save(actual);

        FirmaConductual nueva = FirmaConductual.builder()
                .expediente(actual.getExpediente())
                .analista(actual.getAnalista())
                .comportamientoPreDelictivo(request.getComportamientoPreDelictivo())
                .metodoAproximacion(request.getMetodoAproximacion())
                .metodoAtaque(request.getMetodoAtaque())
                .comportamientoPostDelictivo(request.getComportamientoPostDelictivo())
                .elementosDistintivos(request.getElementosDistintivos())
                .fechaRegistro(LocalDateTime.now())
                .version(actual.getVersion() + 1)
                .vigente(true)
                .build();

        firmaRepository.save(nueva);

        return convertirResponse(nueva);
    }

    /**
     * Obtiene la firma conductual vigente de un expediente.
     *
     * Este método recupera la versión actual (vigente) de la firma conductual
     * asociada a un expediente específico. Solo devuelve la firma que está
     * marcada como vigente, ignorando versiones históricas anteriores.
     *
     * @param expedienteId ID del expediente del cual se desea obtener la firma
     *
     * @return {@link FirmaConductualResponse} con los datos de la firma vigente
     *
     * @throws ResourceNotFoundException si no existe firma vigente para el expediente
     *
     * @see #obtenerHistorial(Long)
     */
    @Override
    public FirmaConductualResponse obtenerActual(Long expedienteId) {

        FirmaConductual firma = firmaRepository
                .findByExpedienteIdAndVigenteTrue(expedienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe una firma conductual registrada para el expediente indicado."));

        return convertirResponse(firma);
    }

    /**
     * Obtiene el historial completo de firmas conductuales de un expediente.
     *
     * Este método devuelve todas las versiones de las firmas conductuales
     * registradas para un expediente, ordenadas de mayor a menor versión
     * (la más reciente primero). Incluye tanto firmas vigentes como históricas.
     *
     * @param expedienteId ID del expediente del cual se desea obtener el historial
     *
     * @return Lista de {@link FirmaConductualResponse} ordenadas por versión
     *         descendente. Retorna una lista vacía si no existen firmas
     *
     * @see #obtenerActual(Long)
     */
    @Override
    public List<FirmaConductualResponse> obtenerHistorial(Long expedienteId) {

        return firmaRepository
                .findByExpedienteIdOrderByVersionDesc(expedienteId)
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    /**
     * Verifica si existe una firma conductual vigente para un expediente.
     *
     * Realiza una verificación rápida sin recuperar toda la información
     * de la firma. Útil para validaciones previas o para determinar si
     * es necesario crear o actualizar la firma de un expediente.
     *
     * @param expedienteId ID del expediente a verificar
     *
     * @return {@code true} si existe una firma vigente para el expediente,
     *         {@code false} en caso contrario
     */
    @Override
    public boolean existeFirma(Long expedienteId) {
        return firmaRepository
                .findByExpedienteIdAndVigenteTrue(expedienteId)
                .isPresent();
    }

    // =======================
    // Métodos privados
    // =======================

    /**
     * Valida los campos de la solicitud de registro de firma conductual.
     * Verifica que al menos uno de los campos de la firma conductual contenga
     * información válida (no nula ni vacía). Esta validación es obligatoria
     * para evitar registros incompletos sin datos significativos.
     *
     * @param "request Objeto {@link "RegistrarFirmaConductualRequest} a validar
     *
     * @throws BusinessException si todos los campos de comportamiento y elementos
     *                          distintivos están vacíos o nulos
     *
     * @see "#esVacio(String)"
     * */
    private void validarCamposRequest(RegistrarFirmaConductualRequest request) {

        boolean vacio =
                esVacio(request.getComportamientoPreDelictivo()) &&
                        esVacio(request.getMetodoAproximacion()) &&
                        esVacio(request.getMetodoAtaque()) &&
                        esVacio(request.getComportamientoPostDelictivo()) &&
                        esVacio(request.getElementosDistintivos());

        if (vacio) {
            throw new BusinessException(
                    "Debe registrar al menos un elemento de la firma conductual.");
        }
    }

    /**
     * Verifica si una cadena de texto está vacía o nula.
     *
     * Una cadena se considera vacía si es {@code null} o contiene solo
     * espacios en blanco. Este método se utiliza para validar campos
     * opcionales en la firma conductual.
     *
     * @param valor Cadena de texto a validar
     *
     * @return {@code true} si el valor es null o está en blanco,
     *         {@code false} si contiene texto válido
     */
    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    /**
     * Convierte una entidad {@link .FirmaConductual} a su representación de respuesta.
     *
     * Este método realiza la conversión entre la entidad JPA interna y el DTO
     * de respuesta que se devuelve a los clientes. Extrae todos los datos relevantes
     * de la firma conductual y sus referencias relacionadas (expediente y analista).
     *
     * @param firma Entidad {@link .FirmaConductual} a convertir
     *
     * @return {@link .FirmaConductualResponse} con los datos formateados
     *         para la respuesta HTTP, incluyendo:
     *         - ID de la firma y del expediente
     *         - ID del analista responsable
     *         - Todos los campos de comportamiento y elementos distintivos
     *         - Metadata (versión, vigencia, fecha de registro)
     *
     * @throws NullPointerException si firma es null o si sus referencias
     *                              (expediente o analista) son null
     */
    private FirmaConductualResponse convertirResponse(FirmaConductual firma) {

        return FirmaConductualResponse.builder()
                .id(firma.getId())
                .expedienteId(firma.getExpediente().getId())
                .analistaId(firma.getAnalista().getId())
                .version(firma.getVersion())
                .vigente(firma.getVigente())
                .fechaRegistro(firma.getFechaRegistro())
                .comportamientoPreDelictivo(firma.getComportamientoPreDelictivo())
                .metodoAproximacion(firma.getMetodoAproximacion())
                .metodoAtaque(firma.getMetodoAtaque())
                .comportamientoPostDelictivo(firma.getComportamientoPostDelictivo())
                .elementosDistintivos(firma.getElementosDistintivos())
                .build();
    }

}