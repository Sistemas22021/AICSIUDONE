package naranja.custodia_360.exception.strategy;

import naranja.custodia_360.exception.dto.ErrorResponse;
import naranja.custodia_360.exception.type.StorageException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class StorageExceptionStrategy implements ExceptionMappingStrategy {

    @Override
    public boolean canHandle(Throwable throwable) {
        // Una sola línea limpia gracias a tu enfoque. Cero strings cableados.
        return throwable instanceof StorageException;
    }

    @Override
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        StorageException ex = (StorageException) throwable;

        return new ResponseEntity<>(
                new ErrorResponse(ex.getStatus().value(), ex.getStatus().getReasonPhrase(), ex.getMessage()),
                ex.getStatus()
        );
    }
}