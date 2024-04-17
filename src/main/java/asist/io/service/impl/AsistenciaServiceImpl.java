package asist.io.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.entity.Horario;
import asist.io.exception.ModelException;
import asist.io.mapper.AsistenciaMapper;
import asist.io.repository.AsistenciaRepository;
import asist.io.service.IAsistenciaService;
import asist.io.service.ICursoService;
import asist.io.service.IEstudianteService;
import asist.io.service.IHorarioService;
import asist.io.util.DateFormatter;
import asist.io.util.ExcelGenerator;

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
    private IHorarioService horarioService;

    /**
     * Registra una asistencia en la base de datos
     * @param asistenciaPostDTO Asistencia a registrar
     * @return Asistencia registrada en formato AsistenciaGetDTO
     * @throws ModelException Si la asistencia es nula, si el curso o el alumno no existen, o si el alumno ya tiene registrada una asistencia para la fecha
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
            estudianteService.obtenerEstudianteEntityPorCodigoAsistenciaYLu(asistenciaPostDTO.getCodigoAsistencia(),asistenciaPostDTO.getLu()),horarioService.obtenerHorarioEntityPorId(asistenciaPostDTO.getHorarioId()))));
    }


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
    @Override
    public List<List<Object>> obtenerAsistenciaPorCursoYPeriodo(String cursoId,String fechaInicio, String fechaFin){
        cursoService.existePorId(cursoId);
        if( DateFormatter.stringToLocalDate(fechaInicio).isAfter(DateFormatter.stringToLocalDate(fechaFin))) {
            logger.error("La fecha de inicio no puede ser posterior a la fecha de fin");
            throw new ModelException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        logger.info("Obteniendo asistencia para el curso con id " + cursoId);
        return generarTablaAsistencias(agruparAsistenciasPorFechaYHorario(
            AsistenciaMapper.toDTO(asistenciaRepository.findByCursoId(cursoId))), 
            estudianteService.obtenerEstudiantesPorIdCurso(cursoId), 
            horarioService.obtenerEncabezadosYHorariosEntreDosFechas(DateFormatter.stringToLocalDate(fechaInicio).atStartOfDay(), DateFormatter.stringToLocalDate(fechaFin).plusDays(1).atStartOfDay(), cursoId));
    }

 

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
    @Override
    public List<List<Object>> obtenerAsistenciaPorLuCursoYPeriodo(String lu, String cursoId, String fechaInicio, String fechaFin) {
        cursoService.existePorId(cursoId);
        estudianteService.obtenerEstudianteEntityPorLuYCursoId(lu, cursoId);
        logger.info("Obteniendo asistencia para el alumno con LU " + lu + " en el curso con id " + cursoId);
        Map<LocalDate, List<AsistenciaGetDTO>> asistenciasAgrupadas = agruparAsistenciasPorFechaYHorario(AsistenciaMapper.toDTO(asistenciaRepository.findByEstudianteLuAndCursoId(lu, cursoId)));
        List<EstudianteGetDTO> estudiantes = new ArrayList<>();
        estudiantes.add(estudianteService.obtenerEstudiantePorLuYCursoId(lu, cursoId));
        Map<String,Horario> horarios = horarioService.obtenerEncabezadosYHorariosEntreDosFechas(DateFormatter.stringToLocalDate(fechaInicio).atStartOfDay(), DateFormatter.stringToLocalDate(fechaFin).plusDays(1).atStartOfDay(), cursoId);
        
        return generarTablaAsistencias(asistenciasAgrupadas, estudiantes, horarios);
    }

    /**
     * Obtiene una asistencia de un alumno en una fecha y horario especifico
     * @param lu Lu del alumno
     * @param fecha Fecha de la asistencia
     * @param horarioId Id del horario
     * @return asistencia en formato AsistenciaGetDTO
     */
    @Override
    public AsistenciaGetDTO obtenerAsistenciaPorFechaLuYHorario(String fecha, String lu, String horarioId) {
        if (fecha == null ) {
            logger.error("La fecha no puede ser nula");
            throw new ModelException("La fecha no puede ser nula");
        }
        LocalDateTime inicioDelDia = DateFormatter.stringToLocalDate(fecha).atStartOfDay();
        LocalDateTime finDelDia = DateFormatter.stringToLocalDate(fecha).plusDays(1).atStartOfDay();
        logger.info("Obteniendo asistencia para el alumno con LU " + lu + " en el horario con id " + horarioId + " en la fecha " + fecha);
        AsistenciaGetDTO asistenciaGetDTO = AsistenciaMapper.toDTO(asistenciaRepository.findByFechaAndEstudianteLuAndHorarioId(inicioDelDia, finDelDia, lu, horarioId));
        
        if(asistenciaGetDTO != null) return asistenciaGetDTO;
        
        throw new ModelException("No se encontro asistencia para el alumno con LU " + lu + " en el horario con id " + horarioId + " en la fecha " + fecha);
    }

    /**
     * Genera un archivo excel con las asistencias de un curso en un periodo
     * @param cursoId Id del curso
     * @param fechaInicio Fecha de inicio del periodo
     * @param fechaFin Fecha de fin del periodo
     * @return Archivo excel con las asistencias
     */
    @Override
    public ResponseEntity<ByteArrayResource> generarExcelAsistenciaPorCursoYPeriodo(String cursoId, String fechaInicio, String fechaFin){
        String nombreArchivo = cursoService.obtenerCursoPorId(cursoId).getNombre() +" - "+ fechaInicio.replace("/", "-") + " a " + fechaFin.replace("/", "-");
        return ExcelGenerator.escribirTablaEnExcel(obtenerAsistenciaPorCursoYPeriodo(cursoId, fechaInicio, fechaFin), nombreArchivo );
    }

    /**
     * Genera un archivo excel con las asistencias de un alumno en un periodo
     * @param lu Lu del alumno
     * @param cursoId Id del curso
     * @param fechaInicio Fecha de inicio del periodo
     * @param fechaFin Fecha de fin del periodo
     * @return Archivo excel con las asistencias
     */
    public ResponseEntity<ByteArrayResource> generarExcelAsistenciaPorLuCursoYPeriodo(String lu, String cursoId, String fechaInicio, String fechaFin){
        String nombreArchivo = estudianteService.obtenerEstudiantePorLuYCursoId(lu,cursoId).getNombre() +" - "+ fechaInicio.replace("/", "-") + " a " + fechaFin.replace("/", "-");
        return ExcelGenerator.escribirTablaEnExcel(obtenerAsistenciaPorLuCursoYPeriodo(lu, cursoId, fechaInicio, fechaFin), nombreArchivo );
    }


    /**
     * Metodo Interno
     * Valida que todos los campos de asistencia sean validos, se valida:
     * - Que la asistencia no sea nula
     * - Que el curso exista
     * - Que el alumno exista en el curso especifico
     * - Que el alumno no tenga registrada una asistencia para la fecha y horario especifico
     * @param asistenciaPostDTO Asistencia a validar
     * @throws ModelException Si la asistencia es nula, si el curso o el alumno no existen, o si el alumno ya tiene registrada una asistencia para la fecha
     */
    private void validarAsistencia(AsistenciaPostDTO asistenciaPostDTO){
        if (asistenciaPostDTO == null) throw new ModelException("La asistencia no puede ser nula");
        cursoService.obtenerCursoPorCodigoAsistencia(asistenciaPostDTO.getCodigoAsistencia());
        estudianteService.obtenerEstudianteEntityPorCodigoAsistenciaYLu(asistenciaPostDTO.getCodigoAsistencia(), asistenciaPostDTO.getLu());
        
        try{
            obtenerAsistenciaPorFechaLuYHorario(DateFormatter.localDateToString(LocalDate.now()),asistenciaPostDTO.getLu(), asistenciaPostDTO.getHorarioId());
            logger.error("El alumno con LU " + asistenciaPostDTO.getLu() + " ya tiene registrada una asistencia para la fecha " + DateFormatter.localDateTimeToString(LocalDateTime.now()));
            throw new ModelException("El alumno con LU " + asistenciaPostDTO.getLu() + " ya tiene registrada una asistencia para la fecha " + DateFormatter.localDateTimeToString(LocalDateTime.now()));
        }catch(ModelException e){
            logger.info ("Asistencia validada con exito");
        }
    }
    

    /** 
     * Metodo Intgerno 
     * Agrupa las asistencias por fecha y horario con formato:
     * Map<Fecha, List<AsistenciaGetDTO>>
     * "2021-06-01" -> List<AsistenciaGetDTO> con fecha "2021-06-01"
     * @param asistencias Lista de asistencias a agrupar
     * @return Mapa con las asistencias agrupadas
    */
    private Map<LocalDate, List<AsistenciaGetDTO>> agruparAsistenciasPorFechaYHorario(List<AsistenciaGetDTO> asistencias) {
        return asistencias.stream().collect(Collectors.groupingBy(a -> a.getFecha().toLocalDate()));
    }

    /**
     * Metodo Interno
     * Genera una tabla de asistencias con formato
     * [
     *      ["19/09/2022 - 10:00 - 11:00", "19/09/2022 - 11:00 - 12:00"]
     *      ["estudiante1", true, false]
     * ]
     * @param asistencias Mapa con las asistencias agrupadas por fecha
     * @param estudiantes Lista de estudiantes a incluir en la tabla ordenados alfabeticamente
     * @param horarios TreeMap con los horarios a incluir en la tabla
     * @return Tabla de asistencias
     * 
     */
    private List<List<Object>> generarTablaAsistencias(Map<LocalDate, List<AsistenciaGetDTO>> asistencias, List<EstudianteGetDTO> estudiantes, Map<String,Horario> horarios) {
        List<List<Object>> tabla = new ArrayList<>();
        logger.info("Generando tabla de asistencias");
        // Añade los encabezados de horarios a la tabla
        List<Object> encabezados = new ArrayList<>();
        encabezados.add("Estudiante");
        encabezados.addAll(horarios.keySet());
        tabla.add(encabezados);
        
        // Añade las asistencias de cada estudiante a la tabla
        for (EstudianteGetDTO estudiante : estudiantes) {
            List<Object> fila = new ArrayList<>();
            fila.add(estudiante.getNombre());
            for (String encabezado : horarios.keySet()) {
                LocalDate fecha = LocalDate.parse(encabezado.split(" ")[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                List<AsistenciaGetDTO> asistenciasDelDia = asistencias.get(fecha);
                if (asistenciasDelDia == null) {
                    fila.add(false);
                    continue;
                }boolean asistio = asistenciasDelDia != null && asistenciasDelDia.stream().anyMatch(a -> a.getEstudiante().getNombre().equals(estudiante.getNombre()) && a.getHorarioId().equals(horarios.get(encabezado).getId()));
                fila.add(asistio);
            }
            tabla.add(fila);
        }
        return tabla;
    }
           
}
