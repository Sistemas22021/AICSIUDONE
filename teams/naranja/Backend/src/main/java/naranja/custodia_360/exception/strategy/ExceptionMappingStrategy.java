package naranja.custodia_360.exception.strategy;

import naranja.custodia_360.exception.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;

public interface ExceptionMappingStrategy {

    // Define si esta estrategia sabe cómo manejar esta excepción específica o su causa raíz
    boolean canHandle(Throwable throwable);

    // Construye la respuesta JSON estructurada con el código de estado correcto
    ResponseEntity<ErrorResponse> handle(Throwable throwable);
}