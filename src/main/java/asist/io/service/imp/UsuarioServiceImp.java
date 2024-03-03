package asist.io.service.imp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import asist.io.dto.usuarioDtos.UsuarioDto;
import asist.io.dto.usuarioDtos.UsuarioRegDto;
import asist.io.entity.Usuario;
import asist.io.exceptions.ModelException;
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
     * Guarda un usuario en la base de datos
     * @param usuario
     * @throws ModelException
     * @return void
     */
    @Override
    public void guardarUsuario(UsuarioRegDto usuario) {
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
     * Elimina un usuario de la base de datos
     * @param correo
     * @throws ModelException
     * @return void
     */
    @Override
    public void eliminarUsuario(String correo) {
        logger.info("Eliminando usuario con correo: " + correo);
        buscarUsuario(correo);
        usuarioRepository.deleteByCorreo(correo);
    }

    /**
     * Actualiza un usuario en la base de datos
     * @param usuario
     * @throws ModelException
     * @return void
     */
    @Override
    public void actualizarUsuario(UsuarioDto usuario) {
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
    public UsuarioDto buscarUsuarioDto(String correo) {
        logger.info("Buscando usuario con correo: " + correo);
        Usuario usuario = buscarUsuario(correo);
        return usuarioMapper.toDto(usuario);
    }

    /**
     * Busca un usuario en la base de datos
     * @param correo
     * @return Usuario
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
     * Valida un usuario
     * @param token
     * @return void
     * @throws ModelException
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
     * Envia un correo de recuperacion de contraseña a un usuario
     * @param correo
     * @return void
     */
    public void enviarOlvideContrasena(String correo){
        logger.info(correo + " ha solicitado un cambio de contraseña");
        Usuario usuario = buscarUsuario(correo);
            emailSenderService.generarCorreoRecuperacion(usuarioMapper.toDto(usuario),
        tokenService.generarToken(usuario.getCorreo(), "RECUPERACION", usuario));
    }

    /**
     * Envia un correo de confirmacion a un usuario, esto en caso de que su token anterior
     * se haya vencido
     * @param correo
     * @return void
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
     * Cambia la contraseña de un usuario, validando el token y lo elimina
     * @param token
     * @param password
     */
    @Override
    public void cambiarContrasena(String token, String password) {
        tokenService.validarToken(token);
        Usuario usuario = tokenService.obtenerUsuario(token);
        usuario.setContrasena(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);
        tokenService.eliminarToken(token);
    }

    /**
     * Cambia la contraseña de un usuario
     * @param correo
     * @param password
     */
    @Override
    public void cambiarContrasenaLogueado(String correo, String contrasenaActual, String contrasenaNueva) {
        Usuario usuario = buscarUsuario(correo);
        if(!passwordEncoder.matches(contrasenaActual, usuario.getContrasena())){
            throw new ModelException("La contraseña actual no coincide con la contraseña ingresada");
        }else if(passwordEncoder.matches(contrasenaNueva, usuario.getContrasena())){
            throw new ModelException("La contraseña nueva no puede ser igual a la contraseña actual");
        }
        usuario.setContrasena(passwordEncoder.encode(contrasenaNueva));
        usuarioRepository.save(usuario);
    }


    @Override
    public String obtenerTokenPorCorreoTipo(String correo, String tipo) {
        return tokenService.obtenerTokenPorUsuarioTipo(usuarioRepository.findByCorreo(correo),tipo);
    }


    

    
}
