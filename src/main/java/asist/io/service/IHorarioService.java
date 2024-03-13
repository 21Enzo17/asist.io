package asist.io.service;

import org.springframework.stereotype.Service;

import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;


@Service
public interface IHorarioService {

    public void registrarHorario(HorarioPostDTO horarioDTO);

    public HorarioGetDTO obtenerHorarioPorId(String id);
    
} 
