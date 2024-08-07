package asist.io.exception.filters;

import org.springframework.http.HttpStatus;

/**
 * Excepción que representa un error 400 Bad Request.
 * Se lanza cuando la solicitud del cliente no puede ser
 * procesada por el servidor debido a un error en la solicitud.
 */
public class BadRequestException extends HttpException {
    public BadRequestException(String cause) {
        super(cause, HttpStatus.BAD_REQUEST);
    }
}
