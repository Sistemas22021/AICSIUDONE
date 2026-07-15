package naranja.custodia_360.exception.strategy;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import naranja.custodia_360.exception.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AudioStreamExceptionStrategy implements ExceptionMappingStrategy {

    private static final Logger log = LoggerFactory.getLogger(AudioStreamExceptionStrategy.class);

    @Override
    public boolean canHandle(Throwable throwable) {
        return throwable instanceof ClientAbortException || throwable instanceof AsyncRequestNotUsableException;
    }

    @Override
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        log.warn("El cliente interrumpió la reproducción o descarga del audio de forma abrupta: {}", throwable.getMessage());
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).build();
    }
}