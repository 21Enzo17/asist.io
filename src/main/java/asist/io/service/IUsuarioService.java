package asist.io.service;

import asist.io.dto.usuarioDtos.UsuarioDto;
import asist.io.dto.usuarioDtos.UsuarioRegDto;
import asist.io.entity.Usuario;


public interface IUsuarioService {

    public void guardarUsuario(UsuarioRegDto usuario);

    public void eliminarUsuario(String correo);

    public void actualizarUsuario(UsuarioDto usuario);

    public UsuarioDto buscarUsuarioDto(String correo);

    public Usuario buscarUsuario(String correo);

    public void validarUsuario(String token);

    public void enviarCorreoConfirmacion(String correo);

    public void enviarOlvideContrasena(String correo);

    public String obtenerTokenPorCorreoTipo(String correo, String tipo);

    public void cambiarContrasena(String token, String password);

    public void cambiarContrasenaLogueado(String correo, String contrasenaActual, String contrasenaNueva);
} 
