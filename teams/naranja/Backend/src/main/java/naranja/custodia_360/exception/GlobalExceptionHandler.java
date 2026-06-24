package naranja.custodia_360.exception;

import com.google.genai.errors.ClientException;
import naranja.custodia_360.exception.dto.ErrorResponse;
import naranja.custodia_360.exception.type.ExternalProviderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.UnknownHostException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Intercepta las excepciones en tiempo de ejecucion genéricas que lanza Spring AI
     * para extraer la causa real si proviene de Gemini.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        Throwable rootCause = ex;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }

        // Si la causa raíz real es el error de credenciales de Google
        if (rootCause instanceof ClientException clientEx) {
            return handleGoogleClientException(clientEx);
        }

        // Si la causa raíz real es que se fue el internet
        if (rootCause instanceof UnknownHostException unknownHostEx) {
            return handleUnknownHostException(unknownHostEx);
        }

        // Si es cualquier otra RuntimeException que no nos interesa, la dejamos pasar a la red de seguridad
        throw ex;
    }

    /**
     * Caso 1: Captura la falta de internet o caida del servidor de Google.
     * Intercepta UnknownHostException cuando no se puede resolver el dominio de Gemini.
     */
    @ExceptionHandler(UnknownHostException.class)
    public ResponseEntity<ErrorResponse> handleUnknownHostException(UnknownHostException ex) {
        HttpStatus status = HttpStatus.BAD_GATEWAY; // Codigo 502: El servidor externo no es alcanzable
        return buildResponse(status, "No se pudo establecer conexion con el proveedor de IA (Gemini). Verifique la conexion a internet del servidor.");
    }

    /**
     * Caso 2: Captura errores directos del SDK de Google GenAI (como la API Key invalida).
     */
    @ExceptionHandler(ClientException.class)
    public ResponseEntity<ErrorResponse> handleGoogleClientException(ClientException ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Codigo 500
        String userMessage = "Error en la configuracion del servicio de IA.";

        // Analizamos el mensaje de la consola para dar una respuesta semantica
        if (ex.getMessage() != null && ex.getMessage().contains("API key not valid")) {
            status = HttpStatus.UNAUTHORIZED; // Codigo 401
            userMessage = "La credencial de acceso (API Key) para el servicio de Gemini es invalida o ha expirado.";
        }

        return buildResponse(status, userMessage);
    }

    /**
     * Captura nuestra excepcion personalizada en caso de que manejemos el error manualmente en el service.
     */
    @ExceptionHandler(ExternalProviderException.class)
    public ResponseEntity<ErrorResponse> handleExternalProviderException(ExternalProviderException ex) {
        return buildResponse(ex.getStatus(), ex.getMessage());
    }

    /**
     * Red de seguridad para cualquier otro error no mapeado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        log.error("Ocurrio un error no controlado en el backend: ", ex);

        return buildResponse(status, "Ocurrio un error inesperado en el servidor. Por favor, intente mas tarde.");
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message
        );
        return new ResponseEntity<>(errorResponse, status);
    }

}