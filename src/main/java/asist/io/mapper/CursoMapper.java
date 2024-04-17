package asist.io.mapper;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPatchDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.entity.Curso;
import asist.io.entity.Usuario;

import java.util.ArrayList;
import java.util.List;

public class CursoMapper {

    /**
     * Convertir un CursoPostDTO a un Curso
     * @param curso CursoPostDTO a convertir
     * @return Curso convertido
     */
    public static Curso toEntity(CursoPostDTO curso, Usuario usuario) {
        if (curso == null) return null;

        Curso entity = new Curso();
        entity.setNombre(curso.getNombre());
        entity.setDescripcion(curso.getDescripcion());
        entity.setCarrera(curso.getCarrera());
        entity.setCodigoAsistencia(curso.getCodigoAsistencia());
        entity.setUsuario(usuario);
        return entity;
    }

    /**
     * Convertir un CursoPatchDTO a un Curso
     * @param curso CursoPatchDTO a convertir
     * @return Curso convertido
     */
    public static Curso toEntity(CursoPatchDTO curso, Usuario usuario) {
        if (curso == null) return null;

        Curso entity = new Curso();
        entity.setId(curso.getId());
        entity.setNombre(curso.getNombre());
        entity.setDescripcion(curso.getDescripcion());
        entity.setCarrera(curso.getCarrera());
        entity.setCodigoAsistencia(curso.getCodigoAsistencia());
        entity.setUsuario(usuario);
        return entity;
    }

    /**
     * Convertir un CursoGetDTO a un CursoPatchDTO
     * @param curso CursoGetDTO a convertir
     * @return
     */
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

    /**
     * Convertir un Curso a un CursoGetDTO
     * @param curso Curso a convertir
     * @return
     */
    public static CursoGetDTO toGetDTO(Curso curso) {
        if (curso == null) return null;

        CursoGetDTO dto = new CursoGetDTO();
        dto.setId(curso.getId());
        dto.setNombre(curso.getNombre());
        dto.setDescripcion(curso.getDescripcion());
        dto.setCarrera(curso.getCarrera());
        dto.setCodigoAsistencia(curso.getCodigoAsistencia());
        dto.setIdUsuario(curso.getUsuario().getId());
        return dto;
    }

    /**
     * Convertir una lista de Curso a una lista de CursoGetDTO
     * @param cursos Lista de cursos a convertir
     * @return
     */
    public static List<CursoGetDTO> toGetDTO(List<Curso> cursos) {
        if (cursos == null) return null;

        List<CursoGetDTO> dtos = new ArrayList<>();
        for (Curso curso : cursos) {
            dtos.add(toGetDTO(curso));
        }
        return dtos;
    }


}
