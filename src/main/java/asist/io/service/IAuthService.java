package asist.io.service;

import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioLoginResDTO;

public interface IAuthService {
    
    /**
     * Metodo encargado del logueo de un usuario
     * @param loginReq Datos del usuario para loguearse
     * @return Datos del usuario logueado (Token y un objeto usuarioDto con sus datos)
     */
    public UsuarioLoginResDTO login(UsuarioLoginDTO loginReq);
}
