package naranja.custodia_360.exception;

import com.google.genai.errors.ClientException;
import naranja.custodia_360.exception.dto.ErrorResponse;
import naranja.custodia_360.exception.strategy.ExceptionMappingStrategy;
import naranja.custodia_360.exception.type.ExternalProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.net.UnknownHostException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final List<ExceptionMappingStrategy> strategies;

    // Spring inyecta automaticamente todas las clases que implementen ExceptionMappingStrategy
    public GlobalExceptionHandler(List<ExceptionMappingStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Capturador unificado para RuntimeException. Desempaqueta y busca una estrategia que aplique.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {

        // 1. Intentamos verificar si existe una estrategia para la excepción tal y como llegó
        for (ExceptionMappingStrategy strategy : strategies) {
            if (strategy.canHandle(ex)) {
                return strategy.handle(ex);
            }
        }

        // 2. Si no, buscamos la causa raíz profunda (para los envoltorios de Spring AI)
        Throwable rootCause = ex;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }

        // 3. Evaluamos si hay una estrategia para esa causa raíz
        for (ExceptionMappingStrategy strategy : strategies) {
            if (strategy.canHandle(rootCause)) {
                return strategy.handle(rootCause);
            }
        }

        // Si ninguna estrategia de nuestro sistema la reconoce, va a la red de seguridad final (500)
        throw ex;
    }

    /**
     * Mantenemos este aqui de forma directa por ser una excepcion nativa del ciclo de Spring Web MVC
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String userMessage = String.format("Falta un componente requerido en la peticion: '%s'", ex.getRequestPartName());
        return new ResponseEntity<>(new ErrorResponse(status.value(), status.getReasonPhrase(), userMessage), status);
    }

    /**
     * Red de seguridad final para cualquier desastre no controlado
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception ex) {
        log.error("Ocurrio un error no controlado en el backend: ", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return new ResponseEntity<>(new ErrorResponse(status.value(), status.getReasonPhrase(),
                "Ocurrio un error inesperado en el servidor. Por favor, intente mas tarde."), status);
    }

}