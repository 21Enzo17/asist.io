package asist.io.mapper;

import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.entity.Curso;
import asist.io.entity.Horario;

public class HorarioMapper {
    public static Horario toEntity(HorarioPostDTO horarioDTO, Curso curso) {
        Horario horario = new Horario();
        horario.setCurso(curso);
        horario.setEntrada(horarioDTO.getEntrada());
        horario.setSalida(horarioDTO.getSalida());
        
        return horario;
    }

    public static HorarioGetDTO toDTO(Horario horario) {
        HorarioGetDTO horarioDTO = new HorarioGetDTO();
        horarioDTO.setEntrada(horario.getEntrada());
        horarioDTO.setSalida(horario.getSalida());
        
        return horarioDTO;
    }

 
}