package asist.io.dto.usuarioDTO;

import java.io.Serializable;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioLoginDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private String correo;

    private String contrasena;

    private Boolean verificado;


}
