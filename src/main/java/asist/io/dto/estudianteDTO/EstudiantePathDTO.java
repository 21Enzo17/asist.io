package asist.io.dto.estudianteDTO;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EstudiantePathDTO implements Serializable {
    private String id;
    
    @Size(min = 4, max = 15, message = "La longitud del LU debe ser entre 4 y 15 caracteres")
    private String lu;
    
    @Size(min = 3, max = 150, message = "El nombre no puede exceder los 150 caracteres")
    private String nombre;
}
