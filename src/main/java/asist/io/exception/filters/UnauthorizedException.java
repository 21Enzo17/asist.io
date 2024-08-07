package asist.io.exception.filters;

import org.springframework.http.HttpStatus;

/**
 * Excepción que representa un error de autenticación.
 * Se lanza cuando un usuario no tiene permisos para acceder a un recurso.
 */
public class UnauthorizedException extends HttpException {
    public UnauthorizedException(String cause) {
        super(cause, HttpStatus.UNAUTHORIZED);
    }
}
