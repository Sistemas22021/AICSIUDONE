package naranja.custodia_360.exception.strategy;

import naranja.custodia_360.exception.dto.ErrorResponse;
import naranja.custodia_360.exception.type.ExternalProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ExternalProviderExceptionStrategy implements ExceptionMappingStrategy {

    @Override
    public boolean canHandle(Throwable throwable) {
        return throwable instanceof ExternalProviderException;
    }

    @Override
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        ExternalProviderException ex = (ExternalProviderException) throwable;

        HttpStatus status = ex.getStatus();

        return new ResponseEntity<>(
                new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage()),
                status
        );
    }
}