package asist.io.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.service.IAsistenciaService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/v1/asistencias")
public class AsistenciaController {
    @Autowired
    private IAsistenciaService asistenciaService;

    /**
     * Maneja las solicitudes de registro de asistencia de los usuarios.
     * @param asistenciaPostDTO Un objeto AsistenciaPostDTO que contiene la información de la asistencia proporcionada por el usuario.
     * @return ResponseEntity con la información de la asistencia registrada si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAsistencia(@RequestBody @Valid AsistenciaPostDTO asistenciaPostDTO){
        Map<String, Object> response = new HashMap<>();
        try {
            AsistenciaGetDTO asistenciaGetDTO  = asistenciaService.registrarAsistencia(asistenciaPostDTO);
            response.put ("asistencia", asistenciaGetDTO);
            response.put("message", "Asistencia registrada correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
             response.put("message", "Error al registrar la asistencia: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Maneja las solicitudes de obtención de asistencias de los usuarios.
     * @param fechaInicio
     * @param fechaFin
     * @param idCurso
     * @return ResponseEntity con la información de las asistencias obtenidas si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */
    @GetMapping("/obtenerAsistenciasPorCursoYPeriodo")
    public ResponseEntity<?> obtenerAsistenciasPorCursoYPeriodo(@RequestParam String fechaInicio, @RequestParam String fechaFin, @RequestParam String idCurso){
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("asistencias", asistenciaService.obtenerAsistenciaPorCursoYPeriodo(idCurso, fechaInicio, fechaFin));
            response.put("message", "Asistencias obtenidas correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al obtener asistencias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    @GetMapping("/obtenerAsistenciasPorCursoYPeriodo/excel")
    public ResponseEntity<?> obtenerAsistenciasPorCursoYPeriodoExcel(@RequestParam String fechaInicio, @RequestParam String fechaFin, @RequestParam String idCurso){
        Map<String, Object> response = new HashMap<>();
        try {
            return asistenciaService.generarExcelAsistenciaPorCursoYPeriodo(idCurso, fechaInicio, fechaFin);
        } catch (Exception e) {
            response.put("error", "Error al obtener asistencias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }
    

    /**
     * Maneja las solicitudes de obtención de asistencias de los usuarios.
     * @param lu
     * @param idCurso
     * @param fechaInicio
     * @param fechaFin
     * @return ResponseEntity con la información de las asistencias obtenidas si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */ 
    @GetMapping("/obtenerAsistenciasPorLuCursoYPeriodo")
    public ResponseEntity<?> obtenerAsistenciasPorLuCursoYPeriodo(@RequestParam String lu, @RequestParam String idCurso, @RequestParam String fechaInicio, @RequestParam String fechaFin){
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("asistencias", asistenciaService.obtenerAsistenciaPorLuCursoYPeriodo(lu, idCurso, fechaInicio, fechaFin));
            response.put("message", "Asistencias obtenidas correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al obtener asistencias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    /**
     * Maneja la solicitud de obtener asistencias de un alumno en un periodo y curso especifico en formato excel.
     * @param lu lu del usuario
     * @param idCurso id del curso
     * @param fechaInicio fecha de inicio del periodo
     * @param fechaFin fecha de fin del periodo
     * @return ResponseEntity con el archivo de excel si la operación fue exitosa,
     */ 
    @GetMapping("/obtenerAsistenciasPorLuCursoYPeriodo/excel")
    public ResponseEntity<?> obtenerAsistenciasPorLuCursoYPeriodoExcel(@RequestParam String lu, @RequestParam String idCurso, @RequestParam String fechaInicio, @RequestParam String fechaFin){
        Map<String, Object> response = new HashMap<>();
        try {
            return asistenciaService.generarExcelAsistenciaPorLuCursoYPeriodo(lu, idCurso, fechaInicio, fechaFin);
        } catch (Exception e) {
            response.put("message", "Error al obtener asistencias: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }

    /**
     * Maneja las solicitudes de obtención de asistencias de los usuarios.
     * @param fecha
     * @param lu
     * @param idHorario
     * @return ResponseEntity con la información de la asistencia obtenida si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */
    @GetMapping("/obtenerAsistenciaPorFechaLuYHorario")
    public ResponseEntity<?> obtenerAsistenciaPorFechaLuYHorario(@RequestParam String fecha, @RequestParam String lu, @RequestParam String idHorario){
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("asistencia", asistenciaService.obtenerAsistenciaPorFechaLuYHorario(fecha, lu, idHorario));
            response.put("message", "Asistencia obtenida correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al obtener asistencia: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

    }    
}
