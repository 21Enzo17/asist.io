package asist.io.dto.asistenciaDTO;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsistenciaPostDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El codigo de asistencia no puede ser nulo")
    @NotEmpty(message = "El codigo de asistencia no puede estar vacio")
    @NotBlank(message = "El código de asistencia no puede estar en blanco")
    @Size(max = 50, message = "El código de asistencia no puede exceder los 50 caracteres")
    private String codigoAsistencia;

    @NotNull(message = "La libreta universitaria no puede ser nula")
    @NotEmpty(message = "La libreta universitaria no puede estar vacia")
    @NotBlank(message = "La libreta universitaria no puede estar en blanco")
    @Size(min = 4, max = 15, message = "La longitud de la libreta universitaria debe ser entre 4 y 15 caracteres")
    private String lu;

    @Size(max = 50, message = "El ID del horario no puede exceder los 50 caracteres")
    private String horarioId; 
}
