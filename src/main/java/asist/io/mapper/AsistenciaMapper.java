package asist.io.mapper;


import java.util.ArrayList;
import java.util.List;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.entity.Asistencia;
import asist.io.entity.Curso;
import asist.io.entity.Estudiante;

public class AsistenciaMapper {


    public static Asistencia toEntity(AsistenciaPostDTO asistenciaPostDTO, Curso curso, Estudiante estudiante) {
        if(asistenciaPostDTO == null) return null;
        
        Asistencia asistencia = new Asistencia();
        asistencia.setCurso(curso);
        asistencia.setEstudiante(estudiante);

        return asistencia;
    }

    public static AsistenciaGetDTO toDTO(Asistencia asistencia) {
        if(asistencia == null) return null;

        AsistenciaGetDTO asistenciaGetDTO = new AsistenciaGetDTO();
        asistenciaGetDTO.setCursoId(asistencia.getCurso().getId());
        asistenciaGetDTO.setEstudiante(EstudianteMapper.toGetDTO(asistencia.getEstudiante()));
        asistenciaGetDTO.setFecha(asistencia.getFecha());

        return asistenciaGetDTO;
    }

    public static List<AsistenciaGetDTO> toDTO(List<Asistencia> asistencias) {
        if(asistencias == null) return null;

        List<AsistenciaGetDTO> asistenciasDTO = new ArrayList<>();
        for(Asistencia asistencia : asistencias) {
            asistenciasDTO.add(toDTO(asistencia));
        }

        return asistenciasDTO;
    }

}
