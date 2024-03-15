package asist.io.dto.cursoDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CursoGetDTO implements Serializable {
    private String id;
    private String nombre;
    private String descripcion;
    private String carrera;
    private String codigoAsistencia;
    private String idUsuario;
}
