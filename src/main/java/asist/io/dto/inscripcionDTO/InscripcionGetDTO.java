package asist.io.dto.inscripcionDTO;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionGetDTO implements Serializable {
    private String id;
    private EstudianteGetDTO estudiante;
    private CursoGetDTO curso;
}
