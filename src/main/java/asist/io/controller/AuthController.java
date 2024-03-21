package asist.io.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.exception.ModelException;
import asist.io.service.IAuthService;


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
     * @return Una respuesta HTTP que contiene el token JWT en el encabezado "Authorization" si el inicio de sesión es exitoso.
     *         Si el inicio de sesión no es exitoso, la respuesta contiene un mensaje de error en el cuerpo.
     *
     * El método funciona de la siguiente manera:
     * 1. Intenta iniciar sesión con el servicio de autenticación.
     * 2. Si el usuario no está verificado, devuelve una respuesta con estado 400 (Bad Request) y un mensaje de error.
     * 3. Si el inicio de sesión es exitoso, crea un encabezado "Authorization" con el token JWT y devuelve una respuesta con estado 200 (OK) y el usuario en el cuerpo.
     * 4. Si el inicio de sesión no es exitoso debido a credenciales incorrectas, devuelve una respuesta con estado 400 y un mensaje de error.
     * 5. Si ocurre cualquier otra excepción, devuelve una respuesta con estado 400 y un mensaje de error.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UsuarioLoginDTO loginReq) {
        Map<String, Object> response = new HashMap<>();
        try {
            UsuarioGetLoginDTO loginRes = authService.login(loginReq);
            HttpHeaders responseHeaders = new HttpHeaders();
            if(!loginRes.getUsuario().getVerificado()){
                response.put("mensaje", "Usuario no verificado, revise su casilla de email para verificar su cuenta.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(new HttpHeaders()).body(response);
            }
            response.put("usuario", loginRes.getUsuario());
            response.put("token", loginRes.getToken());
            return ResponseEntity.ok().headers(responseHeaders).body(response);
        } catch (ModelException e) {
            response.put("mensaje", "Nombre de usuario o contraseña invalidos");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(new HttpHeaders()).body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al iniciar sesion");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).headers(new HttpHeaders()).body(response);
        }
    }

    /**
     * Verifica la validez de un token JWT.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si la verificación fue exitosa o no.
     *
     * El método funciona de la siguiente manera:
     * 1. Intenta verificar el token JWT.
     * 2. Si la verificación es exitosa, devuelve una respuesta con estado 200 (OK) y un mensaje indicando que el token fue verificado correctamente.
     * 3. Si la verificación no es exitosa (por ejemplo, si el token es inválido o ha expirado), devuelve una respuesta con estado 400 (Bad Request) y un mensaje de error.
     * 4. Si ocurre cualquier otra excepción, devuelve una respuesta con estado 400 y un mensaje de error.
     *
     * Nota: Este método asume que el token JWT se envía en el encabezado "Authorization" de la solicitud HTTP.
     */
    @GetMapping("/verificar-token")
    public ResponseEntity<?> verificarToken() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("mensaje", "Token verificado correctamente");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al verificar token");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
}