package asist.io.service.impl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import asist.io.dto.ContrasenaDTO.ContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioCambioContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioPatchDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.mapper.UsuarioMapper;
import asist.io.repository.UsuarioRepository;
import asist.io.service.IEmailSenderService;
import asist.io.service.ITokenService;
import asist.io.service.IUsuarioService;
import jakarta.transaction.Transactional;

@Service
public class UsuarioServiceImpl implements IUsuarioService {
    private final Logger logger =  Logger.getLogger(this.getClass());


    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private IEmailSenderService emailSenderService;
    @Autowired
    private ITokenService tokenService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Metodo encargado de guardar un usuario.
     * @param usuario
     */
    @Override
    public void guardarUsuario(UsuarioPostDTO usuario) {
       Usuario usuarioRegistro = new Usuario();
        if(usuarioRepository.findByCorreo(usuario.getCorreo()) == null){
            logger.info("Guardando usuario con correo: " + usuario.getCorreo());
            usuario.setContrasena((passwordEncoder.encode(usuario.getContrasena())));
            usuarioRegistro = UsuarioMapper.toEntity(usuario);
            usuarioRegistro.setVerificado(false);
            usuarioRepository.save(usuarioRegistro);
            emailSenderService.generarCorreoValidacion(UsuarioMapper.toDto(usuarioRegistro), tokenService.generarToken(usuarioRegistro.getCorreo(), "VERIFICACION",usuarioRegistro));
            logger.info ("Usuario registrado con exito, " + usuario.getCorreo());
        }else{
            throw new ModelException("Ya existe un usuario con este correo");
        }
    }

    /**
     * Metodo encargado de eliminar un usuario.
     * @param correo Correo del usuario que se va a eliminar.
     */
    @Override
    @Transactional
    public void eliminarUsuario(String correo, String contrasena) {
        Usuario usuario = buscarUsuario(correo);
        if(!passwordEncoder.matches(contrasena, usuario.getContrasena())){
            logger.error("La contraseña ingresada no coincide con la contraseña del usuario " + correo);
            throw new ModelException("La contraseña ingresada no coincide con la contraseña del usuario");
        }
        usuarioRepository.deleteByCorreo(correo);
        logger.info ("Usuario eliminado con exito, " + correo);
    }

    /**
     * Metodo encargado de actualizar un usuario.
     * @param usuario 
     * @return
     */
    @Override
    @SuppressWarnings("null")
    public void actualizarUsuario(UsuarioPatchDTO usuario) {
        Usuario usuarioEncontrado = usuarioRepository.findById(usuario.getId()).orElseThrow(() -> new ModelException("No existe un usuario con este id"));
        if(usuario.getNombre() != null){
            usuarioEncontrado.setNombre(usuario.getNombre());
        }
        usuarioRepository.save(usuarioEncontrado);
        logger.info("Usuario actualizado con exito, " + usuarioEncontrado.getCorreo());
    }

    /**
     * Busca un usuario en la base de datos
     * @param correo
     * @return UsuarioDto
     */
    @Override
    public UsuarioGetDTO buscarUsuarioDto(String correo) {
        Usuario usuario = buscarUsuario(correo);
        logger.info ("Usuario encontrado con exito, " + correo);
        return UsuarioMapper.toDto(usuario);
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
        logger.info ("Usuario encontrado con exito, " + correo);
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
        Usuario usuario = buscarUsuario(correo);
            emailSenderService.generarCorreoRecuperacion(UsuarioMapper.toDto(usuario),
        tokenService.generarToken(usuario.getCorreo(), "RECUPERACION", usuario));
        logger.info ("Correo de recuperacion enviado con exito a " + correo);
    }

    /**
     * Metodo encargado de enviar el correo de Validacion de mail a un usuario
     * @param correo
    */
    @Override
    public void enviarCorreoConfirmacion(String correo) {
        Usuario usuario = buscarUsuario(correo);
        if(!usuario.isVerificado()){
            emailSenderService.generarCorreoValidacion(UsuarioMapper.toDto(usuario), 
            tokenService.generarToken(usuario.getCorreo(), "VERIFICACION",usuario));
        }else{
            logger.error("El usuario ya ha sido verificado "+ correo);
            throw new ModelException("El usuario ya ha sido verificado");
        }
        logger.info ("Correo de validacion enviado con exito a " + correo);
    }

    /**
     * Metodo encargado de cambiar la contraseña de un usuario.
     * @param token Token
     * @param password Contraseña
     */
    @Override
    public void cambiarContrasena(String token, ContrasenaDTO password) {
        tokenService.validarToken(token);
        Usuario usuario = tokenService.obtenerUsuario(token);
        usuario.setContrasena(passwordEncoder.encode(password.getContrasena()));
        usuarioRepository.save(usuario);
        tokenService.eliminarToken(token);
        logger.info ("Contraseña cambiada con exito para " + usuario.getCorreo());
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
        }else if(passwordEncoder.matches(usuarioCambio.getContrasenaNueva(), usuario.getContrasena())){
            throw new ModelException("La contraseña nueva no puede ser igual a la contraseña actual");
        }
        usuario.setContrasena(passwordEncoder.encode(usuarioCambio.getContrasenaNueva()));
        usuarioRepository.save(usuario);
        logger.info ("Contraseña cambiada con exito para " + usuario.getCorreo());
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
