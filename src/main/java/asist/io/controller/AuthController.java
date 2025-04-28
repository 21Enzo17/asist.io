package asist.io.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import asist.io.dto.response.ApiResponse;
import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.exception.ModelException;
import asist.io.service.IAuthService;
import asist.io.util.ResponseBuilder;


@RestController
@RequestMapping("/api/v1/auth")
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class AuthController {

    @Autowired
    private IAuthService authService;

    /**
     * Maneja las solicitudes de inicio de sesión de los usuarios.
     *
     * @param loginReq Un objeto UsuarioLoginDto que contiene el nombre de usuario y la contraseña proporcionados por el usuario.
     *
     * @return Una respuesta HTTP que contiene el token JWT y los datos del usuario si el inicio de sesión es exitoso.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UsuarioGetLoginDTO>> login(@RequestBody UsuarioLoginDTO loginReq) {
        try {
            UsuarioGetLoginDTO loginRes = authService.login(loginReq);
            if(!loginRes.getUsuario().getVerificado()){
                return ResponseBuilder.badRequest("Usuario no verificado, revise su casilla de email para verificar su cuenta.");
            }
            return ResponseBuilder.ok("Inicio de sesión exitoso", loginRes);
        } catch (ModelException e) {
            return ResponseBuilder.badRequest("Nombre de usuario o contraseña inválidos");
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al iniciar sesión");
        }
    }

    /**
     * Verifica la validez de un token JWT.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si la verificación fue exitosa o no.
     */
    @GetMapping("/verificar-token")
    public ResponseEntity<ApiResponse<Object>> verificarToken() {
        try {
            return ResponseBuilder.ok("Token verificado correctamente");
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al verificar token");
        }
    }
    
}