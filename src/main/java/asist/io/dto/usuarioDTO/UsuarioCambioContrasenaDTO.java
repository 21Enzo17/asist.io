package asist.io.dto.usuarioDTO;

import java.io.Serializable;

import asist.io.dto.passwordDTO.PasswordDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
    @Valid
    private PasswordDTO contrasenaNueva;
}
