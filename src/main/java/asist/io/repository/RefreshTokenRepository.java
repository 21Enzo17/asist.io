package asist.io.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import jakarta.transaction.Transactional;

/**
 * Repositorio para operaciones de base de datos con tokens de refresco.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    
    /**
     * Busca un token de refresco por su valor.
     * 
     * @param token Valor del token de refresco a buscar
     * @return Un Optional que contiene el token si existe
     */
    Optional<RefreshToken> findByToken(String token);
    
    /**
     * Revoca todos los tokens de refresco de un usuario específico.
     * Esto es útil cuando el usuario cierra sesión o cambia la contraseña.
     * 
     * @param usuario El usuario cuyos tokens serán revocados
     */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken r SET r.revocado = true WHERE r.usuario = :usuario AND r.revocado = false")
    void revokeAllUserTokens(Usuario usuario);
    
    /**
     * Elimina todos los tokens que están expirados o han sido revocados.
     * Esta operación se puede programar para ejecutarse periódicamente.
     * 
     * @param now La fecha y hora actual
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken r WHERE r.revocado = true OR r.fechaExpiracion < :now")
    void deleteAllExpiredOrRevoked(Instant now);
    
    /**
     * Cuenta el número de tokens de refresco activos que tiene un usuario.
     * 
     * @param usuario El usuario para el que se contarán los tokens
     * @return El número de tokens activos
     */
    @Query("SELECT COUNT(r) FROM RefreshToken r WHERE r.usuario = :usuario AND r.revocado = false AND r.fechaExpiracion > CURRENT_TIMESTAMP")
    long countActiveTokensByUser(Usuario usuario);
}