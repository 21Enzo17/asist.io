package asist.io.dto.inscripcionDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InscripcionPostDTO implements Serializable {
    @NotNull
    @NotEmpty
    @NotBlank
    private String idEstudiante;

    @NotNull
    @NotEmpty
    @NotBlank
    private String idCurso;
}
