package asist.io.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPatchDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.entity.Horario;
import asist.io.exception.ModelException;
import asist.io.mapper.HorarioMapper;
import asist.io.repository.HorarioRepository;
import asist.io.service.ICursoService;
import asist.io.service.IHorarioService;
import asist.io.util.DateFormatter;
import jakarta.transaction.Transactional;

@Service
@Transactional
@SuppressWarnings("null")
public class HorarioServiceImpl implements IHorarioService {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private HorarioRepository horarioRepository;
    @Autowired
    private ICursoService cursoService;

    /**
     * Registra un horario
     * @param HorarioDTO con la informacion del horario
     * @return HorarioGetDTO con la informacion del horario registrado
     * @throws ModelException si los datos del horario no son validos o si existe un horario que se superpone con el horario a registrar
     */
    @Override
    public HorarioGetDTO registrarHorario(HorarioPostDTO horarioDTO) {
        Horario horario = HorarioMapper.toEntity(horarioDTO, cursoService.obtenerCursoEntityPorId(horarioDTO.getCursoId()));

        validarHorario(horario);
        logger.info("Registrando horario para el curso " + horarioDTO.getCursoId());
        return HorarioMapper.toDTO(horarioRepository.save(horario));
    }

    /**
     * Actualiza un horario
     * @param HorarioDTO con la informacion del horario
     * @return HorarioGetDTO con la informacion del horario actualizado
     * @throws ModelException si los datos del horario no son validos o si existe un horario que se superpone con el horario a registrar
     */
    @Override
    public HorarioGetDTO actualizarHorario(HorarioPatchDTO horarioDTO) {
        Horario horario = obtenerHorarioEntityPorId(horarioDTO.getHorarioId());
        if(horarioDTO.getDia() != null )horario.setDia(horarioDTO.getDia());
        if(horarioDTO.getEntrada() != null )horario.setEntrada(horarioDTO.getEntrada());
        if(horarioDTO.getSalida() != null )horario.setSalida(horarioDTO.getSalida());
        validarHorario(horario);
        logger.info("Horario: " + horario.getId() + " actualizado con exito");
        return HorarioMapper.toDTO(horarioRepository.save(horario));
    }

    /**
     * Elimina un horario
     * @param id id del horario
     * @throws ModelException si no se encuentra el horario
     */
    @Override
    public void eliminarHorario(String id) {
        Horario horario = obtenerHorarioEntityPorId(id);
        logger.info("Eliminando horario con id " + id);
        horarioRepository.delete(horario);
    }
    
    /**
     * Obtiene un horario por su id
     * @param id id del horario
     * @return HorarioGetDTO con la informacion del horario
     * @throws ModelException si no se encuentra el horario
     */
    @Override
    public HorarioGetDTO obtenerHorarioPorId(String id) {
        Horario horario = horarioRepository.findById(id).orElse(null);
        if (horario == null) {
            logger.error("No se encontro el horario con id: " + id);
            throw new ModelException("No se encontro el horario con id: " + id);
        }
        logger.info ("Horario con id " + id + " encontrado con exito");
        return HorarioMapper.toDTO(horario);
    }

    /**
     * Obtiene un horario por su id
     * @param id id del horario
     * @return Horario con la informacion del horario
     * @throws ModelException si no se encuentra el horario
     */
    @Override
    public Horario obtenerHorarioEntityPorId(String id) {
        Horario horario = horarioRepository.findById(id).orElse(null);
        if (horario == null) {
            logger.error("No se encontro el horario con id: " + id);
            throw new ModelException("No se encontro el horario con id: " + id);
        }
        logger.info ("Horario con id " + id + " encontrado con exito");
        return horario;
    }

    /**
     * Obtiene todos los horarios de un curso
     * @param cursoId id del curso
     * @return Lista de HorarioGetDTO con la informacion de los horarios
     * @throws ModelException si no se encuentran horarios para el curso
     */
    @Override
    public List<HorarioGetDTO> obtenerHorariosPorCurso(String cursoId) {
        cursoService.existePorId(cursoId);
        logger.info("Obteniendo horarios para el curso con id " + cursoId);
        List<HorarioGetDTO> lista = HorarioMapper.toDTO(horarioRepository.findByCursoId(cursoId));
        if (lista.isEmpty()) {
            logger.error("No se encontraron horarios para el curso con id " + cursoId);
            throw new ModelException("No se encontraron horarios para el curso con id " + cursoId);
        }
        logger.info ("Se encontraron " + lista.size() + " horarios para el curso con id " + cursoId + " con exito");
        return lista;
    }

