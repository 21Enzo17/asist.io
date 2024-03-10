package asist.io.dto.asistenciaDTO;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AsistenciaPostDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;

    @NotNull(message = "La fecha no puede ser nula")
    @NotEmpty(message = "La fecha no puede estar vacía")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate fecha;

    @NotNull(message = "El codigo de asistencia no puede ser nulo")
    @NotEmpty(message = "El codigo de asistencia no puede estar vacio")
    private String codigoAsistencia;

    @NotNull(message = "La libreta universitaria no puede ser nula")
    @NotEmpty(message = "La libreta universitaria no puede estar vacia")
    private String lu;
}
