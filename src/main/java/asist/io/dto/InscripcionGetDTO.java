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
public class InscripcionGetDTO implements Serializable {
    private String id;
    private EstudianteGetDTO estudiante;
    private CursoGetDTO curso;
}
