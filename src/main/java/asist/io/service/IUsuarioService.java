package asist.io.service;

import asist.io.dto.usuarioDtos.UsuarioDto;
import asist.io.dto.usuarioDtos.UsuarioRegDto;


public interface IUsuarioService {

    public void guardarUsuario(UsuarioRegDto usuario);

    public void eliminarUsuario(String correo);

    public void actualizarUsuario(UsuarioDto usuario);

    public UsuarioDto buscarUsuario(String correo);

    
} 
