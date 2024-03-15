package asist.io.service;

import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;

public interface IAuthService {
    
    /**
     * Metodo encargado del logueo de un usuario
     * @param loginReq Datos del usuario para loguearse
     * @return Datos del usuario logueado (Token y un objeto usuarioDto con sus datos)
     */
    public UsuarioGetLoginDTO login(UsuarioLoginDTO loginReq);
}
