package asist.io.service;

import asist.io.dto.ContrasenaDTO.ContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioCambioContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioPatchDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.entity.Usuario;


public interface IUsuarioService {

    /**
     * Metodo encargado de guardar un usuario.
     * @param usuario
     */
    public void guardarUsuario(UsuarioPostDTO usuario);

    /**
     * Metodo encargado de eliminar un usuario.
     * @param correo Correo del usuario que se va a eliminar.
     * @param contrasena Contraseña del usuario que se va a eliminar.
     */
    public void eliminarUsuario(String correo, String contrasena);

    /**
     * Metodo encargado de actualizar un usuario.
     * @param usuario 
     * @return
     */
    public void actualizarUsuario(UsuarioPatchDTO usuario);


    /**
     * Metodo encargado de buscar un usuario por correo.
     * @param correo
     * @return
     */
    public UsuarioGetDTO buscarUsuarioDto(String correo);

    /**
     * Metodo encargado de buscar un usuario por correo.
     * @param correo
     * @return
     */
    public Usuario buscarUsuario(String correo);

    /**
     * Metodo encargado de validar un usuario.
     * @param token
     */
    public void validarUsuario(String token);

    /**
     * Metodo encargado de enviar el correo de Validacion de mail a un usuario
     * @param correo
    */
    public void enviarCorreoConfirmacion(String correo);

    /**
     * Metodo encargado de enviar el correo de Recuperacion de contraseña a un usuario
     * @param correo
    */
    public void enviarOlvideContrasena(String correo);

    /**
     * Metodo encargado de obtener un token por correo y tipo.
     * @param correo 
     * @param tipo (VERIFICACION, RECUPERACION).
     * @return Token
     */
    public String obtenerTokenPorCorreoTipo(String correo, String tipo);

    /**
     * Metodo encargado de cambiar la contraseña de un usuario.
     * @param token Token
     * @param password Contraseña
     */
    public void cambiarContrasena(String token, ContrasenaDTO password);

    /**
     * Metodo encargado de cambiar la contraseña de un usuario logueado.
     * @param correo 
     * @param contrasenaActual
     * @param contrasenaNueva
     */
    public void cambiarContrasenaLogueado(UsuarioCambioContrasenaDTO usuario);
} 
