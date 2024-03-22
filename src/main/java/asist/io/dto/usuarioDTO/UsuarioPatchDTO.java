package asist.io.dto.usuarioDTO;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsuarioPatchDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "El id no puede ser nulo ni vacío")
    @NotNull(message = "El id no puede ser nulo")
    private String id;

    @NotEmpty(message = "El nombre no puede ser nulo ni vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 25 caracteres")
    private String nombre;
}
