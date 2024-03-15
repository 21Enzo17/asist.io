package asist.io.service.impl;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.exception.ModelException;
import asist.io.service.IEmailSenderService;
import asist.io.util.BodyGenerator;
import asist.io.util.Constantes;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailSenderServiceImpl implements IEmailSenderService{
    static Logger logger = Logger.getLogger(IEmailSenderService.class);
    private final JavaMailSender mailSender;

    @Autowired
    private BodyGenerator bodyGenerator;
    

    public EmailSenderServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Metodo encargado de enviar un correo electronico.
     * @param emisor 
     * @param para 
     * @param tema 
     * @param cuerpo Tenemos dos Bodys distintos, uno para la validacion de correo y otro para la recuperacion de contraseña
     * @param imagen Se deja la imagen para un futuro agregar logo a los correos, de momento se setea en null en los params
     */
    @Override
    @SuppressWarnings("null")
    public void enviarCorreo(String emisor, String para, String tema, String cuerpo, Optional<InputStreamSource> imagen) {
        logger.info("Enviando correo a: " + para);
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emisor);
            helper.setTo(para);
            helper.setSubject(tema);
            helper.setText(cuerpo, true);
            if(imagen.isPresent())
                helper.addInline("logo", imagen.get(), "image/png");
            mailSender.send(message);
        } catch (MailException e) {
            logger.error("Error al enviar el correo: " + e.getMessage());
            throw new ModelException("Error al enviar el correo, por favor intente de nuevo más tarde.");
        } catch (MessagingException e) {
            logger.error("Error al configurar el mensaje: " + e.getMessage());
            throw new ModelException("Error al configurar el mensaje, por favor verifique los datos ingresados.");
        } catch (IllegalArgumentException e) {
            logger.error("Argumento invalido: " + e.getMessage());
            throw new ModelException("Uno o más de los argumentos proporcionados son inválidos. Por favor verifique los datos ingresados.");
        }
        logger.info("Correo enviado a: " + para);
    }


    /**
     * Metodo encargado de generar el correo de validacion de correo electronico.
     * @param usuario usuario al que se le va a enviar el correo
     * @param token token que se va a enviar para la validacion del correo
     */
    @Override
    public void generarCorreoValidacion(UsuarioGetDTO usuario, String token ){
        new Thread(new Runnable(){
            public void run(){
                String cuerpo = bodyGenerator.generateBody(usuario.getNombre(), token);
                String tema = "Asist.io - Validación de correo";
                String para = usuario.getCorreo();
                enviarCorreo(Constantes.CORREO_SETEADO, para, tema, cuerpo, Optional.empty());
            }
        }).start();
    }


    /**
     * Metodo encargado de generar el correo de recuperacion de contraseña.
     * @param usuario usuario al que se le va a enviar el correo
     * @param token token que se va a enviar para la recuperacion de contraseña
     */
    @Override
    public void generarCorreoRecuperacion(UsuarioGetDTO usuario, String token){
        new Thread(new Runnable(){
            public void run(){
                String cuerpo = bodyGenerator.generatePasswordResetBody(usuario.getNombre(), token);
                String tema = "Asist.io - Recuperación de contraseña";
                String para = usuario.getCorreo();
                enviarCorreo(Constantes.CORREO_SETEADO, para, tema, cuerpo, Optional.empty());
            }
        }).start();
    }
}
