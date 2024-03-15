package asist.io.service;

import java.time.LocalDate;
import java.util.List;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.exception.ModelException;


public interface IAsistenciaService {
    /**
     * Registra una asistencia en la base de datos
     * @param asistenciaPostDTO Asistencia a registrar
     * @return Asistencia registrada en formato AsistenciaGetDTO
     * @throws ModelException Si la asistencia es nula, si el curso o el alumno no existen, o si el alumno ya tiene registrada una asistencia para la fecha
     * @SuppressWarnings("null") Para evitar advertencias de null en el metodo validarAsistencia
     */
    public AsistenciaGetDTO registrarAsistencia(AsistenciaPostDTO asistenciaPostDTO);

    /**
     * Obtiene todas las asistencias de un curso
     * @param cursoId Id del curso
     * @return Lista de asistencias en formato AsistenciaGetDTO
     * @throws ModelException Si el curso no existe
     */
    public List<AsistenciaGetDTO> obtenerAsistenciaPorCurso(String cursoId);

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
    public List<AsistenciaGetDTO> obtenerAsistenciaPorPeriodoYCurso(String fechaInicio, String fechaFin, String cursoId);

    /**
     * Obtiene todas las asistencias de un alumno
     * @param lu Lu del alumno
     * @param cursoId Id del curso
     * @return Lista de asistencias en formato AsistenciaGetDTO
     * @throws ModelException Si el alumno no existe
     */
    public List<AsistenciaGetDTO> obtenerAsistenciaPorLuYCurso(String lu, String cursoId);

    /**
     * Obtiene todas las asistencias de un alumno en una fecha y horario especifico
     * @param lu Lu del alumno
     * @param fecha Fecha de la asistencia
     * @param horarioId Id del horario
     * @return Lista de asistencias en formato AsistenciaGetDTO
     */
    public AsistenciaGetDTO obtenerAsistenciaPorFechaLuYHorario(LocalDate fecha, String lu, String horarioId);
}
