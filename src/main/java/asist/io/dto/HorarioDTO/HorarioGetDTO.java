package asist.io.dto.HorarioDTO;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HorarioGetDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String horarioId;

    private LocalTime entrada;
    private LocalTime salida;
    
    private DayOfWeek dia;
}
