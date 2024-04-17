package asist.io.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPatchDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.service.IHorarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/horarios")
public class HorarioController {

    @Autowired
    private IHorarioService horarioService;

    /**
     * Maneja las solicitudes de registro de horarios de los usuarios.
     * @param horarioDTO Un objeto HorarioPostDTO que contiene la información del horario proporcionada por el usuario.
     * @return ResponseEntity con la información del horario registrado si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarHorario(@RequestBody @Valid HorarioPostDTO horarioDTO){
        Map<String, Object> response = new HashMap<>();
        try {
            HorarioGetDTO horarioGetDTO  = horarioService.registrarHorario(horarioDTO);
            response.put ("horario", horarioGetDTO);
            response.put("message", "Horario registrado correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al registrar el horario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
 
    /**
     * Maneja las solicitudes de actualización de horarios de los usuarios.
     * @param horarioPatchDTO Un objeto HorarioPatchDTO que contiene la información del horario proporcionada por el usuario.
     * @return ResponseEntity con la información del horario actualizado si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */
    @PatchMapping("/actualizar")
    public ResponseEntity<?> actualizarHorario(@RequestBody @Valid HorarioPatchDTO horarioPatchDTO){
        Map<String, Object> response = new HashMap<>();
        try {
            HorarioGetDTO horarioGetDTO  = horarioService.actualizarHorario(horarioPatchDTO);
            response.put ("horario", horarioGetDTO);
            response.put("message", "Horario actualizado correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al actualizar el horario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Maneja las solicitudes de eliminación de horarios de los usuarios.
     * @param id Id del horario a eliminar
     * @return ResponseEntity con un mensaje de confirmación si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarHorario(@PathVariable String id){
        Map<String, Object> response = new HashMap<>();
        try {
            horarioService.eliminarHorario(id);
            response.put("message", "Horario eliminado correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al eliminar el horario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Maneja las solicitudes de obtención de horarios de los usuarios.
     * @param cursoId Id del curso
     * @return ResponseEntity con la información de los horarios obtenidos si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */
    @GetMapping("/obtenerHorarios/{cursoId}")
    public ResponseEntity<?> obtenerHorariosPorCurso(@PathVariable String cursoId){
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("horarios", horarioService.obtenerHorariosPorCurso(cursoId));
            response.put("message", "Horarios obtenidos correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al obtener los horarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Maneja las solicitudes de obtención de horarios de los usuarios.
     * @param id Id del horario
     * @return ResponseEntity con la información del horario obtenido si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error.
     */
    @PostMapping("/obtenerHorarioPorId/{id}")
    public ResponseEntity<?> obtenerHorarioPorId(@PathVariable String id){
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("horario", horarioService.obtenerHorarioPorId(id));
            response.put("message", "Horario obtenido correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("message", "Error al obtener los horarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


}
