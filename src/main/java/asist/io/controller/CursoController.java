package asist.io.controller;

import asist.io.dto.CursoGetDTO;
import asist.io.dto.CursoPatchDTO;
import asist.io.dto.CursoPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.ICursoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cursos")
public class CursoController {
    @Autowired
    private ICursoService cursoService;

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

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity obtenerCursosPorIdUsuario(@PathVariable String idUsuario) {
        Map<String, Object> response = new HashMap<>();

        try {
            CursoGetDTO curso = cursoService.obtenerCursoPorIdUsuario(idUsuario);
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

}
