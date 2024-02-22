package asist.io.controller;

import asist.io.dto.inscripcionDTO.InscripcionGetDTO;
import asist.io.dto.inscripcionDTO.InscripcionPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IInscripcionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/inscripciones")
public class InscripcionController {
    @Autowired
    private IInscripcionService inscripcionService;

    /**
     * Registra una inscripción en la base de datos
     * @param inscripcion Inscripción a registrar
     * @return ResponseEntity con la información de la inscripción registrada si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @PostMapping()
    public ResponseEntity registrarInscripcion(@Valid @RequestBody InscripcionPostDTO inscripcion) {
        Map<String, Object> response = new HashMap<>();

        try {
            InscripcionGetDTO inscripcionRegistrada = inscripcionService.registrarInscripcion(inscripcion);
            response.put("inscripcion", inscripcionRegistrada);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        catch (DataIntegrityViolationException e) {
            response.put("error", "El estudiante ya está inscrito en el curso");
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            response.put("error", "Error interno del servidor");
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene una inscripción por su id
     * @param id Id de la inscripción a obtener
     * @return ResponseEntity con la información de la inscripción si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @GetMapping("/{id}")
    public ResponseEntity obtenerInscripcionPorId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            InscripcionGetDTO inscripcionObtenida = inscripcionService.obtenerInscripcionPorId(id);
            response.put("inscripcion", inscripcionObtenida);
            response.put("success", true);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            response.put("error", "Error interno del servidor");
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina una inscripción por su id
     * @param id Id de la inscripción a eliminar
     * @return ResponseEntity con la información de la inscripción eliminada si la operación fue exitosa,
     * de lo contrario la ResponseEntity contendrá un mensaje de error
     */
    @DeleteMapping("/{id}")
    public ResponseEntity eliminarInscripcionPorId(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean eliminada = inscripcionService.eliminarInscripcionPorId(id);
            response.put("success", eliminada);
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch (ModelException e) {
            response.put("error", e.getMessage());
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        catch (Exception e) {
            response.put("error", "Error interno del servidor");
            response.put("success", false);
            return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
