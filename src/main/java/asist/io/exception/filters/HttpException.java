package asist.io.exception.filters;

import org.springframework.http.HttpStatus;

public abstract class HttpException extends RuntimeException {
    public HttpException(String cause, HttpStatus status) {
        super(cause);
    }
}
