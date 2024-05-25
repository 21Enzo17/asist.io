package asist.io.mapper;

import java.util.ArrayList;
import java.util.List;


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
        horario.setDia(horarioDTO.getDia());
        return horario;
    }

    public static HorarioGetDTO toDTO(Horario horario) {
        HorarioGetDTO horarioDTO = new HorarioGetDTO();
        horarioDTO.setEntrada(horario.getEntrada());
        horarioDTO.setSalida(horario.getSalida());
        horarioDTO.setHorarioId(horario.getId());
        horarioDTO.setDayOfWeek(horario.getDia());
        return horarioDTO;
    }


    public static List<HorarioGetDTO> toDTO(List<Horario> lista){
        List<HorarioGetDTO> listaDTO = new ArrayList<>();
        for (Horario horario : lista) {
            listaDTO.add(toDTO(horario));
        }
        return listaDTO;
    }
 
}