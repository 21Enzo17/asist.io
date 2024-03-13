package asist.io.dto.HorarioDTO;

import java.io.Serializable;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HorarioPostDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cursoId;
    private LocalTime entrada;
    private LocalTime salida;
    
}
