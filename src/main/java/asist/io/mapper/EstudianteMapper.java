package asist.io.mapper;

import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePathDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.entity.Curso;
import asist.io.entity.Estudiante;

import java.util.ArrayList;
import java.util.List;


public class EstudianteMapper {

    /**
     * Convierte un EstudiantePostDTO a un Estudiante
     * @param estudiante EstudiantePostDTO a convertir
     * @return Estudiante convertido
     */
    public static Estudiante toEntity(EstudiantePostDTO estudiante, Curso curso) {
        Estudiante entity = new Estudiante();
        entity.setNombre(estudiante.getNombre());
        entity.setLu(estudiante.getLu());
        entity.setCurso(curso);

        return entity;
    }

    /**
     * Convierte un EstudianteGetDTO a un Estudiante
     * @param estudiante EstudianteGetDTO a convertir
     * @return Estudianteconvertido
     */
    public static Estudiante toEntity(EstudianteGetDTO estudiante) {
        Estudiante entity = new Estudiante();
        entity.setId(estudiante.getId());
        entity.setNombre(estudiante.getNombre());
        entity.setLu(estudiante.getLu());

        return entity;
    }

    /**
     * Convierte una lista de EstudiantePostDTO a una lista de Estudiante
     * @param estudiantes Lista de EstudiantePostDTO a convertir
     * @return Lista de Estudiante convertida
     */
    public static List<Estudiante> toEntity(List<EstudiantePostDTO> estudiantes,Curso curso) {
        List<Estudiante> entities = new ArrayList<>();
        for (EstudiantePostDTO estudiante : estudiantes) {

            entities.add(toEntity(estudiante,curso));
        }
        return entities;
    }

    /**
     * Convierte un Estudiante a un EstudiantePostDTO
     * @param estudiante Estudiante a convertir
     * @return EstudiantePostDTO convertido
     */
    public static EstudiantePostDTO toPostDTO(Estudiante estudiante) {
        EstudiantePostDTO dto = new EstudiantePostDTO();
        dto.setNombre(estudiante.getNombre());
        dto.setLu(estudiante.getLu());

        return dto;
    }

    /**
     * Convierte un Estudiante a un EstudiantePathDTO
     * @param estudiante Estudiante a convertir
     * @return EstudiantePathDTO convertido
     */
    public static EstudiantePathDTO toPatchDTO(Estudiante estudiante) {
        EstudiantePathDTO dto = new EstudiantePathDTO();
        dto.setId(estudiante.getId());
        dto.setNombre(estudiante.getNombre());
        dto.setLu(estudiante.getLu());

        return dto;
    }

    /**
     * Convierte un Estudiante a un EstudianteGetDTO
     * @param estudiante Estudiante a convertir
     * @return EstudianteGetDTO convertido
     */
    public static EstudianteGetDTO toGetDTO(Estudiante estudiante) {
        EstudianteGetDTO dto = new EstudianteGetDTO();
        dto.setId(estudiante.getId());
        dto.setNombre(estudiante.getNombre());
        dto.setLu(estudiante.getLu());

        return dto;
    }

    /**
     * Convierte una lista de Estudiante a una lista de EstudianteGetDTO
     * @param estudiantes Lista de Estudiante a convertir
     * @return Lista de EstudianteGetDTO convertida
     */
    public static List<EstudianteGetDTO> toGetDTO(List<Estudiante> estudiantes) {
        List<EstudianteGetDTO> dtos = new ArrayList<>();
        for (Estudiante estudiante : estudiantes) {
            dtos.add(toGetDTO(estudiante));
        }
        return dtos;
    }
}
