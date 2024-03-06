package asist.io.dto.usuarioDtos;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioLoginResDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private UsuarioDto usuario;
    private String token;
}
