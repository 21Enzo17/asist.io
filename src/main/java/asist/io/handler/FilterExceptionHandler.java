package asist.io.handler;

import asist.io.exception.filters.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class FilterExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity handleNotFoundException(NotFoundException ex) {
        Map<String, Object> body = Map.of(
                "message", ex.getMessage(),
                "status", "404 Not Found"
        );


        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity handleException(Exception ex) {
        Map<String, Object> body = Map.of(
                "message", ex.getMessage(),
                "status", "401 Unauthorized"
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity handleBadRequestException(BadRequestException ex) {
        Map<String, Object> body = Map.of(
                "message", ex.getMessage(),
                "status", "400 Bad Request"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity handleForbiddenException(ForbiddenException ex) {
        Map<String, Object> body = Map.of(
                "message", ex.getMessage(),
                "status", "403 Forbidden"
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity handleInternalServerErrorException(InternalServerErrorException ex) {
        Map<String, Object> body = Map.of(
                "message", ex.getMessage(),
                "status", "500 Internal Server Error"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
