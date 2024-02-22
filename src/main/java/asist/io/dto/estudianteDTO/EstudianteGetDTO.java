package asist.io.dto.estudianteDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteGetDTO implements Serializable {
    private String id;
    private String lu;
    private String nombre;
}
