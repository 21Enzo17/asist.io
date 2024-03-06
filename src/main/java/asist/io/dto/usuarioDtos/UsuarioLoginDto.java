package asist.io.dto.usuarioDtos;

import java.io.Serializable;

import org.apache.xpath.operations.Bool;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioLoginDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private String correo;

    private String contrasena;

    private Boolean verificado;


}
