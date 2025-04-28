package asist.io.controller;

import asist.io.decorators.GetUser;
import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPatchDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.response.ApiResponse;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.service.ICursoService;
import asist.io.util.ResponseBuilder;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cursos")
public class CursoController {
    @Autowired
    private ICursoService cursoService;

    /**
     * Registra un curso en la base de datos
     * @param curso Curso a registrar
     * @return ResponseEntity con la información del curso registrado si la operación fue exitosa
     */
    @PostMapping()
    public ResponseEntity<ApiResponse<CursoGetDTO>> registrarCurso(@Valid @RequestBody CursoPostDTO curso) {
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(curso);
        return ResponseBuilder.created("Curso registrado correctamente", cursoRegistrado);
    }

    /**
     * Elimina un curso de la base de datos
     * @param id Id del curso a eliminar
     * @return ResponseEntity que indica si la operación fue exitosa o no
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> eliminarCurso(@PathVariable String id, @GetUser UsuarioGetDTO user) {
        cursoService.esPropietario(id, user.getId());
        boolean eliminado = cursoService.eliminarCurso(id);
        return ResponseBuilder.ok("Curso eliminado correctamente");
    }

    /**
     * Actualiza un curso en la base de datos
     * @param curso Curso a actualizar
     * @return ResponseEntity con la información del curso actualizado si la operación fue exitosa
     */
    @PatchMapping()
    public ResponseEntity<ApiResponse<CursoGetDTO>> actualizarCurso(@Valid @RequestBody CursoPatchDTO curso, @GetUser UsuarioGetDTO user) {
        cursoService.esPropietario(curso.getId(), user.getId());
        CursoGetDTO cursoActualizado = cursoService.actualizarCurso(curso);
        return ResponseBuilder.ok("Curso actualizado correctamente", cursoActualizado);
    }

    /**
     * Obtiene un curso por su id
     * @param id Id del curso a obtener
     * @return ResponseEntity con la información del curso si se encontró un curso con el id proporcionado
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<CursoGetDTO>> obtenerCursoPorId(@PathVariable String id) {
        CursoGetDTO curso = cursoService.obtenerCursoPorId(id);
        return ResponseBuilder.ok("Curso obtenido correctamente", curso);
    }

    /**
     * Obtiene un curso por su código de asistencia
     * @param codigoAsistencia Código de asistencia del curso a obtener
     * @return ResponseEntity con la información del curso si se encontró un curso con el código de asistencia proporcionado
     */
    @GetMapping("/codigo-asistencia/{codigoAsistencia}")
    public ResponseEntity<ApiResponse<CursoGetDTO>> obtenerCursoPorCodigoAsistencia(@PathVariable String codigoAsistencia) {
        CursoGetDTO curso = cursoService.obtenerCursoPorCodigoAsistencia(codigoAsistencia);
        return ResponseBuilder.ok("Curso obtenido correctamente", curso);
    }

    /**
     * Obtiene una lista de cursos por el id de un usuario
     * @param idUsuario Id del usuario
     * @return ResponseEntity con la lista de cursos si se encontraron cursos con el id de usuario proporcionado
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<ApiResponse<List<CursoGetDTO>>> obtenerCursosPorIdUsuario(@PathVariable String idUsuario) {
        List<CursoGetDTO> cursos = cursoService.obtenerCursosPorIdUsuario(idUsuario);
        return ResponseBuilder.ok("Cursos obtenidos correctamente", cursos);
    }

    /**
     * Obtiene una lista de cursos según una palabra clave que coincida con el nombre
     * @param termino Palabra clave para buscar cursos
     * @param usuarioId Id del usuario
     * @return ResponseEntity con la lista de cursos si se encontraron cursos con la palabra clave proporcionada
     */
    @GetMapping("/termino/{termino}")
    public ResponseEntity<ApiResponse<List<CursoGetDTO>>> obtenerCursosPorTermino(@PathVariable String termino, @RequestParam String usuarioId) {
        List<CursoGetDTO> cursos = cursoService.obtenerCursosPorTerminoYUsuario(termino, usuarioId);
        return ResponseBuilder.ok("Cursos obtenidos correctamente", cursos);
    }

    /**
     * Genera un código de asistencia único
     * @return ResponseEntity con el código de asistencia generado si la operación fue exitosa
     */
    @GetMapping("/codigo-asistencia")
    public ResponseEntity<ApiResponse<String>> generarCodigoAsistencia() {
        String codigoAsistencia = cursoService.generarCodigoAsistencia();
        return ResponseBuilder.ok("Código de asistencia generado correctamente", codigoAsistencia);
    }
}
