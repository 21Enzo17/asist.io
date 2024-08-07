package asist.io.exception.filters;

import org.springframework.http.HttpStatus;

/**
 * Excepción que representa un error interno del servidor.
 * Esta excepción se lanza cuando ocurre un error interno en el servidor.
 */
public class InternalServerErrorException extends HttpException {
    public InternalServerErrorException(String cause) {
        super(cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
