package asist.io.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.apache.log4j.Logger;
import asist.io.auth.JwtUtil;
import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.repository.UsuarioRepository;
import asist.io.service.IAuthService;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;

import java.util.Optional;

@Service
public class AuthServiceImpl implements IAuthService {
    private final Logger logger = Logger.getLogger(this.getClass());

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioServiceImpl usuarioService;
    private final IRefreshTokenService refreshTokenService;
    private final ITokenBlacklistService tokenBlacklistService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UsuarioServiceImpl usuarioService,
            IRefreshTokenService refreshTokenService,
            ITokenBlacklistService tokenBlacklistService,
            UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
        this.refreshTokenService = refreshTokenService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Método encargado del logueo de un usuario
     * @param loginReq Datos del usuario para loguearse
     * @return Datos del usuario logueado (Token y un objeto usuarioDto con sus datos)
     */
    @Override
    @Transactional
    public UsuarioGetLoginDTO login(UsuarioLoginDTO loginReq) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginReq.getCorreo(), loginReq.getContrasena()));
        } catch (BadCredentialsException e) {
            logger.error("Credenciales incorrectas, " + loginReq.getCorreo());
            throw new ModelException("Credenciales incorrectas");
        }

        String email = authentication.getName();
        Optional<Usuario> userOptional = Optional.ofNullable(usuarioRepository.findByCorreo(email));
        
        if (userOptional.isEmpty()) {
            throw new ModelException("Usuario no encontrado");
        }
        
        Usuario user = userOptional.get();
        
        // Creamos un token de acceso JWT
        String accessToken = jwtUtil.createAccessToken(user);
        
        // Creamos un refresh token
        RefreshToken refreshToken = refreshTokenService.crearRefreshToken(user);
        
        UsuarioGetLoginDTO loginRes = new UsuarioGetLoginDTO();
        loginRes.setUsuario(usuarioService.buscarUsuarioDto(email));
        loginRes.setToken(accessToken);
        loginRes.setRefreshToken(refreshToken.getToken());
        
        logger.info("Usuario autenticado correctamente: " + loginRes.getUsuario().getCorreo());
        return loginRes;
    }
    
    /**
     * Método encargado de renovar un token de acceso utilizando un refresh token
     * @param refreshTokenStr El refresh token para generar un nuevo token de acceso
     * @return Nuevo token de acceso
     */
    @Override
    public String refreshToken(String refreshTokenStr) {
        if (refreshTokenStr == null || refreshTokenStr.isEmpty()) {
            throw new ModelException("Refresh token no proporcionado");
        }
        
        logger.info("Intentando renovar token de acceso con refresh token");
        
        try {
            // Validamos el refresh token y generamos un nuevo token de acceso
            return jwtUtil.createAccessTokenFromRefreshToken(refreshTokenStr);
        } catch (ModelException e) {
            logger.error("Error al renovar token: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al renovar token: " + e.getMessage());
            throw new ModelException("Error al renovar el token de acceso");
        }
    }
    
    /**
     * Método encargado de cerrar la sesión de un usuario
     * @param accessToken Token de acceso a invalidar
     * @param refreshTokenStr Refresh token a invalidar (opcional)
     */
    @Override
    @Transactional
    public void logout(String accessToken, String refreshTokenStr) {
        logger.info("Cerrando sesión de usuario");
        
        // Invalidamos el token de acceso añadiéndolo a la lista negra
        if (accessToken != null && !accessToken.isEmpty()) {
            if (accessToken.startsWith("Bearer ")) {
                accessToken = accessToken.substring(7);
            }
            jwtUtil.invalidateToken(accessToken);
        }
        
        // Invalidamos el refresh token si se proporcionó
        if (refreshTokenStr != null && !refreshTokenStr.isEmpty()) {
            refreshTokenService.validarRefreshToken(refreshTokenStr).ifPresent(token -> {
                token.setRevocado(true);
                refreshTokenService.revocarTodosLosTokensDelUsuario(token.getUsuario());
                logger.info("Refresh token revocado para usuario: " + token.getUsuario().getCorreo());
            });
        }
    }
}
