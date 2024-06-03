package asist.io.dto.HorarioDTO;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import asist.io.util.Constantes;
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

    private String entrada;
    private String salida;
    
    private String dia;

    public void setDayOfWeek(DayOfWeek dia) {
        this.dia = Constantes.DIAS_DE_LA_SEMANA_INVERSO.get(dia);
    }

    public void setEntrada(LocalTime entrada) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String entradaFormateada = entrada.format(formatter);
        this.entrada = entradaFormateada;
    }

    public void setSalida(LocalTime salida) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String salidaFormateada = salida.format(formatter);
        this.salida = salidaFormateada;
    }
}
