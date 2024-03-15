package asist.io.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.exception.ModelException;
import asist.io.mapper.AsistenciaMapper;
import asist.io.repository.AsistenciaRepository;
import asist.io.service.IAsistenciaService;
import asist.io.service.ICursoService;
import asist.io.service.IEstudianteService;
import asist.io.service.IHorarioService;
import asist.io.service.IInscripcionService;
import asist.io.util.DateFormatter;

@Service
public class AsistenciaServiceImpl implements IAsistenciaService {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private AsistenciaRepository asistenciaRepository;
    @Autowired
    private ICursoService cursoService;
    @Autowired
    private IEstudianteService estudianteService;
    @Autowired
    private IInscripcionService inscripcionService;
    @Autowired
    private IHorarioService horarioService;

    /**
     * Registra una asistencia en la base de datos
     * @param asistenciaPostDTO Asistencia a registrar
     * @return Asistencia registrada en formato AsistenciaGetDTO
     * @throws ModelException Si la asistencia es nula, si el curso o el alumno no existen, o si el alumno ya tiene registrada una asistencia para la fecha
     * @SuppressWarnings("null") Para evitar advertencias de null en el metodo validarAsistencia
     */
    @SuppressWarnings("null")
    @Override
    public AsistenciaGetDTO registrarAsistencia(AsistenciaPostDTO asistenciaPostDTO) {

        asistenciaPostDTO.setHorarioId(horarioService.obtenerHorarioPorLocalDateTime(asistenciaPostDTO.getCodigoAsistencia(),LocalDateTime.now()).getId());
        validarAsistencia(asistenciaPostDTO);
        
        logger.info("Registrando asistencia para el alumno, " + asistenciaPostDTO.getLu() + " en el curso " + asistenciaPostDTO.getCodigoAsistencia());

        return AsistenciaMapper.toDTO(asistenciaRepository.save(
            AsistenciaMapper.toEntity(asistenciaPostDTO,
            cursoService.obtenerCursoEntityPorCodigoAsistencia(asistenciaPostDTO.getCodigoAsistencia()),
            estudianteService.obtenerEstudianteEntityPorLu(asistenciaPostDTO.getLu()),horarioService.obtenerHorarioEntityPorId(asistenciaPostDTO.getHorarioId()))));
    }


    /**
     * Obtiene todas las asistencias de un curso
     * @param cursoId Id del curso
     * @return Lista de asistencias en formato AsistenciaGetDTO
     * @throws ModelException Si el curso no existe
     */
    @Override
    public List<AsistenciaGetDTO> obtenerAsistenciaPorCurso(String cursoId) {
        cursoService.existePorId(cursoId);
        logger.info("Obteniendo asistencia para el curso con id " + cursoId);
        return AsistenciaMapper.toDTO(asistenciaRepository.findByCursoId(cursoId));
    }

