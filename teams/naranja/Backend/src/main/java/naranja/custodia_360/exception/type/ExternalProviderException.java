package naranja.custodia_360.exception.type;

import org.springframework.http.HttpStatus;

public class ExternalProviderException extends RuntimeException {
    private final HttpStatus status;

    public ExternalProviderException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
