package asist.io.mapper;

import asist.io.dto.inscripcionDTO.InscripcionGetDTO;
import asist.io.dto.inscripcionDTO.InscripcionPostDTO;
import asist.io.entity.Curso;
import asist.io.entity.Estudiante;
import asist.io.entity.Inscripcion;

public class InscripcionMapper {

    /**
     * Convierte una InscripcionPostDTO a una Inscripcion
     * @param inscripcion InscripcionPostDTO a convertir
     * @return Inscripcion convertida
     */
    public static Inscripcion toEntity(InscripcionPostDTO inscripcion, Curso curso, Estudiante estudiante) {
        Inscripcion entity = new Inscripcion();
        entity.setCurso(curso);
        entity.setEstudiante(estudiante);

        return entity;
    }

    /**
     * Convierte una Inscripcion a una InscripcionGetDTO
     * @param inscripcion Inscripcion a convertir
     * @return InscripcionGetDTO convertida
     */
    public static InscripcionGetDTO toGetDTO(Inscripcion inscripcion) {
        InscripcionGetDTO dto = new InscripcionGetDTO();
        dto.setId(inscripcion.getId());
        dto.setCurso(CursoMapper.toGetDTO(inscripcion.getCurso()));
        dto.setEstudiante(EstudianteMapper.toGetDTO(inscripcion.getEstudiante()));

        return dto;
    }
}
