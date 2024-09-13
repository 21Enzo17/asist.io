package asist.io.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BodyGenerator {
    @Value("${cors.allowed-origins}")
    public static String SITIO_SETEADO;
    private static final String VALIDACION = SITIO_SETEADO + "/auth/verify-email/";
    private static final String RESET_PASSWORD = SITIO_SETEADO + "/auth/reset-password/";

    
    /**
     * Metodo que genera el cuerpo del correo de validacion de cuenta
     * @param nombre del usuario
     * @param token de validacion
     * @return el cuerpo del correo
     */
    public String generateBody(String nombre, String token) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f4f4f4;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 80%;\n" +
                "            margin: auto;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        .main {\n" +
                "            padding: 20px;\n" +
                "            background-color: white;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            color: white;\n" +
                "            background-color: #3498db;\n" +
                "            padding: 10px 20px;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 3px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"main\">\n" +
                "            <h2>¡Bienvenido a Asist.io, " + nombre + "!</h2>\n" +
                "            <p>Para completar el registro en Asist.io, por favor haga click en el siguiente enlace:</p>\n" +
                "            <a href=\"" + VALIDACION + token + "\" class=\"button\">Validar registro</a>\n" +
                "            <p>Si no puede hacer click en el enlace, por favor copie y pegue la siguiente dirección en su navegador:</p>\n" +
                "            <p>" + VALIDACION + token + "</p>\n" +
                "            <p>Gracias por confiar en nosotros.</p>\n" +
                "            <p>Atentamente,</p>\n" +
                "            <p>El equipo de ASIST</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Metodo que genera el cuerpo del correo de restablecimiento de contraseña
     * @param nombre del usuario
     * @param token de recuperacion
     * @return el cuerpo del correo
     */
    public String generatePasswordResetBody(String nombre, String token) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            background-color: #f4f4f4;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 80%;\n" +
                "            margin: auto;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        .main {\n" +
                "            padding: 20px;\n" +
                "            background-color: white;\n" +
                "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            color: white;\n" +
                "            background-color: #3498db;\n" +
                "            padding: 10px 20px;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 3px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"main\">\n" +
                "            <h2>Hola, " + nombre + "!</h2>\n" +
                "            <p>Hemos recibido una solicitud para restablecer tu contraseña en Asist.io. Si no hiciste esta solicitud, puedes ignorar este correo electrónico.</p>\n" +
                "            <p>Para restablecer tu contraseña, por favor haz click en el siguiente enlace:</p>\n" +
                "            <a href=\""+ RESET_PASSWORD + token + "\" class=\"button\">Restablecer contraseña</a>\n" +
                "            <p>Si no puedes hacer click en el enlace, por favor copia y pega la siguiente dirección en tu navegador:</p>\n" +
                "            <p>" + RESET_PASSWORD + token + "</p>\n" +
                "            <p>Gracias por confiar en nosotros.</p>\n" +
                "            <p>Atentamente,</p>\n" +
                "            <p>El equipo de ASIST</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
    


}
