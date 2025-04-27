package asist.io.dto.cursoDTO;

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
public class CursoPatchDTO implements Serializable {
    private String id;
    
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres")
    private String nombre;
    
    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String descripcion;
    
    @Size(max = 50, message = "La carrera no puede exceder los 50 caracteres")
    private String carrera;
    
    private String codigoAsistencia;
}

