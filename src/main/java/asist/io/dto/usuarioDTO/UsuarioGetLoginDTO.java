package asist.io.dto.usuarioDTO;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioGetLoginDTO implements Serializable{
    private static final long serialVersionUID = 1L;

    private UsuarioGetDTO usuario;
    private String token;
}
