package asist.io.dto.usuarioDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioLoginResDto {
    private UsuarioDto usuario;
    private String token;
}
