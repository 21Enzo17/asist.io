package asist.io.dto.HorarioDTO;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import asist.io.util.Constantes;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class HorarioPostDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    
    private String cursoId;
    @NotNull(message = "La hora de entrada no puede ser nula")
    private LocalTime entrada;
    @NotNull(message = "La hora de salida no puede ser nula")
    private LocalTime salida;
    
    @NotNull(message = "El día no puede ser nulo")
    
    

    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", 
             message = "El día de la semana debe ser uno de los siguientes: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO")
    private String dia;

    public DayOfWeek getDia() {
        return Constantes.DIAS_DE_LA_SEMANA.get(dia);
    }

}
