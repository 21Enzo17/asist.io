package asist.io.service.impl;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.repository.RefreshTokenRepository;
import asist.io.service.IRefreshTokenService;

/**
 * Implementación del servicio para la gestión de tokens de refresco.
 */
@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    /**
     * Logger para registrar las operaciones de esta clase.
     */
    private final Logger logger = Logger.getLogger(this.getClass());
    
    /**
     * Duración de validez de los tokens de refresco en días.
     * Por defecto son 7 días si no se especifica en las propiedades.
     */
    @Value("${jwt.refresh-token.expiration-days:7}")
    private long expirationDays;
    
    /**
     * Repositorio para acceder a los tokens de refresco en la base de datos.
     */
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Constructor para inyección de dependencias.
     * 
     * @param refreshTokenRepository Repositorio para los tokens de refresco
     */
    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * {@inheritDoc}
     * 
     * Crea un nuevo token de refresco para el usuario especificado.
     * Revoca todos los tokens anteriores de este usuario por seguridad.
     */
    @Override
    @Transactional
    public RefreshToken crearRefreshToken(Usuario usuario) {
        logger.info("Creando nuevo refresh token para usuario: " + usuario.getCorreo());
        
        // Primero revocamos todos los tokens anteriores del usuario
        revocarTodosLosTokensDelUsuario(usuario);
        
        // Creamos un nuevo token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setFechaExpiracion(Instant.now().plusMillis(TimeUnit.DAYS.toMillis(expirationDays)));
        refreshToken.setRevocado(false);
        
        // Guardamos en la base de datos
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        logger.info("Refresh token creado correctamente con expiración: " + savedToken.getFechaExpiracion());
        
        return savedToken;
    }

    /**
     * {@inheritDoc}
     * 
     * Valida un token de refresco verificando que exista, no esté expirado y no haya sido revocado.
     */
    @Override
    public Optional<RefreshToken> validarRefreshToken(String token) {
        logger.info("Validando refresh token");
        
        return refreshTokenRepository.findByToken(token)
                .filter(refreshToken -> {
                    // Si el token está expirado o revocado, registramos el motivo y retornamos vacío
                    if (refreshToken.esExpirado()) {
                        logger.warn("Refresh token expirado: " + token);
                        return false;
                    } else if (refreshToken.isRevocado()) {
                        logger.warn("Refresh token revocado: " + token);
                        return false;
                    }
                    // Token válido
                    return true;
                });
    }

    /**
     * {@inheritDoc}
     * 
     * Revoca todos los tokens de refresco del usuario.
     */
    @Override
    @Transactional
    public void revocarTodosLosTokensDelUsuario(Usuario usuario) {
        logger.info("Revocando todos los refresh tokens del usuario: " + usuario.getCorreo());
        refreshTokenRepository.revokeAllUserTokens(usuario);
    }

    /**
     * {@inheritDoc}
     * 
     * Elimina todos los tokens que están expirados o han sido revocados.
     */
    @Override
    @Transactional
    public void eliminarTokensExpirados() {
        logger.info("Eliminando tokens expirados y revocados");
        refreshTokenRepository.deleteAllExpiredOrRevoked(Instant.now());
    }
}