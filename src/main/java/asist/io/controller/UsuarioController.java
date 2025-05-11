package asist.io.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import asist.io.dto.ContrasenaDTO.ContrasenaDTO;
import asist.io.dto.response.ApiResponse;
import asist.io.dto.usuarioDTO.UsuarioCambioContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioPatchDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IUsuarioService;
import asist.io.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
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
    

    /**
     * Maneja las solicitudes de registro de nuevos usuarios.
     *
     * @param usuario Un objeto UsuarioRegDto que contiene la información del usuario proporcionada en la solicitud.
     * @param request La solicitud HTTP que se está manejando.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si el registro fue exitoso o no.
     */
    @PostMapping("/registro")
    public ResponseEntity<ApiResponse<Object>> register(@Valid @RequestBody UsuarioPostDTO usuario, HttpServletRequest request) {
        try {
            usuarioService.guardarUsuario(usuario);
            return ResponseBuilder.ok("Usuario registrado correctamente");
        } catch (ModelException e) {
            return ResponseBuilder.badRequest("Error al registrar el usuario: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al registrar usuario", e);
            return ResponseBuilder.internalError("Ups!, ha ocurrido un error: " + e.getMessage());
        }
    }

    /**
     * Maneja la solicitud de eliminación de un usuario.
     * 
     * @param correo El correo electrónico del usuario que se eliminará.
     * @param contrasena La contraseña del usuario que se eliminará.
     * 
     * @return Una respuesta HTTP que contiene un mensaje indicando si el usuario fue eliminado correctamente o no.
     */
    @DeleteMapping("/eliminar")
    public ResponseEntity<ApiResponse<Object>> eliminarUsuario(@RequestParam String correo, @RequestParam String contrasena) {
        try{
            usuarioService.eliminarUsuario(correo, contrasena);
            return ResponseBuilder.ok("Usuario eliminado correctamente");
        }catch(ModelException exception){
            return ResponseBuilder.badRequest("Error al eliminar usuario: " + exception.getMessage());
        }
    }

    /**
     * Maneja las solicitudes para actualizar la información de un usuario.
     *
     * @param usuario Un objeto UsuarioPatchDTO que contiene la información actualizada del usuario.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si la actualización fue exitosa o no.
     */
    @PatchMapping("/actualizar")
    public ResponseEntity<ApiResponse<Object>> actualizarUsuario(@RequestBody @Valid UsuarioPatchDTO usuario) {
        try{
            usuarioService.actualizarUsuario(usuario);
            return ResponseBuilder.ok("Usuario actualizado correctamente");
        }catch(ModelException exception){
            return ResponseBuilder.badRequest("Error al actualizar usuario: " + exception.getMessage());
        }
    }

    /**
     * Maneja las solicitudes para validar un usuario a través de un token.
     *
     * @param token El token de validación que se utilizará para validar al usuario.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si la validación fue exitosa o no.
     */
    @PatchMapping("/validar/{token}")
    public ResponseEntity<ApiResponse<Object>> validarUsuario(@PathVariable String token) {
        logger.info("Confirmando usuario con token: " + token);
        try {
            usuarioService.validarUsuario(token);
            return ResponseBuilder.ok("Usuario confirmado correctamente");
        } catch (ModelException e) {
            return ResponseBuilder.badRequest("Error al validar el usuario: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al validar usuario", e);
            return ResponseBuilder.internalError("Ups!, ha ocurrido un error: " + e.getMessage());
        }
    }

    /**
     * Maneja las solicitudes para restablecer la contraseña de un usuario.
     *
     * @param correo El correo electrónico del usuario que desea restablecer su contraseña.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si la solicitud de restablecimiento de contraseña fue exitosa o no.
     */
    @PostMapping("/olvide-mi-contrasena")
    public ResponseEntity<ApiResponse<Object>> forgotPassword(@RequestParam String correo) {
        try{
            usuarioService.enviarOlvideContrasena(correo);
            return ResponseBuilder.ok("Se ha enviado un correo para restablecer la contraseña");
        }catch(ModelException exception){
            return ResponseBuilder.badRequest("Ups!, ha ocurrido un error: " + exception.getMessage());
        }
    }

    /**
     * Maneja las solicitudes para restablecer la contraseña de un usuario.
     *
     * @param token El token de restablecimiento de contraseña que se utilizará para identificar al usuario.
     * @param contrasena La nueva contraseña que el usuario desea establecer.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si el restablecimiento de la contraseña fue exitoso o no.
     */
    @PatchMapping("/cambiar-contrasena/{token}")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@PathVariable String token, @RequestBody @Valid ContrasenaDTO contrasena) {
        try{
            usuarioService.cambiarContrasena(token, contrasena);
            return ResponseBuilder.ok("Contraseña restablecida correctamente");
        }catch(ModelException exception){
            return ResponseBuilder.badRequest("Error al cambiar contraseña: " + exception.getMessage());
        }
    }
    
    /**
     * Maneja las solicitudes para reenviar el correo de confirmación a un usuario.
     *
     * @param correo El correo electrónico del usuario al que se le reenviará el correo de confirmación.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si el reenvío del correo de confirmación fue exitoso o no.
     */
    @PostMapping("/reenviar-correo-confirmacion")
    public ResponseEntity<ApiResponse<Object>> reenviarCorreoConfirmacion(@RequestParam String correo) {
        try{
            usuarioService.enviarCorreoConfirmacion(correo);
            return ResponseBuilder.ok("Se ha reenviado el correo de confirmacion");
        }catch(ModelException exception){
            return ResponseBuilder.badRequest("Error al reenviar correo de confirmacion: " +  exception.getMessage());
        }
    }

    /**
     * Maneja las solicitudes para cambiar la contraseña de un usuario que ya está autenticado.
     *
     * @param usuarioCambio Un objeto UsuarioCambioContraDTO que contiene la información del usuario y las contraseñas actual y nueva.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si el cambio de contraseña fue exitoso o no.
     */
    @PatchMapping("/cambiar-contrasena-logueado")
    public ResponseEntity<ApiResponse<Object>> cambiarContrasena(@RequestBody @Valid UsuarioCambioContrasenaDTO usuarioCambio) {
        try{
            usuarioService.cambiarContrasenaLogueado(usuarioCambio);
            return ResponseBuilder.ok("Contraseña cambiada correctamente");
        }catch(ModelException exception){
            return ResponseBuilder.badRequest("Error al cambiar la contraseña: " + exception.getMessage());
        }
    }
}
