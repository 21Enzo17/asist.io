package asist.io.exception.filters;

import org.springframework.http.HttpStatus;

/**
 * Excepción que representa un error 404 Not Found.
 * Se lanza cuando no se encuentra un recurso solicitado.
 */
public class NotFoundException extends HttpException {
    public NotFoundException(String cause) {
        super(cause, HttpStatus.NOT_FOUND);
    }
}
