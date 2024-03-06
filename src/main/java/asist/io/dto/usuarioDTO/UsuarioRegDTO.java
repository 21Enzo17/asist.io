package asist.io.dto.usuarioDTO;

import java.io.Serializable;


import asist.io.dto.passwordDTO.PasswordDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsuarioRegDTO implements Serializable{

    @NotEmpty(message = "El correo no puede ser nulo ni vacío")
    @Email(message = "El correo no es valido")
    private String correo;
    
    @Valid
    private PasswordDTO contrasena;

    @NotEmpty(message = "El nombre no puede ser nulo ni vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 25 caracteres")
    private String nombre;
}
