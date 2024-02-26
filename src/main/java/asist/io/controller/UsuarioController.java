package asist.io.controller;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import asist.io.dto.usuarioDtos.UsuarioLoginDto;
import asist.io.dto.usuarioDtos.UsuarioLoginResDto;
import asist.io.dto.usuarioDtos.UsuarioRegDto;
import asist.io.exceptions.ModelException;
import asist.io.service.IAuthService;
import asist.io.service.IUsuarioService;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/usuario")
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class UsuarioController {


    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IAuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioRegDto usuario) {
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

    
}
