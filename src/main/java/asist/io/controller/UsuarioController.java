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

import asist.io.dto.ContrasenaDTO.ContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioCambioContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioPatchDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IUsuarioService;
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
     *
     * El método funciona de la siguiente manera:
     * 1. Intenta guardar el nuevo usuario utilizando el servicio de usuarios.
     * 2. Si el registro es exitoso, devuelve una respuesta con estado 200 (OK) y un mensaje indicando que el usuario fue registrado correctamente.
     * 3. Si el registro no es exitoso debido a una excepción ModelException (por ejemplo, si el nombre de usuario ya está en uso), devuelve una respuesta con estado 400 (Bad Request) y un mensaje de error.
     * 4. Si ocurre cualquier otra excepción, devuelve una respuesta con estado 500 (Internal Server Error) y un mensaje de error.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> register(@Valid @RequestBody UsuarioPostDTO usuario, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            usuarioService.guardarUsuario(usuario);
            response.put("message", "Usuario registrado correctamente");
            return ResponseEntity.ok().body(response);
        } catch (ModelException e) {
            response.put("message", "Error al registrar el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "Ups!, ha ocurrido un error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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
    public ResponseEntity<?> eliminarUsuario(@RequestParam String correo, @RequestParam String contrasena) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.eliminarUsuario(correo, contrasena);
            response.put("message", "Usuario eliminado correctamente");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("message", "Error al eliminar usuario: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PatchMapping("/actualizar")
    public ResponseEntity<?> actualizarUsuario(@RequestBody @Valid UsuarioPatchDTO usuario) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.actualizarUsuario(usuario);
            response.put("message", "Usuario actualizado correctamente");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("message", "Error al actualizar usuario: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Maneja las solicitudes para validar un usuario a través de un token.
     *
     * @param token El token de validación que se utilizará para validar al usuario.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si la validación fue exitosa o no.
     *
     * El método funciona de la siguiente manera:
     * 1. Intenta validar al usuario utilizando el servicio de usuarios y el token proporcionado.
     * 2. Si la validación es exitosa, devuelve una respuesta con estado 200 (OK) y un mensaje indicando que el usuario fue confirmado correctamente.
     * 3. Si la validación no es exitosa debido a una excepción ModelException (por ejemplo, si el token es inválido), devuelve una respuesta con estado 400 (Bad Request) y un mensaje de error.
     * 4. Si ocurre cualquier otra excepción, devuelve una respuesta con estado 500 (Internal Server Error) y un mensaje de error.
     */
    @PatchMapping("/validar/{token}")
    public ResponseEntity<?> validarUsuario(@PathVariable String token) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Confirmando usuario con token: " + token);
        try {
            usuarioService.validarUsuario(token);
            response.put("message", "Usuario confirmado correctamente");
            return ResponseEntity.ok().body(response);
        } catch (ModelException e) {
            response.put("message", "Error al validar el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("message", "Ups!, ha ocurrido un error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    /**
     * Maneja las solicitudes para restablecer la contraseña de un usuario.
     *
     * @param correo El correo electrónico del usuario que desea restablecer su contraseña.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si la solicitud de restablecimiento de contraseña fue exitosa o no.
     *
     * El método funciona de la siguiente manera:
     * 1. Intenta enviar un correo de restablecimiento de contraseña utilizando el servicio de usuarios y el correo electrónico proporcionado.
     * 2. Si la solicitud es exitosa, devuelve una respuesta con estado 200 (OK) y un mensaje indicando que se ha enviado un correo para restablecer la contraseña.
     * 3. Si la solicitud no es exitosa debido a una excepción ModelException (por ejemplo, si el correo electrónico no está asociado a ningún usuario), devuelve una respuesta con estado 400 (Bad Request) y un mensaje de error.
     */
    @PostMapping("/olvide-mi-contrasena")
    public ResponseEntity<?> forgotPassword(@RequestParam String correo) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.enviarOlvideContrasena(correo);
            response.put("message", "Se ha enviado un correo para restablecer la contraseña");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("message", "Ups!, ha ocurrido un error: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
    }

    /**
     * Maneja las solicitudes para restablecer la contraseña de un usuario.
     *
     * @param token El token de restablecimiento de contraseña que se utilizará para identificar al usuario.
     * @param contrasena La nueva contraseña que el usuario desea establecer.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si el restablecimiento de la contraseña fue exitoso o no.
     *
     * El método funciona de la siguiente manera:
     * 1. Intenta cambiar la contraseña del usuario utilizando el servicio de usuarios, el token y la nueva contraseña proporcionados.
     * 2. Si el restablecimiento es exitoso, devuelve una respuesta con estado 200 (OK) y un mensaje indicando que la contraseña fue restablecida correctamente.
     * 3. Si el restablecimiento no es exitoso debido a una excepción ModelException (por ejemplo, si el token es inválido), devuelve una respuesta con estado 400 (Bad Request) y un mensaje de error.
     */
    @PatchMapping("/cambiar-contrasena/{token}")
    public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody @Valid ContrasenaDTO contrasena) {
        Map<String, Object> response = new HashMap<>();

        try{
            usuarioService.cambiarContrasena(token, contrasena);
            response.put("message", "Contraseña restablecida correctamente");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("message", "Error al cambiar contraseña: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * Maneja las solicitudes para reenviar el correo de confirmación a un usuario.
     *
     * @param correo El correo electrónico del usuario al que se le reenviará el correo de confirmación.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si el reenvío del correo de confirmación fue exitoso o no.
     *
     * El método funciona de la siguiente manera:
     * 1. Intenta reenviar el correo de confirmación utilizando el servicio de usuarios y el correo electrónico proporcionado.
     * 2. Si el reenvío es exitoso, devuelve una respuesta con estado 200 (OK) y un mensaje indicando que el correo de confirmación ha sido reenviado.
     * 3. Si el reenvío no es exitoso debido a una excepción ModelException (por ejemplo, si el correo electrónico no está asociado a ningún usuario), devuelve una respuesta con estado 400 (Bad Request) y un mensaje de error.
     */
    @PostMapping("/reenviar-correo-confirmacion")
    public ResponseEntity<?> reenviarCorreoConfirmacion(@RequestParam String correo) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.enviarCorreoConfirmacion(correo);
            response.put("message", "Se ha reenviado el correo de confirmacion");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("message", "Error al reenviar correo de confirmacion: " +  exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Maneja las solicitudes para cambiar la contraseña de un usuario que ya está autenticado.
     *
     * @param usuarioCambio Un objeto UsuarioCambioContraDTO que contiene la información del usuario y las contraseñas actual y nueva.
     *
     * @return Una respuesta HTTP que contiene un mensaje indicando si el cambio de contraseña fue exitoso o no.
     *
     * El método funciona de la siguiente manera:
     * 1. Intenta cambiar la contraseña del usuario utilizando el servicio de usuarios y la información proporcionada en el objeto usuarioCambio.
     * 2. Si el cambio es exitoso, devuelve una respuesta con estado 200 (OK) y un mensaje indicando que la contraseña fue cambiada correctamente.
     * 3. Si el cambio no es exitoso debido a una excepción ModelException (por ejemplo, si la contraseña actual es incorrecta), devuelve una respuesta con estado 400 (Bad Request) y un mensaje de error.
     */
    @PatchMapping("/cambiar-contrasena-logueado")
    public ResponseEntity<?> cambiarContrasena(@RequestBody @Valid UsuarioCambioContrasenaDTO usuarioCambio) {
        Map<String, Object> response = new HashMap<>();
        try{
            usuarioService.cambiarContrasenaLogueado(usuarioCambio);
            response.put("message", "Contraseña cambiada correctamente");
            return ResponseEntity.ok().body(response);
        }catch(ModelException exception){
            response.put("message", "Errror al cambiar la contraseña: " + exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    
    
    
    
    
}
