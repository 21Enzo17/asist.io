package asist.io.mapper;

import asist.io.dto.CursoGetDTO;
import asist.io.dto.CursoPatchDTO;
import asist.io.dto.CursoPostDTO;
import asist.io.entity.Curso;

public class CursoMapper {

    /**
     * Convertir un CursoPostDTO a un Curso
     * @param curso CursoPostDTO a convertir
     * @return Curso convertido
     */
    public static Curso toEntity(CursoPostDTO curso) {
        if (curso == null) return null;

        Curso entity = new Curso();
        entity.setNombre(curso.getNombre());
        entity.setDescripcion(curso.getDescripcion());
        entity.setCarrera(curso.getCarrera());
        entity.setCodigoAsistencia(curso.getCodigoAsistencia());
        return entity;
    }

    /**
     * Convertir un CursoPatchDTO a un Curso
     * @param curso CursoPatchDTO a convertir
     * @return Curso convertido
     */
    public static Curso toEntity(CursoPatchDTO curso) {
        if (curso == null) return null;

        Curso entity = new Curso();
        entity.setId(curso.getId());
        entity.setNombre(curso.getNombre());
        entity.setDescripcion(curso.getDescripcion());
        entity.setCarrera(curso.getCarrera());
        entity.setCodigoAsistencia(curso.getCodigoAsistencia());
        return entity;
    }

    /**
     * Convertir un CursoGetDTO a un Curso
     * @param curso CursoGetDTO a convertir
     * @return Curso convertido
     */
    public static Curso toEntity(CursoGetDTO curso) {
        if (curso == null) return null;

        Curso entity = new Curso();
        entity.setId(curso.getId());
        entity.setNombre(curso.getNombre());
        entity.setDescripcion(curso.getDescripcion());
        entity.setCarrera(curso.getCarrera());
        entity.setCodigoAsistencia(curso.getCodigoAsistencia());
        return entity;
    }

    /**
     * Convertir un Curso a un CursoGetDTO
     * @param curso Curso a convertir
     * @return CursoGetDTO convertido
     */
    public static CursoPostDTO toPostDTO(Curso curso) {
        if (curso == null) return null;

        CursoPostDTO dto = new CursoPostDTO();
        dto.setNombre(curso.getNombre());
        dto.setDescripcion(curso.getDescripcion());
        dto.setCarrera(curso.getCarrera());
        dto.setCodigoAsistencia(curso.getCodigoAsistencia());
        return dto;
    }

    public static CursoPatchDTO toPatchDTO(Curso curso) {
        if (curso == null) return null;

        CursoPatchDTO dto = new CursoPatchDTO();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setDescripcion(curso.getDescripcion());
        dto.setCarrera(curso.getCarrera());
        dto.setCodigoAsistencia(curso.getCodigoAsistencia());
        return dto;
    }

    public static CursoPatchDTO toPatchDTO(CursoGetDTO curso) {
        if (curso == null) return null;

        CursoPatchDTO dto = new CursoPatchDTO();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setDescripcion(curso.getDescripcion());
        dto.setCarrera(curso.getCarrera());
        dto.setCodigoAsistencia(curso.getCodigoAsistencia());
        return dto;
    }

    public static CursoGetDTO toGetDTO(Curso curso) {
        if (curso == null) return null;

        CursoGetDTO dto = new CursoGetDTO();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setDescripcion(curso.getDescripcion());
        dto.setCarrera(curso.getCarrera());
        dto.setCodigoAsistencia(curso.getCodigoAsistencia());
        return dto;
    }


}
