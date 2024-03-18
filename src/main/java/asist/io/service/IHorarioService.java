package asist.io.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPatchDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.entity.Horario;
import asist.io.exception.ModelException;


@Service
public interface IHorarioService {

    /**
     * Registra un horario
     * @param HorarioDTO con la informacion del horario
     * @return HorarioGetDTO con la informacion del horario registrado
     * @throws ModelException si los datos del horario no son validos o si existe un horario que se superpone con el horario a registrar
     */
    public HorarioGetDTO registrarHorario(HorarioPostDTO horarioDTO);

    /**
     * Actualiza un horario
     * @param HorarioDTO con la informacion del horario
     * @return HorarioGetDTO con la informacion del horario actualizado
     * @throws ModelException si los datos del horario no son validos o si existe un horario que se superpone con el horario a registrar
     */
    public HorarioGetDTO actualizarHorario(HorarioPatchDTO horarioDTO);

    /**
     * Elimina un horario
     * @param id id del horario
     * @throws ModelException si no se encuentra el horario
     */
    public void eliminarHorario(String id);

    /**
     * Obtiene un horario por su id
     * @param id id del horario
     * @return HorarioGetDTO con la informacion del horario
     * @throws ModelException si no se encuentra el horario
     */
    public HorarioGetDTO obtenerHorarioPorId(String id);

    /**
     * Obtiene un horario por su id
     * @param id id del horario
     * @return Horario con la informacion del horario
     * @throws ModelException si no se encuentra el horario
     */
    public Horario obtenerHorarioEntityPorId(String id);
    
    /**
     * Obtiene todos los horarios de un curso
     * @param cursoId id del curso
     * @return Lista de HorarioGetDTO con la informacion de los horarios
     * @throws ModelException si no se encuentran horarios para el curso
     */
    public List<HorarioGetDTO> obtenerHorariosPorCurso(String cursoId);

    /**
     * Obtiene los horarios existentes en una fecha particular
     * @param codigoAsistencia codigo del curso
     * @param fecha fecha en la que se desea buscar los horarios
     * @return Horario con la informacion del horario
     * @throws ModelException si no se encuentra un horario para la fecha 
     */
    public Horario obtenerHorarioPorLocalDateTime(String codigoAsistencia, LocalDateTime fecha);

    /**
     * Obtiene los encabezados de los horarios de un curso entre dos fechas
     * Con el formato: dd/MM/yyyy HH:mm - HH:mm
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @param cursoId id del curso
     * @return Lista de encabezados
     */
    public Map<String, Horario> obtenerEncabezadosYHorariosEntreDosFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin, String cursoId);
} 
