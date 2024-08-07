package asist.io.exception.filters;

import org.springframework.http.HttpStatus;

/**
 * Excepción que representa un error 403 Forbidden.
 * Se lanza cuando el servidor entiende la solicitud, pero se niega a responderla.
 */
public class ForbiddenException extends HttpException {
    public ForbiddenException(String cause) {
        super(cause, HttpStatus.FORBIDDEN);
    }
}
