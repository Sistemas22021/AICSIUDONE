package naranja.custodia_360.exception.strategy;

import com.google.genai.errors.ClientException;
import naranja.custodia_360.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GeminiClientExceptionStrategy implements ExceptionMappingStrategy {
    @Override
    public boolean canHandle(Throwable throwable) {
        return throwable instanceof ClientException;
    }

    @Override
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        ClientException ex = (ClientException) throwable;
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Error en la configuracion del servicio de IA.";

        if (ex.getMessage() != null && ex.getMessage().contains("API key not valid")) {
            status = HttpStatus.UNAUTHORIZED;
            message = "La credencial de acceso (API Key) para el servicio de Gemini es invalida o ha expirado.";
        }

        return new ResponseEntity<>(new ErrorResponse(status.value(), status.getReasonPhrase(), message), status);
    }
}