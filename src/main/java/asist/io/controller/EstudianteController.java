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
@RequestMapping("/api/v1/estudiantes")
public class EstudianteController {
    @Autowired
    private IEstudianteService estudianteService;

    /**
     * Endpoint que registra un estudiante en la base de datos
     * @param estudiante Estudiante a registrar
     * @return ResponseEntity que contiene el estudiante registrado si
     * la petición fue exitosa o un mensaje de error en caso contrario
     */
    @PostMapping()
    public ResponseEntity registrarEstudiante(@Valid @RequestBody EstudiantePostDTO estudiante) {
        Map<String, Object> response = new HashMap<>();

        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);
        response.put("estudiante", estudianteRegistrado);
        response.put("success", true);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint que registra una lista de estudiantes en la base de datos
     * @param estudiantes Lista de estudiantes a registrar
     * @return ResponseEntity que contiene los estudiantes registrados si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @PostMapping("/lista")
    public ResponseEntity registrarEstudiantes(@Valid @RequestBody List<EstudiantePostDTO> estudiantes) {
        Map<String, Object> response = new HashMap<>();

        List<EstudianteGetDTO> estudiantesRegistrados = estudianteService.registrarEstudiantes(estudiantes);
        response.put("estudiantes", estudiantesRegistrados);
        response.put("success", true);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint que obtiene un estudiante de la base de datos
     * @param idEstudiante id del estudiante a obtener
     * @return ResponseEntity que contiene el estudiante obtenido si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @GetMapping("/id/{idEstudiante}")
    public ResponseEntity obtenerEstudiantePorId(@PathVariable String idEstudiante) {
        Map<String, Object> response = new HashMap<>();

        EstudianteGetDTO estudianteObtenido = estudianteService.obtenerEstudiantePorId(idEstudiante);
        response.put("estudiante", estudianteObtenido);
        response.put("success", true);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Endpoint que obtiene un estudiante de la base de datos
     * @param lu lu del estudiante a obtener
     * @return ResponseEntity que contiene el estudiante obtenido si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @GetMapping("/lu/{lu}")
    public ResponseEntity obtenerEstudiantePorLuYCursoId(@PathVariable String lu, @RequestParam String cursoId) {
        Map<String, Object> response = new HashMap<>();

        EstudianteGetDTO estudianteObtenido = estudianteService.obtenerEstudiantePorLuYCursoId(lu,cursoId );
        response.put("estudiante", estudianteObtenido);
        response.put("success", true);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Endpoint que obtiene una lista de estudiantes que entan registrados en un curso
     * @param id id del curso
     * @return ResponseEntity que contiene la lista de estudiantes obtenidos si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @GetMapping("/curso/{id}")
    public ResponseEntity obtenerEstudiantesPorIdCurso(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        List<EstudianteGetDTO> estudiantesObtenidos = estudianteService.obtenerEstudiantesPorIdCurso(id);
        response.put("estudiantes", estudiantesObtenidos);
        response.put("success", true);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Endpoint que elimina un estudiante de la base de datos
     * @param id id del estudiante a eliminar
     * @return ResponseEntity que contiene un mensaje de éxito si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarEstudiantePorId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        boolean eliminado = estudianteService.eliminarEstudiante(id);
        response.put("success", eliminado);
        return new ResponseEntity(response, HttpStatus.OK);
    }

    /**
     * Endpoint que elimina una lista de estudiantes de la base de datos
     * @param ids Lista de ids de estudiantes a eliminar
     * @return ResponseEntity que contiene un mensaje de éxito si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @DeleteMapping()
    public ResponseEntity eliminarEstudiantes(@RequestBody List<String> ids) {
        Map<String, Object> response = new HashMap<>();

        boolean eliminado = estudianteService.eliminarEstudiantes(ids);
        response.put("success", eliminado);
        return new ResponseEntity(response, HttpStatus.OK);
    }
}
