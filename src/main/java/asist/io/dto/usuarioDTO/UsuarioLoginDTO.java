package asist.io.dto.usuarioDTO;

import java.io.Serializable;

import asist.io.util.Constantes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioLoginDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "El correo no puede ser nulo ni vacío")
    @Email(message = "El correo no es válido")
    @Size(max = 100, message = "El correo no puede exceder los 100 caracteres")
    private String correo;

    @NotEmpty(message = "La contraseña no puede ser nula ni vacía")
    @Pattern(regexp = Constantes.CONTRASENA_PATTERN, message = "La contraseña debe contener al menos una letra, un carácter especial y tener al menos 5 caracteres de longitud")
    @Size(min = 5, max = 100, message = "La contraseña debe tener entre 5 y 100 caracteres")
    private String contrasena;

    private Boolean verificado;
}
