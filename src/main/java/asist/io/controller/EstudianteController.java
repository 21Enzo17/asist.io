package asist.io.controller;

import asist.io.dto.EstudianteGetDTO;
import asist.io.dto.EstudiantePathDTO;
import asist.io.dto.EstudiantePostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IEstudianteService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {
    @Autowired
    private IEstudianteService estudianteService;

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
        catch (DataIntegrityViolationException e) {
            response.put("error", "Body can't be null");
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
    }

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
}
