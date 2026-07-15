package naranja.custodia_360.exception.strategy;

import naranja.custodia_360.exception.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.net.UnknownHostException;

@Component
public class NetworkExceptionStrategy implements ExceptionMappingStrategy {
    @Override
    public boolean canHandle(Throwable throwable) {
        return throwable instanceof UnknownHostException;
    }

    @Override
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        String message = "No se pudo establecer conexion con el proveedor de IA (Gemini). Verifique la conexion a internet del servidor.";
        return new ResponseEntity<>(new ErrorResponse(status.value(), status.getReasonPhrase(), message), status);
    }
}