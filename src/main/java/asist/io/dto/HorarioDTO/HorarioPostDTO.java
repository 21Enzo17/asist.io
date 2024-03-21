package asist.io.dto.HorarioDTO;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

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
    
    private static final Map<String, DayOfWeek> DIAS_DE_LA_SEMANA = new HashMap<>();
    static {
        DIAS_DE_LA_SEMANA.put("LUNES", DayOfWeek.MONDAY);
        DIAS_DE_LA_SEMANA.put("MARTES", DayOfWeek.TUESDAY);
        DIAS_DE_LA_SEMANA.put("MIERCOLES", DayOfWeek.WEDNESDAY);
        DIAS_DE_LA_SEMANA.put("JUEVES", DayOfWeek.THURSDAY);
        DIAS_DE_LA_SEMANA.put("VIERNES", DayOfWeek.FRIDAY);
        DIAS_DE_LA_SEMANA.put("SABADO", DayOfWeek.SATURDAY);
        DIAS_DE_LA_SEMANA.put("DOMINGO", DayOfWeek.SUNDAY);
    }

    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", 
             message = "El día de la semana debe ser uno de los siguientes: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO")
    private String dia;

    public DayOfWeek getDia() {
        return DIAS_DE_LA_SEMANA.get(dia);
    }

}
