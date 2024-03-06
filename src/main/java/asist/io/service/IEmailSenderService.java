package asist.io.service;

import java.util.Optional;

import org.springframework.core.io.InputStreamSource;

import asist.io.dto.usuarioDTO.UsuarioGetDTO;


public interface IEmailSenderService {

    /**
     * Metodo encargado de enviar un correo electronico.
     * @param emisor 
     * @param para 
     * @param tema 
     * @param cuerpo Tenemos dos Bodys distintos, uno para la validacion de correo y otro para la recuperacion de contraseña
     * @param Optional <imagen> 
     */
    public void enviarCorreo(String emisor, String para, String tema, String cuerpo, Optional<InputStreamSource> imagen);

    /**
     * Metodo encargado de generar el correo de validacion de correo electronico.
     * @param usuario usuario al que se le va a enviar el correo
     * @param token token que se va a enviar para la validacion del correo
     */
    public void generarCorreoValidacion(UsuarioGetDTO usuario, String token );
    
    /**
     * Metodo encargado de generar el correo de recuperacion de contraseña.
     * @param usuario usuario al que se le va a enviar el correo
     * @param token token que se va a enviar para la recuperacion de contraseña
     */
    public void generarCorreoRecuperacion(UsuarioGetDTO usuario, String token);
} 
