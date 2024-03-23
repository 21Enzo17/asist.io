package asist.io.dto.estudianteDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstudiantePostDTO implements Serializable {
    @NotNull(message = "El campo no puede ser nulo")
    @NotEmpty(message = "El campo no puede estar vacío")
    @NotBlank(message = "El campo no puede contener solamente espacios vacíos")
    @Size(min = 4, max = 15, message = "La longitud del campo debe ser entre 4 y  15 carácteres")
    private String lu;
    @NotNull(message = "El campo no puede ser nulo")
    @NotEmpty(message = "El campo no puede estar vacío")
    @NotBlank(message = "El campo no puede contener solamente espacios vacíos")
    @Size(min = 3, max = 150, message = "La longitud del campo debe ser entre 3 y  150 carácteres")
    private String nombre;

    @NotNull(message = "El campo no puede ser nulo")
    @NotEmpty(message = "El campo no puede estar vacío")
    @NotBlank(message = "El campo no puede contener solamente espacios vacíos")
    private String cursoId;
}
