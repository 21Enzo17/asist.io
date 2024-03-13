package asist.io.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.entity.Horario;
import asist.io.exception.ModelException;
import asist.io.mapper.HorarioMapper;
import asist.io.repository.HorarioRepository;
import asist.io.service.ICursoService;
import asist.io.service.IHorarioService;

@Service
@SuppressWarnings("null")
public class HorarioServiceImpl implements IHorarioService {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private ICursoService cursoService;

    @Override
    public void registrarHorario(HorarioPostDTO horarioDTO) {
        validarHorario(horarioDTO);
        logger.info("Registrando horario para el curso " + horarioDTO.getCursoId());
        horarioRepository.save(HorarioMapper.toEntity(horarioDTO, cursoService.obtenerCursoEntityPorId(horarioDTO.getCursoId())));
    }
    
    @Override
    public HorarioGetDTO obtenerHorarioPorId(String id) {
        Horario horario = horarioRepository.findById(id).orElse(null);
        if (horario == null) {
            logger.error("No se encontro el horario con id: " + id);
            throw new ModelException("No se encontro el horario con id: " + id);
        }
        return HorarioMapper.toDTO(horario);
    }
    
    private void validarHorario(HorarioPostDTO horarioDTO) {
        if (horarioDTO.getEntrada() == null) {
            logger.error("La hora de entrada no puede ser nula");
            throw new ModelException("La hora de entrada no puede ser nula");
        }
        if (horarioDTO.getSalida() == null) {
            logger.error("La hora de salida no puede ser nula");
            throw new ModelException("La hora de salida no puede ser nula");
        }
        if (horarioDTO.getEntrada().isAfter(horarioDTO.getSalida())) {
            logger.error("La hora de entrada no puede ser mayor a la hora de salida");
            throw new ModelException("La hora de entrada no puede ser mayor a la hora de salida");
        }
        if (horarioDTO.getEntrada().equals(horarioDTO.getSalida())) {
            logger.error("La hora de entrada no puede ser igual a la hora de salida");
            throw new ModelException("La hora de entrada no puede ser igual a la hora de salida");
        }
        cursoService.existePorId(horarioDTO.getCursoId());
        
    }


    
}
