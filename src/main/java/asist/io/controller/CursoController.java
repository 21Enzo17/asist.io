package asist.io.controller;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPatchDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.ICursoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/cursos")
public class CursoController {
    @Autowired
    private ICursoService cursoService;

    /**
     * Registra un curso en la base de datos
     * @param curso Curso a registrar
     * @return ResponseEntity con la información del curso registrado si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @PostMapping()
    public ResponseEntity registrarCurso(@Valid @RequestBody CursoPostDTO curso) {
        Map<String, Object> response = new HashMap<>();

        try {
            CursoGetDTO cursoRegistrado = cursoService.registrarCurso(curso);
            response.put("curso", cursoRegistrado);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e) {
            response.put("error", "Body can't be null");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Elimina un curso de la base de datos
     * @param id Id del curso a eliminar
     * @return ResponseEntity que indica si la operación fue exitosa o no
     */
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarCurso(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean eliminado = cursoService.eliminarCurso(id);
            response.put("success", eliminado);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Actualiza un curso en la base de datos
     * @param curso Curso a actualizar
     * @return ResponseEntity con la información del curso actualizado si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @PatchMapping()
    public ResponseEntity actualizarCurso(@Valid @RequestBody CursoPatchDTO curso) {
        Map<String, Object> response = new HashMap<>();

        try {
            CursoGetDTO cursoActualizado = cursoService.actualizarCurso(curso);
            response.put("curso", cursoActualizado);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e) {
            response.put("error", "Body can't be null");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene un curso por su id
     * @param id Id del curso a obtener
     * @return ResponseEntity con la información del curso si se encontró un curso con el id proporcionado,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @GetMapping("/id/{id}")
    public ResponseEntity obtenerCursoPorId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            CursoGetDTO curso = cursoService.obtenerCursoPorId(id);
            response.put("curso", curso);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene un curso por su código de asistencia
     * @param codigoAsistencia Código de asistencia del curso a obtener
     * @return ResponseEntity con la información del curso si se encontró un curso con el código de asistencia proporcionado,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @GetMapping("/codigo-asistencia/{codigoAsistencia}")
    public ResponseEntity obtenerCursoPorCodigoAsistencia(@PathVariable String codigoAsistencia) {
        Map<String, Object> response = new HashMap<>();

        try {
            CursoGetDTO curso = cursoService.obtenerCursoPorCodigoAsistencia(codigoAsistencia);
            response.put("curso", curso);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene una lista de cursos por el id de un usuario
     * @param idUsuario Id del usuario
     * @return ResponseEntity con la lista de cursos si se encontraron cursos con el id de usuario proporcionado,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity obtenerCursosPorIdUsuario(@PathVariable String idUsuario) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CursoGetDTO> curso = cursoService.obtenerCursosPorIdUsuario(idUsuario);
            response.put("cursos", curso);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene una lista de cursos según una palabra clave que coincida con el nombre
     * @param termino Palabra clave para buscar cursos
     * @return ResponseEntity con la lista de cursos si se encontraron cursos con la palabra clave proporcionada,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @GetMapping("/termino/{termino}")
    public ResponseEntity obtenerCursosPorTermino(@PathVariable String termino) {
        Map<String, Object> response = new HashMap<>();

        try {
            response.put("cursos", cursoService.obtenerCursosPorTermino(termino));
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.NOT_FOUND);
        }
    }
}
