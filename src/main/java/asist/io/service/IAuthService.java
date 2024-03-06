package asist.io.service;

import asist.io.dto.usuarioDtos.UsuarioLoginDto;
import asist.io.dto.usuarioDtos.UsuarioLoginResDto;

public interface IAuthService {
    
    /**
     * Metodo encargado del logueo de un usuario
     * @param loginReq Datos del usuario para loguearse
     * @return Datos del usuario logueado (Token y un objeto usuarioDto con sus datos)
     */
    public UsuarioLoginResDto login(UsuarioLoginDto loginReq);
}
