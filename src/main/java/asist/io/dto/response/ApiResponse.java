package asist.io.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Clase genérica para estandarizar las respuestas de los endpoints.
 * @param <T> Tipo de dato que se devolverá en el campo 'data'
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * Indica si la operación fue exitosa
     */
    private boolean success;
    
    /**
     * Mensaje descriptivo sobre el resultado de la operación
     */
    private String message;
    
    /**
     * Datos de respuesta para la solicitud
     */
    private T data;
    
    /**
     * Lista de errores en caso de que la operación haya fallado
     */
    private List<String> errors;
    
    /**
     * Información adicional relacionada con la respuesta
     */
    private Map<String, Object> metadata;
    
    /**
     * Fecha y hora de la respuesta
     */
    private LocalDateTime timestamp;

    /**
     * Constructor para respuestas exitosas con datos
     * @param message Mensaje descriptivo
     * @param data Datos de respuesta
     * @return ApiResponse con success=true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Constructor para respuestas exitosas sin datos
     * @param message Mensaje descriptivo
     * @return ApiResponse con success=true
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Constructor para respuestas fallidas con una lista de errores
     * @param message Mensaje descriptivo
     * @param errors Lista de errores
     * @return ApiResponse con success=false
     */
    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Constructor para respuestas fallidas con un único mensaje de error
     * @param message Mensaje descriptivo
     * @return ApiResponse con success=false
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Constructor para respuestas con metadatos adicionales
     * @param message Mensaje descriptivo
     * @param data Datos de respuesta
     * @param metadata Metadatos adicionales
     * @return ApiResponse con success=true y metadatos
     */
    public static <T> ApiResponse<T> withMetadata(String message, T data, Map<String, Object> metadata) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .metadata(metadata)
                .timestamp(LocalDateTime.now())
                .build();
    }
}