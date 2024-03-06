package asist.io.dto.passwordDTO;

import java.io.Serializable;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordDTO implements Serializable {
    private final static String contrasenaPattern = "(?=.*[a-zA-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{5,}$"; 

    @NotEmpty(message = "La contraseña no puede ser nula ni vacía")
    @Pattern(regexp = contrasenaPattern, message = "La contraseña debe contener al menos una letra, un carácter especial y tener al menos 5 caracteres de longitud")
    private String password;
}
