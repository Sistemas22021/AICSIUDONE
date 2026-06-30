package naranja.custodia_360.exception.strategy;

import naranja.custodia_360.exception.dto.ErrorResponse;
import naranja.custodia_360.exception.type.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BadRequestExceptionStrategy implements ExceptionMappingStrategy {

    @Override
    public boolean canHandle(Throwable throwable) {
        return throwable instanceof BadRequestException;
    }

    @Override
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        BadRequestException ex = (BadRequestException) throwable;
        return new ResponseEntity<>(
                new ErrorResponse(ex.getStatus().value(), ex.getStatus().getReasonPhrase(), ex.getMessage()),
                ex.getStatus()
        );
    }
}