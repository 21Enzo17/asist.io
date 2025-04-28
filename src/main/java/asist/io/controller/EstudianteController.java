package asist.io.controller;

import asist.io.decorators.GetUser;
import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.dto.response.ApiResponse;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.service.ICursoService;
import asist.io.service.IEstudianteService;
import asist.io.util.ResponseBuilder;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estudiantes")
public class EstudianteController {
    @Autowired
    private IEstudianteService estudianteService;
    
    @Autowired
    private ICursoService cursoService;

    /**
     * Endpoint que registra un estudiante en la base de datos
     * @param estudiante Estudiante a registrar
     * @return ResponseEntity que contiene el estudiante registrado si
     * la petición fue exitosa o un mensaje de error en caso contrario
     */
    @PostMapping()
    public ResponseEntity<ApiResponse<EstudianteGetDTO>> registrarEstudiante(@Valid @RequestBody EstudiantePostDTO estudiante, @GetUser UsuarioGetDTO user) {
        cursoService.esPropietario(estudiante.getCursoId(), user.getId());
        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);
        return ResponseBuilder.created("Estudiante registrado correctamente", estudianteRegistrado);
    }

    /**
     * Endpoint que registra una lista de estudiantes en la base de datos
     * @param estudiantes Lista de estudiantes a registrar
     * @return ResponseEntity que contiene los estudiantes registrados si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @PostMapping("/lista")
    public ResponseEntity<ApiResponse<List<EstudianteGetDTO>>> registrarEstudiantes(@Valid @RequestBody List<EstudiantePostDTO> estudiantes, @GetUser UsuarioGetDTO user) {
        cursoService.esPropietario(estudiantes.get(0).getCursoId(), user.getId());
        List<EstudianteGetDTO> estudiantesRegistrados = estudianteService.registrarEstudiantes(estudiantes);
        return ResponseBuilder.created("Estudiantes registrados correctamente", estudiantesRegistrados);
    }

    /**
     * Endpoint que obtiene un estudiante de la base de datos
     * @param idEstudiante id del estudiante a obtener
     * @return ResponseEntity que contiene el estudiante obtenido si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @GetMapping("/id/{idEstudiante}")
    public ResponseEntity<ApiResponse<EstudianteGetDTO>> obtenerEstudiantePorId(@PathVariable String idEstudiante) {
        EstudianteGetDTO estudianteObtenido = estudianteService.obtenerEstudiantePorId(idEstudiante);
        return ResponseBuilder.ok("Estudiante obtenido correctamente", estudianteObtenido);
    }

    /**
     * Endpoint que obtiene un estudiante de la base de datos
     * @param lu lu del estudiante a obtener
     * @return ResponseEntity que contiene el estudiante obtenido si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @GetMapping("/lu/{lu}")
    public ResponseEntity<ApiResponse<EstudianteGetDTO>> obtenerEstudiantePorLuYCursoId(@PathVariable String lu, @RequestParam String cursoId) {
        EstudianteGetDTO estudianteObtenido = estudianteService.obtenerEstudiantePorLuYCursoId(lu, cursoId);
        return ResponseBuilder.ok("Estudiante obtenido correctamente", estudianteObtenido);
    }

    /**
     * Endpoint que obtiene una lista de estudiantes que están registrados en un curso
     * @param id id del curso
     * @return ResponseEntity que contiene la lista de estudiantes obtenidos si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @GetMapping("/curso/{id}")
    public ResponseEntity<ApiResponse<List<EstudianteGetDTO>>> obtenerEstudiantesPorIdCurso(@PathVariable String id) {
        List<EstudianteGetDTO> estudiantesObtenidos = estudianteService.obtenerEstudiantesPorIdCurso(id);
        return ResponseBuilder.ok("Estudiantes obtenidos correctamente", estudiantesObtenidos);
    }

    /**
     * Endpoint que elimina un estudiante de la base de datos
     * @param id id del estudiante a eliminar
     * @return ResponseEntity que contiene un mensaje de éxito si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Boolean>> eliminarEstudiantePorId(@PathVariable String id, @GetUser UsuarioGetDTO user) {
        cursoService.esPropietario(estudianteService.obtenerCursoIdPorEstudianteId(id), user.getId());
        boolean eliminado = estudianteService.eliminarEstudiante(id);
        return ResponseBuilder.ok("Estudiante eliminado correctamente", eliminado);
    }

    /**
     * Endpoint que elimina una lista de estudiantes de la base de datos
     * @param ids Lista de ids de estudiantes a eliminar
     * @return ResponseEntity que contiene un mensaje de éxito si la
     * petición fue exitosa o un mensaje de error en caso contrario
     */
    @DeleteMapping()
    public ResponseEntity<ApiResponse<Boolean>> eliminarEstudiantes(@RequestBody List<String> ids, @GetUser UsuarioGetDTO user) {
        cursoService.esPropietario(estudianteService.obtenerCursoIdPorEstudianteId(ids.get(0)), user.getId());
        boolean eliminado = estudianteService.eliminarEstudiantes(ids);
        return ResponseBuilder.ok("Estudiantes eliminados correctamente", eliminado);
    }
}
