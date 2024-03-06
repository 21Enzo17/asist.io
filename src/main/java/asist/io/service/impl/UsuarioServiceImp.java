package asist.io.service.impl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import asist.io.dto.passwordDTO.PasswordDTO;
import asist.io.dto.usuarioDTO.UsuarioCambioContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioRegDTO;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.mapper.UsuarioMapper;
import asist.io.repository.UsuarioRepository;
import asist.io.service.IEmailSenderService;
import asist.io.service.ITokenService;
import asist.io.service.IUsuarioService;

@Service
public class UsuarioServiceImp implements IUsuarioService {
    private final Logger logger =  Logger.getLogger(this.getClass());


    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private IEmailSenderService emailSenderService;
    @Autowired
    private ITokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UsuarioMapper usuarioMapper;

    /**
     * Metodo encargado de guardar un usuario.
     * @param usuario
     */
    @Override
    public void guardarUsuario(UsuarioRegDTO usuario) {
       Usuario usuarioRegistro = new Usuario();
        if(usuarioRepository.findByCorreo(usuario.getCorreo()) == null){
            logger.info("Guardando usuario con correo: " + usuario.getCorreo());
            usuarioRegistro = usuarioMapper.toEntity(usuario);
            usuarioRegistro.setVerificado(false);
            usuarioRepository.save(usuarioRegistro);
            emailSenderService.generarCorreoValidacion(usuarioMapper.toDto(usuarioRegistro), tokenService.generarToken(usuarioRegistro.getCorreo(), "VERIFICACION",usuarioRegistro));
        }else{
            throw new ModelException("Ya existe un usuario con este correo");
        }
    }

    /**
     * Metodo encargado de eliminar un usuario.
     * @param correo Correo del usuario que se va a eliminar.
     */
    @Override
    public void eliminarUsuario(String correo) {
        logger.info("Eliminando usuario con correo: " + correo);
        buscarUsuario(correo);
        usuarioRepository.deleteByCorreo(correo);
    }

    /**
     * Metodo encargado de actualizar un usuario.
     * @param usuario 
     * @return
     */
    @Override
    public void actualizarUsuario(UsuarioGetDTO usuario) {
        logger.info("Actualizando usuario con correo: " + usuario.getCorreo());
        buscarUsuario(usuario.getCorreo());
        Usuario usuarioActualizado = usuarioMapper.toEntity(usuario);
        usuarioActualizado.setContrasena(usuarioRepository.findByCorreo(usuario.getCorreo()).getContrasena());
        usuarioRepository.save(usuarioActualizado);
    }

    /**
     * Busca un usuario en la base de datos
     * @param correo
     * @return UsuarioDto
     */
    @Override
    public UsuarioGetDTO buscarUsuarioDto(String correo) {
        logger.info("Buscando usuario con correo: " + correo);
        Usuario usuario = buscarUsuario(correo);
        return usuarioMapper.toDto(usuario);
    }

    /**
     * Metodo encargado de buscar un usuario por correo.
     * @param correo
     * @return
     */
    @Override
    public Usuario buscarUsuario(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if(usuario == null){
            throw new ModelException("No existe un usuario con este correo");
        }
        return usuario;
    }

    /**
     * Metodo encargado de validar un usuario.
     * @param token 
     */
    @Override
    public void validarUsuario(String token) {
        tokenService.validarToken(token);
        Usuario usuario = tokenService.obtenerUsuario(token);
        usuario.setVerificado(true);
        usuarioRepository.save(usuario);
        tokenService.eliminarToken(token);
        logger.info(usuario.getCorreo() + " ha sido verificado con exito");
    }

    /**
     * Metodo encargado de enviar el correo de Recuperacion de contraseña a un usuario
     * @param correo
    */
    public void enviarOlvideContrasena(String correo){
        logger.info(correo + " ha solicitado un cambio de contraseña");
        Usuario usuario = buscarUsuario(correo);
            emailSenderService.generarCorreoRecuperacion(usuarioMapper.toDto(usuario),
        tokenService.generarToken(usuario.getCorreo(), "RECUPERACION", usuario));
    }

    /**
     * Metodo encargado de enviar el correo de Validacion de mail a un usuario
     * @param correo
    */
    @Override
    public void enviarCorreoConfirmacion(String correo) {
        Usuario usuario = buscarUsuario(correo);
        if(!usuario.isVerificado()){
            emailSenderService.generarCorreoValidacion(usuarioMapper.toDto(usuario), 
            tokenService.generarToken(usuario.getCorreo(), "VERIFICACION",usuario));
        }else{
            logger.error("El usuario ya ha sido verificado "+ correo);
            throw new ModelException("El usuario ya ha sido verificado");
        }
    }

    /**
     * Metodo encargado de cambiar la contraseña de un usuario.
     * @param token Token
     * @param password Contraseña
     */
    @Override
    public void cambiarContrasena(String token, PasswordDTO password) {
        tokenService.validarToken(token);
        Usuario usuario = tokenService.obtenerUsuario(token);
        usuario.setContrasena(passwordEncoder.encode(password.getPassword()));
        usuarioRepository.save(usuario);
        tokenService.eliminarToken(token);
    }

    /**
     * Metodo encargado de cambiar la contraseña de un usuario logueado.
     * @param usuarioCambio UsuarioCambioContraDTO contiene el correo, la contraseña actual y la nueva contraseña.
     */
    @Override
    public void cambiarContrasenaLogueado(UsuarioCambioContrasenaDTO usuarioCambio) {
        Usuario usuario = buscarUsuario(usuarioCambio.getCorreo());
        if(!passwordEncoder.matches(usuarioCambio.getContrasenaActual(), usuario.getContrasena())){
            throw new ModelException("La contraseña actual no coincide con la contraseña ingresada");
        }else if(passwordEncoder.matches(usuarioCambio.getContrasenaNueva().getPassword(), usuario.getContrasena())){
            throw new ModelException("La contraseña nueva no puede ser igual a la contraseña actual");
        }
        usuario.setContrasena(passwordEncoder.encode(usuarioCambio.getContrasenaNueva().getPassword()));
        usuarioRepository.save(usuario);
    }

    /**
     * Metodo encargado de obtener un token por correo y tipo.
     * @param correo 
     * @param tipo (VERIFICACION, RECUPERACION).
     * @return Token
     */
    @Override
    public String obtenerTokenPorCorreoTipo(String correo, String tipo) {
        return tokenService.obtenerTokenPorUsuarioTipo(usuarioRepository.findByCorreo(correo),tipo);
    }


    

    
}
