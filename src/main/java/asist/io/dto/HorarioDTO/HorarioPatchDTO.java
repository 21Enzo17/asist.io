package asist.io.dto.HorarioDTO;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;
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
    
    private DayOfWeek dia;


}