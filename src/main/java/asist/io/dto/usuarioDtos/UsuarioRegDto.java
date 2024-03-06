package asist.io.dto.usuarioDtos;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsuarioRegDto implements Serializable{
    private final static String contrasenaPattern = "(?=.*[a-zA-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{5,}$"; 
    // Al menos una letra, un carácter especial y tener al menos 5 caracteres de longitud

    @NotEmpty(message = "El correo no puede ser nulo ni vacío")
    @Email(message = "El correo no es valido")
    private String correo;
    
    @NotEmpty(message = "La contraseña no puede ser nula ni vacía")
    @Pattern(regexp = contrasenaPattern, message = "La contraseña debe contener al menos una letra, un carácter especial y tener al menos 5 caracteres de longitud")
    private String contrasena;

    @NotEmpty(message = "El nombre no puede ser nulo ni vacío")
    @Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 25 caracteres")
    private String nombre;
}
