package asist.io.service;

import java.util.Optional;

import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;

/**
 * Interfaz para el servicio de gestión de tokens de refresco (refresh tokens).
 */
public interface IRefreshTokenService {
    
    /**
     * Crea un nuevo token de refresco para un usuario.
     * Si el usuario ya tiene tokens de refresco activos, estos serán revocados.
     * 
     * @param usuario El usuario para el que se creará el token de refresco
     * @return El nuevo token de refresco creado
     */
    RefreshToken crearRefreshToken(Usuario usuario);
    
    /**
     * Valida un token de refresco.
     * Verifica que el token exista, no esté expirado y no haya sido revocado.
     * 
     * @param token El valor del token de refresco a validar
     * @return Un Optional con el token si es válido, o vacío si no es válido
     */
    Optional<RefreshToken> validarRefreshToken(String token);
    
    /**
     * Revoca todos los tokens de refresco de un usuario.
     * Esto es útil cuando el usuario cierra sesión o cambia la contraseña.
     * 
     * @param usuario El usuario cuyos tokens serán revocados
     */
    void revocarTodosLosTokensDelUsuario(Usuario usuario);
    
    /**
     * Elimina todos los tokens expirados o revocados de la base de datos.
     * Esta operación puede programarse para ejecutarse periódicamente.
     */
    void eliminarTokensExpirados();
}