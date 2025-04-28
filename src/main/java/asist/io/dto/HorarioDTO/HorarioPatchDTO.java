package asist.io.dto.HorarioDTO;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

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
public class HorarioPatchDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El id del horario no puede ser nulo")
    private String horarioId;

    private LocalTime entrada;
    private LocalTime salida;
    
    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", 
            message = "El día de la semana debe ser uno de los siguientes: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO")
    private String diaString;
    
    private DayOfWeek dia;
    
    public DayOfWeek getDia() {
        return diaString != null ? Constantes.DIAS_DE_LA_SEMANA.get(diaString) : dia;
    }
    
    public void setDia(String dia) {
        this.diaString = dia;
    }
    
    public void setDia(DayOfWeek dia) {
        this.dia = dia;
    }
}