    /**
     * Obtiene los horarios existentes en una fecha particular
     * @param codigoAsistencia codigo del curso
     * @param fecha fecha en la que se desea buscar los horarios
     * @return Horario con la informacion del horario
     * @throws ModelException si no se encuentra un horario para la fecha 
     */
    @Override
    public Horario obtenerHorarioPorLocalDateTime(String codigoAsistencia, LocalDateTime fecha) {

        if(fecha == null){
            logger.error("La fecha no puede ser nula");
            throw new ModelException("La fecha no puede ser nula");
        }
        Horario horario = horarioRepository.findHorarioContainingTime(codigoAsistencia, fecha.getDayOfWeek(),fecha.toLocalTime());
        if(horario == null){
            logger.error("No se encontro un horario para la fecha " + DateFormatter.localDateTimeToString(fecha));
            throw new ModelException("No se encontro un horario para la fecha " + DateFormatter.localDateTimeToString(fecha));
        }
        logger.info("Horario encontrado para la fecha " + fecha + " con exito");   
        return horario;
    }
    
    /**
     * Obtiene los encabezados de los horarios de un curso entre dos fechas
     * Con el formato: dd/MM/yyyy HH:mm - HH:mm
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @param cursoId id del curso
     * @return Lista de encabezados
     */
    @Override
    public Map<String, Horario> obtenerEncabezadosYHorariosEntreDosFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, String cursoId) {
        Map<String, Horario> horarioPorEncabezado = new TreeMap<>();
        logger.info ("Obteniendo horarios para el curso con id " + cursoId + " entre las fechas " + fechaInicio + " y " + fechaFin);
        for (LocalDateTime fecha = fechaInicio; fecha.isBefore(fechaFin); fecha = fecha.plusDays(1)) {
            List<Horario> horarios = horarioRepository.findByCursoIdAndDia(cursoId, fecha.getDayOfWeek());
            horarios.sort(Comparator.comparing(Horario::getEntrada));
            for (Horario horario : horarios) {
                String horarioStr = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " " +
                                horario.getEntrada().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                                horario.getSalida().format(DateTimeFormatter.ofPattern("HH:mm"));
                horarioPorEncabezado.put(horarioStr, horario);
            }
        }
        return horarioPorEncabezado;
    }

    /**
     * METODO INTERNO
     * Valida los datos de un horario
     * @param horarioDTO
     * 1. La hora de entrada no puede ser nula
     * 2. La hora de salida no puede ser nula
     * 3. La hora de entrada no puede ser mayor a la hora de salida
     * 4. La hora de entrada no puede ser igual a la hora de salida
     * 5. El dia no puede ser nulo
     * 6. El curso debe existir
     * 7. No puede haber un horario que se superponga con el horario a registrar
     * @throws ModelException si los datos no son validos
     */
    private void validarHorario(Horario horario) {
        if (horario.getEntrada() == null) {
            logger.error("La hora de entrada no puede ser nula");
            throw new ModelException("La hora de entrada no puede ser nula");
        }
        if (horario.getSalida() == null) {
            logger.error("La hora de salida no puede ser nula");
            throw new ModelException("La hora de salida no puede ser nula");
        }
        if (horario.getEntrada().isAfter(horario.getSalida())) {
            logger.error("La hora de entrada no puede ser mayor a la hora de salida, Si estas tratando de registrar un horario que inicia un dia y termina el siguiente, por favor registra dos horarios diferentes.");
            throw new ModelException("La hora de entrada no puede ser mayor a la hora de salida");
        }
        if (horario.getEntrada().equals(horario.getSalida())) {
            logger.error("La hora de entrada no puede ser igual a la hora de salida");
            throw new ModelException("La hora de entrada no puede ser igual a la hora de salida");
        }
        if (horario.getDia() == null) {
            logger.error("El dia no puede ser nulo");
            throw new ModelException("El dia no puede ser nulo");
        }
        
        cursoService.existePorId(horario.getCurso().getId());
        
        List<Horario> horarios = horarioRepository.findOverlappingHorarios(horario.getCurso().getId(), horario.getDia(), horario.getEntrada(), horario.getSalida());
        if(horarios.size() != 0){
            if(horarios.size() == 1 && horarios.get(0).getId().equals(horario.getId())){
                logger.info("El horario se superpone con otro horario, pero es el mismo horario");
                return;
            } else if(horarios.size() > 0 ){
                logger.error("El horario se superpone con otro horario");
                throw new ModelException("El horario se superpone con otro horario");
            }
            
        }
        logger.info("Horario validado con exito");
    }

    
}

    


    

