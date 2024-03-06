package asist.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import asist.io.dto.usuarioDtos.UsuarioDto;
import asist.io.service.IEmailSenderService;

@SpringBootTest
public class IEmailSenderServiceTest {
    @Autowired
    private IEmailSenderService target;

    static UsuarioDto usuarioDto;

    @BeforeEach
    public void setUp() {
        usuarioDto = new UsuarioDto();
        usuarioDto.setCorreo("enzo.meneghini@hotmail.com");
        usuarioDto.setNombre("Usuario de prueba");


    }

    @AfterEach
    public void tearDown() {
        usuarioDto = null;
    }

    @Test
    @DisplayName("Test de envio de correo")
    public void testEnviarCorreo(){
        target.enviarCorreo("poo2023@gmail.com", usuarioDto.getCorreo(), "Correo de Prueba", "Test", null);
    }

}
