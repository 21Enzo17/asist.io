package asist.io.dto.asistenciaDTO;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsistenciaGetDTO implements Serializable{
    private static final long serialVersionUID = 1L;


    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha;

    private String cursoId;

    private EstudianteGetDTO estudiante;

}
