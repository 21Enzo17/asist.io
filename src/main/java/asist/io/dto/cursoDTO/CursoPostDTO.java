package asist.io.dto.cursoDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CursoPostDTO implements Serializable {
    @NotNull(message = "El nombre no puede ser nulo")
    @NotEmpty(message = "El nombre no puede estar vacío")
    @NotBlank(message = "El nombre no puede estar en blanco")
    private String nombre;

    @NotNull(message = "La descripción no puede ser nula")
    @NotEmpty(message = "La descripción no puede estar vacía")
    @NotBlank(message = "La descripción no puede estar en blanco")
    private String descripcion;

    @NotNull(message = "La carrera no puede ser nula")
    @NotEmpty(message = "La carrera no puede estar vacía")
    @NotBlank(message = "La carrera no puede estar en blanco")
    private String carrera;

    private String codigoAsistencia;

    @NotNull(message = "El id del usuario no puede ser nulo")
    @NotEmpty(message = "El id del usuario no puede estar vacío")
    @NotBlank(message = "El id del usuario no puede estar en blanco")
    private String idUsuario;
}
