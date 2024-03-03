package asist.io.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import asist.io.dto.usuarioDtos.UsuarioRegDto;
import asist.io.exceptions.ModelException;
import asist.io.service.IUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/v1/usuario")
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class UsuarioController {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private IUsuarioService usuarioService;
    
    @PostMapping("/registro")
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioRegDto usuario, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            usuarioService.guardarUsuario(usuario);
            response.put("Mensaje", "Usuario registrado correctamente");
            return ResponseEntity.ok().body(response);
        } catch (ModelException e) {
            response.put("Mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("Mensaje", "Ups!, ha ocurrido un error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/validar/{token}")
    public ResponseEntity<?> validarUsuario(@PathVariable String token) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Confirmando usuario con token: " + token);
        try {
            usuarioService.validarUsuario(token);
            response.put("Mensaje", "Usuario confirmado correctamente");
            return ResponseEntity.ok().body(response);
        } catch (ModelException e) {
            response.put("Mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("Mensaje", "Ups!, ha ocurrido un error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/olvide-mi-contrasena")
    public ResponseEntity<?> forgotPassword(@RequestParam String correo) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.enviarOlvideContrasena(correo);
            response.put("Mensaje", "Se ha enviado un correo para restablecer la contraseña");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("Mensaje", exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
    }

    @PatchMapping("/cambiar-contrasena/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestParam String contrasena) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.cambiarContrasena(token, contrasena);
            response.put("Mensaje", "Contraseña restablecida correctamente");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("Mensaje", exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    @PostMapping("/reenviar-correo-confirmacion")
    public ResponseEntity<?> reenviarCorreoConfirmacion(@RequestParam String correo) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.enviarCorreoConfirmacion(correo);
            response.put("Mensaje", "Se ha reenviado el correo de confirmacion");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("Mensaje", exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(@RequestParam String correo, @RequestParam String contrasenaNueva, @RequestParam String contrasenaActual) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.cambiarContrasenaLogueado(correo, contrasenaActual, contrasenaNueva);
            response.put("Mensaje", "Contraseña cambiada correctamente");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("Mensaje", exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    
    
    
}
