package asist.io.dto.usuarioDTO;

import java.io.Serializable;

import asist.io.util.Constantes;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter

public class UsuarioCambioContrasenaDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "El correo no puede ser nulo ni vacío")
    @Email(message = "El correo debe ser válido")
    private String correo;
    
    private String contrasenaActual;

    @NotEmpty(message = "La contraseña no puede ser nula ni vacía")
    @Pattern(regexp = Constantes.CONTRASENA_PATTERN, message = "La contraseña debe contener al menos una letra, un carácter especial y tener al menos 5 caracteres de longitud")
    private String contrasenaNueva;
}
