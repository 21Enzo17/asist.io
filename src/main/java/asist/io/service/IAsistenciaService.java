package asist.io.service;

import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.exception.ModelException;

@Service
public interface IAsistenciaService {

    /**
     * Registra una asistencia en la base de datos
     * @param asistenciaPostDTO Asistencia a registrar
     * @return Asistencia registrada en formato AsistenciaGetDTO
     * @throws ModelException Si la asistencia es nula, si el curso o el alumno no existen, o si el alumno ya tiene registrada una asistencia para la fecha
     */
    public AsistenciaGetDTO registrarAsistencia(AsistenciaPostDTO asistenciaPostDTO);

    /**
     * Obtiene todas las asistencias de un curso en un periodo
     * @param cursoId Id del curso
     * @param fechaInicio Fecha de inicio del periodo
     * @param fechaFin Fecha de fin del periodo
     * @return Tabla con formato:
     * [
     *     ["19/09/2022 - 10:00 - 11:00", "19/09/2022 - 11:00 - 12:00"]
     *    ["estudiante1", true, false]
     * ]
     * @throws ModelException Si el curso no existe
     */
    public List<List<Object>> obtenerAsistenciaPorCursoYPeriodo(String cursoId,String fechaInicio, String fechaFin);


    /**
     * Obtiene todas las asistencias de un alumno en un Periodo
     * @param lu Lu del alumno
     * @param cursoId Id del curso
     * @param fechaInicio Fecha de inicio del periodo
     * @param fechaFin Fecha de fin del periodo
     * @return Tabla con formato:
     * [
     *     ["19/09/2022 - 10:00 - 11:00", "19/09/2022 - 11:00 - 12:00"]
     *    ["estudiante1", true, false]
     * ]
     * @throws ModelException Si el alumno no existe
     */
    public List<List<Object>> obtenerAsistenciaPorLuCursoYPeriodo(String lu, String cursoId, String fechaInicio, String fechaFin);

    /**
     * Obtiene una asistencia de un alumno en una fecha y horario especifico
     * @param lu Lu del alumno
     * @param fecha Fecha de la asistencia
     * @param horarioId Id del horario
     * @return asistencia en formato AsistenciaGetDTO
     */
    public AsistenciaGetDTO obtenerAsistenciaPorFechaLuYHorario(String fecha, String lu, String horarioId);

    /**
     * Genera un archivo excel con las asistencias de un curso en un periodo
     * @param cursoId Id del curso
     * @param fechaInicio Fecha de inicio del periodo
     * @param fechaFin Fecha de fin del periodo
     * @return Archivo excel con las asistencias
     */
    public ResponseEntity<ByteArrayResource> generarExcelAsistenciaPorCursoYPeriodo(String cursoId, String fechaInicio, String fechaFin);

    /**
     * Genera un archivo excel con las asistencias de un alumno en un periodo
     * @param lu Lu del alumno
     * @param cursoId Id del curso
     * @param fechaInicio Fecha de inicio del periodo
     * @param fechaFin Fecha de fin del periodo
     * @return Archivo excel con las asistencias
     */
    public ResponseEntity<ByteArrayResource> generarExcelAsistenciaPorLuCursoYPeriodo(String lu, String cursoId, String fechaInicio, String fechaFin);

    
}
