package asist.io.dto.asistenciaDTO;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsistenciaPostDTO implements Serializable {
    private static final long serialVersionUID = 1L;




    @NotNull(message = "El codigo de asistencia no puede ser nulo")
    @NotEmpty(message = "El codigo de asistencia no puede estar vacio")
    private String codigoAsistencia;

    @NotNull(message = "La libreta universitaria no puede ser nula")
    @NotEmpty(message = "La libreta universitaria no puede estar vacia")
    private String lu;

    private String horarioId; 
}
