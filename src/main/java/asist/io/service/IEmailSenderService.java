package asist.io.service;

import org.springframework.core.io.InputStreamSource;

import asist.io.dto.usuarioDtos.UsuarioDto;


public interface IEmailSenderService {
    public void enviarCorreo(String emisor, String para, String tema, String cuerpo, InputStreamSource imagen);
    public void generarCorreoValidacion(UsuarioDto usuario, String token );
    public void generarCorreoRecuperacion(UsuarioDto usuario, String token);
} 
