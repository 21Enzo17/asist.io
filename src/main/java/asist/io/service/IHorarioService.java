package asist.io.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPatchDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.entity.Horario;


@Service
public interface IHorarioService {

    public HorarioGetDTO registrarHorario(HorarioPostDTO horarioDTO);

    public HorarioGetDTO actualizarHorario(HorarioPatchDTO horarioDTO);

    public void eliminarHorario(String id);

    public HorarioGetDTO obtenerHorarioPorId(String id);

    public Horario obtenerHorarioEntityPorId(String id);
    
    public List<HorarioGetDTO> obtenerHorariosPorCurso(String cursoId);

    public Horario obtenerHorarioPorLocalDateTime(String codigoAsistencia, LocalDateTime fecha);
} 
