package asist.io.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import asist.io.dto.usuarioDtos.UsuarioLoginDto;
import asist.io.dto.usuarioDtos.UsuarioLoginResDto;
import asist.io.service.IAuthService;

@RestController
@RequestMapping("/api/v1/auth")
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginDto loginReq) {
        Map<String, Object> response = new HashMap<>();
        try {
            UsuarioLoginResDto loginRes = authService.login(loginReq);
            HttpHeaders responseHeaders = new HttpHeaders();
            if(!loginRes.getUsuario().getVerificado()){
                response.put("ErrorInicioSesion", "Usuario no verificado, revise su casilla de email para verificar su cuenta.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(new HttpHeaders()).body(response);
            }
            responseHeaders.set("Authorization", "Bearer " + loginRes.getToken());
            response.put("Usuario", loginRes.getUsuario());
            return ResponseEntity.ok().headers(responseHeaders).body(response);
        } catch (BadCredentialsException e) {
            response.put("Mensaje", "Nombre de usuario o contraseña invalidos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(new HttpHeaders()).body(response);
        } catch (Exception e) {
            response.put("Mensaje", "Error al iniciar sesion");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(new HttpHeaders()).body(response);
        }
    }
}