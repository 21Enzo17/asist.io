package asist.io.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import asist.io.dto.response.ApiResponse;
import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.exception.ModelException;
import asist.io.service.IAuthService;
import asist.io.util.ResponseBuilder;


import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class AuthController {

    @Autowired
    private IAuthService authService;

    /**
     * Maneja las solicitudes de inicio de sesión de los usuarios.
     *
     * @param loginReq Un objeto UsuarioLoginDTO que contiene el nombre de usuario y la contraseña proporcionados por el usuario.
     *
     * @return Una respuesta HTTP que contiene el token JWT, refresh token y los datos del usuario si el inicio de sesión es exitoso.
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
     * Verifica la validez de un token JWT y devuelve información sobre el usuario autenticado.
     *
     * @return Una respuesta HTTP que contiene información sobre el usuario si el token es válido.
     */
    @GetMapping("/verificar-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verificarToken() {
        try {
            // Obtenemos el usuario autenticado actual
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            // Preparamos la respuesta con información del usuario
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("email", email);
            userInfo.put("authenticated", authentication.isAuthenticated());
            
            return ResponseBuilder.ok("Token verificado correctamente", userInfo);
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al verificar token");
        }
    }
    
    /**
     * Renueva un token de acceso utilizando un refresh token.
     *
     * @param refreshTokenRequest Un mapa que contiene el refresh token.
     *
     * @return Una respuesta HTTP que contiene el nuevo token de acceso.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                return ResponseBuilder.badRequest("El refresh token es requerido");
            }
            
            String newAccessToken = authService.refreshToken(refreshToken);
            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("token", newAccessToken);
            
            return ResponseBuilder.ok("Token renovado correctamente", tokenResponse);
        } catch (ModelException e) {
            return ResponseBuilder.badRequest("Error al renovar token: " + e.getMessage());
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error inesperado al renovar token");
        }
    }
    
    /**
     * Maneja las solicitudes de cierre de sesión de los usuarios.
     *
     * @param tokens Un mapa que contiene los tokens a invalidar.
     *
     * @return Una respuesta HTTP con un mensaje indicando si el cierre de sesión fue exitoso.
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> logout(@RequestBody Map<String, String> tokens, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            // Utilizamos el token del header si no se proporcionó en el cuerpo
            String accessToken = tokens.get("token");
            if (accessToken == null || accessToken.isEmpty()) {
                accessToken = authHeader;
            }
            
            String refreshToken = tokens.get("refreshToken");
            
            authService.logout(accessToken, refreshToken);
            return ResponseBuilder.ok("Sesión cerrada correctamente");
        } catch (Exception e) {
            return ResponseBuilder.badRequest("Error al cerrar sesión: " + e.getMessage());
        }
    }
}