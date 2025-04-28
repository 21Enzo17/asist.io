package asist.io.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.dto.response.ApiResponse;
import asist.io.service.IAsistenciaService;
import asist.io.util.ResponseBuilder;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/v1/asistencias")
public class AsistenciaController {
    @Autowired
    private IAsistenciaService asistenciaService;

    /**
     * Maneja las solicitudes de registro de asistencia de los usuarios.
     * @param asistenciaPostDTO Un objeto AsistenciaPostDTO que contiene la información de la asistencia proporcionada por el usuario.
     * @return ResponseEntity con la información de la asistencia registrada si la operación fue exitosa.
     */
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<AsistenciaGetDTO>> registrarAsistencia(@RequestBody @Valid AsistenciaPostDTO asistenciaPostDTO) {
        try {
            AsistenciaGetDTO asistenciaGetDTO = asistenciaService.registrarAsistencia(asistenciaPostDTO);
            return ResponseBuilder.created("Asistencia registrada correctamente", asistenciaGetDTO);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al registrar la asistencia: " + e.getMessage());
        }
    }

    /**
     * Maneja las solicitudes de obtención de asistencias de los usuarios.
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param idCurso ID del curso
     * @return ResponseEntity con la información de las asistencias obtenidas si la operación fue exitosa.
     */
    @GetMapping("/obtenerAsistenciasPorCursoYPeriodo")
    public ResponseEntity<ApiResponse<List<List<Object>>>> obtenerAsistenciasPorCursoYPeriodo(
            @RequestParam String fechaInicio, 
            @RequestParam String fechaFin, 
            @RequestParam String idCurso) {
        try {
            List<List<Object>> asistencias = asistenciaService.obtenerAsistenciaPorCursoYPeriodo(idCurso, fechaInicio, fechaFin);
            return ResponseBuilder.ok("Asistencias obtenidas correctamente", asistencias);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al obtener asistencias: " + e.getMessage());
        }
    }

    /**
     * Maneja las solicitudes de obtención de asistencias en formato Excel.
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param idCurso ID del curso
     * @return ResponseEntity con el archivo Excel generado si la operación fue exitosa.
     */
    @GetMapping("/obtenerAsistenciasPorCursoYPeriodo/excel")
    public ResponseEntity<?> obtenerAsistenciasPorCursoYPeriodoExcel(
            @RequestParam String fechaInicio, 
            @RequestParam String fechaFin, 
            @RequestParam String idCurso) {
        try {
            return asistenciaService.generarExcelAsistenciaPorCursoYPeriodo(idCurso, fechaInicio, fechaFin);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al obtener asistencias: " + e.getMessage());
        }
    }

    /**
     * Maneja las solicitudes de obtención de asistencias de un alumno específico.
     * @param lu Libreta universitaria del alumno
     * @param idCurso ID del curso
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return ResponseEntity con la información de las asistencias obtenidas si la operación fue exitosa.
     */
    @GetMapping("/obtenerAsistenciasPorLuCursoYPeriodo")
    public ResponseEntity<ApiResponse<List<List<Object>>>> obtenerAsistenciasPorLuCursoYPeriodo(
            @RequestParam String lu, 
            @RequestParam String idCurso, 
            @RequestParam String fechaInicio, 
            @RequestParam String fechaFin) {
        try {
            List<List<Object>> asistencias = asistenciaService.obtenerAsistenciaPorLuCursoYPeriodo(lu, idCurso, fechaInicio, fechaFin);
            return ResponseBuilder.ok("Asistencias obtenidas correctamente", asistencias);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al obtener asistencias: " + e.getMessage());
        }
    }

    /**
     * Maneja la solicitud de obtener asistencias de un alumno en un periodo y curso especifico en formato excel.
     * @param lu Libreta universitaria del alumno
     * @param idCurso ID del curso
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return ResponseEntity con el archivo Excel generado si la operación fue exitosa.
     */
    @GetMapping("/obtenerAsistenciasPorLuCursoYPeriodo/excel")
    public ResponseEntity<?> obtenerAsistenciasPorLuCursoYPeriodoExcel(
            @RequestParam String lu, 
            @RequestParam String idCurso, 
            @RequestParam String fechaInicio, 
            @RequestParam String fechaFin) {
        try {
            return asistenciaService.generarExcelAsistenciaPorLuCursoYPeriodo(lu, idCurso, fechaInicio, fechaFin);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al obtener asistencias: " + e.getMessage());
        }
    }

    /**
     * Maneja las solicitudes de obtención de una asistencia específica por fecha, LU y horario.
     * @param fecha Fecha de la asistencia
     * @param lu Libreta universitaria del alumno
     * @param idHorario ID del horario
     * @return ResponseEntity con la información de la asistencia obtenida si la operación fue exitosa.
     */
    @GetMapping("/obtenerAsistenciaPorFechaLuYHorario")
    public ResponseEntity<ApiResponse<AsistenciaGetDTO>> obtenerAsistenciaPorFechaLuYHorario(
            @RequestParam String fecha, 
            @RequestParam String lu, 
            @RequestParam String idHorario) {
        try {
            AsistenciaGetDTO asistencia = asistenciaService.obtenerAsistenciaPorFechaLuYHorario(fecha, lu, idHorario);
            return ResponseBuilder.ok("Asistencia obtenida correctamente", asistencia);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al obtener asistencia: " + e.getMessage());
        }
    }
}
