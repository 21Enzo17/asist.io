package asist.io.dto.asistenciaDTO;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsistenciaGetDTO implements Serializable{
    private static final long serialVersionUID = 1L;


    @JsonFormat(pattern = "dd/MM/yyyy - HH:mm")
    private LocalDateTime fecha;

    private String cursoId;

    private String horarioId;

    private EstudianteGetDTO estudiante;

}
