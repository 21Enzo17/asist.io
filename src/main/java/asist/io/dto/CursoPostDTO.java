package asist.io.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CursoPostDTO implements Serializable {
    private String nombre;
    private String descripcion;
    private String carrera;
    private String codigoAsistencia;
}
