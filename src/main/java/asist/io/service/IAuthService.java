package asist.io.service;

import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;

public interface IAuthService {
    
    /**
     * Método encargado del logueo de un usuario
     * @param loginReq Datos del usuario para loguearse
     * @return Datos del usuario logueado (Token de acceso, refresh token y un objeto usuarioDto con sus datos)
     */
    UsuarioGetLoginDTO login(UsuarioLoginDTO loginReq);
    
    /**
     * Método encargado de renovar un token de acceso utilizando un refresh token
     * @param refreshToken El refresh token para generar un nuevo token de acceso
     * @return Nuevo token de acceso
     */
    String refreshToken(String refreshToken);
    
    /**
     * Método encargado de cerrar la sesión de un usuario
     * @param accessToken Token de acceso a invalidar
     * @param refreshToken Refresh token a invalidar (opcional)
     */
    void logout(String accessToken, String refreshToken);
}
