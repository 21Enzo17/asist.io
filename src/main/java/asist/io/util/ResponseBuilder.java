package asist.io.util;

import asist.io.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Utilidad para construir respuestas HTTP estandarizadas usando ApiResponse
 */
public class ResponseBuilder {

    /**
     * Crea una respuesta HTTP exitosa con estado 200 (OK)
     * @param message Mensaje descriptivo
     * @param data Datos de respuesta
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }

    /**
     * Crea una respuesta HTTP exitosa con estado 200 (OK) sin datos
     * @param message Mensaje descriptivo
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> ok(String message) {
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    /**
     * Crea una respuesta HTTP exitosa con estado 201 (CREATED)
     * @param message Mensaje descriptivo
     * @param data Datos creados
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(message, data));
    }

    /**
     * Crea una respuesta HTTP para errores de cliente con estado 400 (BAD_REQUEST)
     * @param message Mensaje de error
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    /**
     * Crea una respuesta HTTP para errores de cliente con estado 400 (BAD_REQUEST) y lista de errores
     * @param message Mensaje de error
     * @param errors Lista de errores detallados
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> badRequest(String message, List<String> errors) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message, errors));
    }

    /**
     * Crea una respuesta HTTP para errores de autenticación con estado 401 (UNAUTHORIZED)
     * @param message Mensaje de error
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> unauthorized(String message) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(message));
    }

    /**
     * Crea una respuesta HTTP para errores de autorización con estado 403 (FORBIDDEN)
     * @param message Mensaje de error
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> forbidden(String message) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(message));
    }

    /**
     * Crea una respuesta HTTP para recursos no encontrados con estado 404 (NOT_FOUND)
     * @param message Mensaje de error
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(message));
    }

    /**
     * Crea una respuesta HTTP para errores internos con estado 500 (INTERNAL_SERVER_ERROR)
     * @param message Mensaje de error
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> internalError(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(message));
    }

    /**
     * Crea una respuesta HTTP con metadatos adicionales
     * @param message Mensaje descriptivo
     * @param data Datos de respuesta
     * @param metadata Metadatos adicionales
     * @return ResponseEntity con ApiResponse incluyendo metadatos
     */
    public static <T> ResponseEntity<ApiResponse<T>> withMetadata(String message, T data, Map<String, Object> metadata) {
        return ResponseEntity.ok(ApiResponse.withMetadata(message, data, metadata));
    }

    /**
     * Crea una respuesta HTTP con estado personalizado
     * @param status Estado HTTP
     * @param success Indica si la operación fue exitosa
     * @param message Mensaje descriptivo
     * @param data Datos de respuesta (opcional)
     * @return ResponseEntity con ApiResponse
     */
    public static <T> ResponseEntity<ApiResponse<T>> status(HttpStatus status, boolean success, String message, T data) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .success(success)
                .message(message)
                .data(data)
                .timestamp(java.time.LocalDateTime.now())
                .build();
                
        return new ResponseEntity<>(response, status);
    }
}