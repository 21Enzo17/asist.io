package asist.io.dto.usuarioDTO;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioGetDTO {
    private String id;
    private String nombre;
    private String correo;
    private Boolean verificado;
}
