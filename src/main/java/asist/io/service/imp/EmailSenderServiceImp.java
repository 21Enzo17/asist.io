package asist.io.service.imp;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import asist.io.dto.usuarioDtos.UsuarioDto;
import asist.io.exceptions.ModelException;
import asist.io.service.IEmailSenderService;
import asist.io.util.BodyGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailSenderServiceImp implements IEmailSenderService{
    static Logger logger = Logger.getLogger(IEmailSenderService.class);
    private final JavaMailSender mailSender;

    @Autowired
    private BodyGenerator bodyGenerator;
    

    public EmailSenderServiceImp(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Este metodo se encarga de setear y enviar el correo, seteando en true 
     * la etiqueta de contenido html, ademas de enviando la imagen para que 
     * el html pueda leerla en el correo. Envia una excepcion personalizada en caso
     * de no poder enviar el correo.
     * @param emisor
     * @param para
     * @param tema
     * @param cuerpo
     * @param imagen
     * @throws MessagingException
     * @catch MailException
     * @catch MessagingException
     * @catch IllegalArgumentException
     */
    @Override
    public void enviarCorreo(String emisor, String para, String tema, String cuerpo, InputStreamSource imagen) {
        logger.info("Enviando correo a:" + para);
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(emisor);
            helper.setTo(para);
            helper.setSubject(tema);
            helper.setText(cuerpo, true);
            //helper.addInline("logo", imagen, "image/png");

            
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
     * Este metodo se encarga de generar el cuerpo del correo de validacion y enviarlo de manera asicnronica, asi evitar largos tiempos de espera.
     * @param usuario
     * @param token
     */
    @Override
    public void generarCorreoValidacion(UsuarioDto usuario, String token ){
        new Thread(new Runnable(){
            public void run(){
                String cuerpo = bodyGenerator.generateBody(usuario.getNombre(), token);
                String tema = "Asist.io - Validación de correo";
                String para = usuario.getCorreo();
                enviarCorreo("poo2023correo@gmail.com", para, tema, cuerpo, null);
            }
        }).start();
    }


    /**
     * Este metodo se encarga de generar el cuerpo del correo de recuperacion y enviarlo de manera asicnronica, asi evitar largos tiempos de espera.
     * @param usuario
     * @param token
     */
    @Override
    public void generarCorreoRecuperacion(UsuarioDto usuario, String token){
        new Thread(new Runnable(){
            public void run(){
                String cuerpo = bodyGenerator.generatePasswordResetBody(usuario.getNombre(), token);
                String tema = "Asist.io - Recuperación de contraseña";
                String para = usuario.getCorreo();
                enviarCorreo("poo2023correo@gmail.com", para, tema, cuerpo, null);
            }
        }).start();
    }
}
