package asist.io.dto.asistenciaDTO;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AsistenciaCreateDTO {
    private static final long serialVersionUID = 1L;

    private String codigoAsistencia;
    private String lu;
    private String horarioId;
}
