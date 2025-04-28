package asist.io.controller;

import asist.io.decorators.GetUser;
import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPatchDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.dto.response.ApiResponse;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.service.ICursoService;
import asist.io.service.IHorarioService;
import asist.io.util.ResponseBuilder;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/horarios")
public class HorarioController {

    @Autowired
    private IHorarioService horarioService;
    
    @Autowired
    private ICursoService cursoService;

    /**
     * Maneja las solicitudes de registro de horarios de los usuarios.
     * @param horarioDTO Un objeto HorarioPostDTO que contiene la información del horario proporcionada por el usuario.
     * @return ResponseEntity con la información del horario registrado si la operación fue exitosa.
     */
    @PostMapping("/registrar")
    public ResponseEntity<ApiResponse<HorarioGetDTO>> registrarHorario(@RequestBody @Valid HorarioPostDTO horarioDTO, @GetUser UsuarioGetDTO user){
        cursoService.esPropietario(horarioDTO.getCursoId(), user.getId());
        try {
            HorarioGetDTO horarioGetDTO = horarioService.registrarHorario(horarioDTO);
            return ResponseBuilder.created("Horario registrado correctamente", horarioGetDTO);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al registrar el horario: " + e.getMessage());
        }
    }
 
    /**
     * Maneja las solicitudes de actualización de horarios de los usuarios.
     * @param horarioPatchDTO Un objeto HorarioPatchDTO que contiene la información del horario proporcionada por el usuario.
     * @return ResponseEntity con la información del horario actualizado si la operación fue exitosa.
     */
    @PatchMapping("/actualizar")
    public ResponseEntity<ApiResponse<HorarioGetDTO>> actualizarHorario(@RequestBody @Valid HorarioPatchDTO horarioPatchDTO, @GetUser UsuarioGetDTO user){
        cursoService.esPropietario(horarioService.obtenerCursoIdPorHorarioId(horarioPatchDTO.getHorarioId()), user.getId());
        try {
            HorarioGetDTO horarioGetDTO = horarioService.actualizarHorario(horarioPatchDTO);
            return ResponseBuilder.ok("Horario actualizado correctamente", horarioGetDTO);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al actualizar el horario: " + e.getMessage());
        }
    }

    /**
     * Maneja las solicitudes de eliminación de horarios de los usuarios.
     * @param id Id del horario a eliminar
     * @return ResponseEntity con un mensaje de confirmación si la operación fue exitosa.
     */
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminarHorario(@PathVariable String id, @GetUser UsuarioGetDTO user){
        cursoService.esPropietario(horarioService.obtenerCursoIdPorHorarioId(id), user.getId());
        try {
            horarioService.eliminarHorario(id);
            return ResponseBuilder.ok("Horario eliminado correctamente");
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al eliminar el horario: " + e.getMessage());
        }
    }
    
    /**
     * Maneja las solicitudes de obtención de horarios de los usuarios.
     * @param cursoId Id del curso
     * @return ResponseEntity con la información de los horarios obtenidos si la operación fue exitosa.
     */
    @GetMapping("/obtenerHorarios/{cursoId}")
    public ResponseEntity<ApiResponse<List<HorarioGetDTO>>> obtenerHorariosPorCurso(@PathVariable String cursoId){
        try {
            List<HorarioGetDTO> horarios = horarioService.obtenerHorariosPorCurso(cursoId);
            return ResponseBuilder.ok("Horarios obtenidos correctamente", horarios);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al obtener los horarios: " + e.getMessage());
        }
    }

    /**
     * Maneja las solicitudes de obtención de horarios de los usuarios.
     * @param id Id del horario
     * @return ResponseEntity con la información del horario obtenido si la operación fue exitosa.
     */
    @PostMapping("/obtenerHorarioPorId/{id}")
    public ResponseEntity<ApiResponse<HorarioGetDTO>> obtenerHorarioPorId(@PathVariable String id){
        try {
            HorarioGetDTO horario = horarioService.obtenerHorarioPorId(id);
            return ResponseBuilder.ok("Horario obtenido correctamente", horario);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al obtener los horarios: " + e.getMessage());
        }
    }
}
