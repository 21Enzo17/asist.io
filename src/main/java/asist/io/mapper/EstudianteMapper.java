package asist.io.mapper;

import asist.io.dto.EstudianteGetDTO;
import asist.io.dto.EstudiantePathDTO;
import asist.io.dto.EstudiantePostDTO;
import asist.io.entity.Estudiante;

import java.util.ArrayList;
import java.util.List;

public class EstudianteMapper {
    public static Estudiante toEntity(EstudiantePostDTO estudiante) {
        Estudiante entity = new Estudiante();
        entity.setNombre(estudiante.getNombre());
        entity.setLu(estudiante.getLu());

        return entity;
    }

    public static Estudiante toEntity(EstudianteGetDTO estudiante) {
        Estudiante entity = new Estudiante();
        entity.setId(estudiante.getId());
        entity.setNombre(estudiante.getNombre());
        entity.setLu(estudiante.getLu());

        return entity;
    }

    public static EstudiantePostDTO toPostDTO(Estudiante estudiante) {
        EstudiantePostDTO dto = new EstudiantePostDTO();
        dto.setNombre(estudiante.getNombre());
        dto.setLu(estudiante.getLu());

        return dto;
    }

    public static EstudiantePathDTO toPatchDTO(Estudiante estudiante) {
        EstudiantePathDTO dto = new EstudiantePathDTO();
        dto.setId(estudiante.getId());
        dto.setNombre(estudiante.getNombre());
        dto.setLu(estudiante.getLu());

        return dto;
    }

    public static EstudianteGetDTO toGetDTO(Estudiante estudiante) {
        EstudianteGetDTO dto = new EstudianteGetDTO();
        dto.setId(estudiante.getId());
        dto.setNombre(estudiante.getNombre());
        dto.setLu(estudiante.getLu());

        return dto;
    }

    public static List<EstudianteGetDTO> toGetDTO(List<Estudiante> estudiantes) {
        List<EstudianteGetDTO> dtos = new ArrayList<>();
        for (Estudiante estudiante : estudiantes) {
            dtos.add(toGetDTO(estudiante));
        }
        return dtos;
    }
}
