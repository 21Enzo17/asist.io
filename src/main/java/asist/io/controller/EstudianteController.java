package asist.io.controller;

import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IEstudianteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {
    @Autowired
    private IEstudianteService estudianteService;

    /**
     * Endpoint que registra un estudiante en la base de datos
     * @param estudiante Estudiante a registrar
     * @return ResponseEntity con la respuesta de la petición
     */
    @PostMapping()
    public ResponseEntity registrarEstudiante(@Valid @RequestBody EstudiantePostDTO estudiante) {
        Map<String, Object> response = new HashMap<>();

        try {
            EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);
            response.put("estudiante", estudianteRegistrado);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint que registra una lista de estudiantes en la base de datos
     * @param estudiantes Lista de estudiantes a registrar
     * @return ResponseEntity que contiene los estudiantes registrados si la petición fue exitosa o un mensaje de error si no lo fue
     */
    @PostMapping("/lista")
    public ResponseEntity registrarEstudiantes(@Valid @RequestBody List<EstudiantePostDTO> estudiantes) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<EstudianteGetDTO> estudiantesRegistrados = estudianteService.registrarEstudiantes(estudiantes);
            response.put("estudiantes", estudiantesRegistrados);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.CREATED);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint que obtiene un estudiante de la base de datos
     * @param idEstudiante id del estudiante a obtener
     * @return ResponseEntity con la respuesta de la petición
     */
    @GetMapping("/id/{idEstudiante}")
    public ResponseEntity obtenerEstudiantePorId(@PathVariable String idEstudiante) {
        Map<String, Object> response = new HashMap<>();

        try {
            EstudianteGetDTO estudianteObtenido = estudianteService.obtenerEstudiantePorId(idEstudiante);
            response.put("estudiante", estudianteObtenido);
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
     * Endpoint que obtiene un estudiante de la base de datos
     * @param lu lu del estudiante a obtener
     * @return ResponseEntity con la respuesta de la petición
     */
    @GetMapping("/lu/{lu}")
    public ResponseEntity obtenerEstudiantePorLu(@PathVariable String lu) {
        Map<String, Object> response = new HashMap<>();

        try {
            EstudianteGetDTO estudianteObtenido = estudianteService.obtenerEstudiantePorLu(lu);
            response.put("estudiante", estudianteObtenido);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint que obtiene una lista de estudiantes que entan registrados en un curso
     * @param id id del curso
     * @return ResponseEntity con la respuesta de la petición
     */
    @GetMapping("/curso/{id}")
    public ResponseEntity obtenerEstudiantesPorIdCurso(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<EstudianteGetDTO> estudiantesObtenidos = estudianteService.obtenerEstudiantesPorIdCurso(id);
            response.put("estudiantes", estudiantesObtenidos);
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
     * Endpoint que elimina un estudiante de la base de datos
     * @param id id del estudiante a eliminar
     * @return ResponseEntity con la respuesta de la petición
     */
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarEstudiantePorId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean eliminado = estudianteService.eliminarEstudiante(id);
            response.put("success", eliminado);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Endpoint que elimina una lista de estudiantes de la base de datos
     * @param ids Lista de ids de estudiantes a eliminar
     * @return ResponseEntity con un mensaje de éxito si la petición fue exitosa o un mensaje de error si no lo fue
     */
    @DeleteMapping()
    public ResponseEntity eliminarEstudiantes(@RequestBody List<String> ids) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean eliminado = estudianteService.eliminarEstudiantes(ids);
            response.put("success", eliminado);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }
}