    /**
     * Obtiene todas las asistencias de un curso en un periodo
     * @param fechaInicio Fecha de inicio del periodo
     * @param fechaFin Fecha de fin del periodo
     * @param cursoId Id del curso
     * @return Lista de asistencias en formato AsistenciaGetDTO
     * @throws ModelException Si el curso no existe
     * @throws ModelException Si la fecha de inicio es posterior a la fecha de fin
     * @throws ModelException Si las fechas estan vacias o nulas
     */
    @Override
    public List<AsistenciaGetDTO> obtenerAsistenciaPorPeriodoYCurso(String fechaInicio, String fechaFin, String cursoId) {
        cursoService.existePorId(cursoId);
        if(DateFormatter.stringToLocalDate(fechaInicio).isAfter(DateFormatter.stringToLocalDate(fechaFin))){
            logger.error("La fecha de inicio no puede ser posterior a la fecha de fin");
            throw new ModelException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if(fechaInicio.isBlank() || fechaFin.isBlank() || fechaInicio.isEmpty() || fechaFin.isEmpty() || fechaInicio == null || fechaFin == null){
            logger.error("Las fechas no pueden estar vacias");
            throw new ModelException("Las fechas no pueden estar vacias");
        }
        logger.info("Obteniendo asistencia para el curso con id " + cursoId + " en el periodo " + fechaInicio + " - " + fechaFin);
        return AsistenciaMapper.toDTO(asistenciaRepository.findyByPeriodoAndCursoId(DateFormatter.stringToLocalDate(fechaInicio).atStartOfDay(),DateFormatter.stringToLocalDate(fechaFin).plusDays(1).atStartOfDay(), cursoId));
    }

    /**
     * Obtiene todas las asistencias de un alumno
     * @param lu Lu del alumno
     * @param cursoId Id del curso
     * @return Lista de asistencias en formato AsistenciaGetDTO
     * @throws ModelException Si el alumno no existe
     */
    @Override
    public List<AsistenciaGetDTO> obtenerAsistenciaPorLuYCurso(String lu, String cursoId) {
        cursoService.existePorId(cursoId);
        estudianteService.obtenerEstudianteEntityPorLu(lu);
        logger.info("Obteniendo asistencia para el alumno con LU " + lu + " en el curso con id " + cursoId);
        return AsistenciaMapper.toDTO(asistenciaRepository.findByEstudianteLuAndCursoId(lu, cursoId));
    }

    /**
     * Obtiene todas las asistencias de un alumno en una fecha y horario especifico
     * @param lu Lu del alumno
     * @param fecha Fecha de la asistencia
     * @param horarioId Id del horario
     * @return Lista de asistencias en formato AsistenciaGetDTO
     */
    @Override
    public AsistenciaGetDTO obtenerAsistenciaPorFechaLuYHorario(LocalDate fecha, String lu, String horarioId) {
        if (fecha == null ) {
            logger.error("La fecha no puede ser nula");
            throw new ModelException("La fecha no puede ser nula");
        }
        LocalDateTime inicioDelDia = fecha.atStartOfDay();
        LocalDateTime finDelDia = fecha.plusDays(1).atStartOfDay();
        logger.info("Obteniendo asistencia para el alumno con LU " + lu + " en el horario con id " + horarioId + " en la fecha " + fecha);
        AsistenciaGetDTO asistenciaGetDTO = AsistenciaMapper.toDTO(asistenciaRepository.findByFechaAndEstudianteLuAndHorarioId(inicioDelDia, finDelDia, lu, horarioId));
        
        if(asistenciaGetDTO != null) return asistenciaGetDTO;
        
        return null;
    }

    /**
     * Metodo Interno
     * Valida que todos los campos de asistencia sean validos, se valida:
     * - Que la asistencia no sea nula
     * - Que el curso exista
     * - Que el alumno exista
     * - Que la inscripcion exista
     * - Que el alumno no tenga registrada una asistencia para la fecha y horario especifico
     * @param asistenciaPostDTO Asistencia a validar
     * @throws ModelException Si la asistencia es nula, si el curso o el alumno no existen, o si el alumno ya tiene registrada una asistencia para la fecha
     */
    private void validarAsistencia(AsistenciaPostDTO asistenciaPostDTO){
        if (asistenciaPostDTO == null) throw new ModelException("La asistencia no puede ser nula");
        cursoService.obtenerCursoPorCodigoAsistencia(asistenciaPostDTO.getCodigoAsistencia());
        estudianteService.obtenerEstudiantePorLu(asistenciaPostDTO.getLu());
        inscripcionService.existePorCodigoAsistenciaYLu(asistenciaPostDTO.getCodigoAsistencia(), asistenciaPostDTO.getLu());
        
        if(obtenerAsistenciaPorFechaLuYHorario(LocalDate.now(),asistenciaPostDTO.getLu(), asistenciaPostDTO.getHorarioId()) != null){
            logger.error("El alumno con LU " + asistenciaPostDTO.getLu() + " ya tiene registrada una asistencia para la fecha " + asistenciaPostDTO.getFecha());
            throw new ModelException("El alumno con LU " + asistenciaPostDTO.getLu() + " ya tiene registrada una asistencia para la fecha " + asistenciaPostDTO.getFecha());
        }
        logger.info ("Asistencia validada con exito");
    }
    
}
