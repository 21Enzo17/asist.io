package asist.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import asist.io.dto.usuarioDtos.UsuarioLoginDto;
import asist.io.dto.usuarioDtos.UsuarioRegDto;
import asist.io.exceptions.ModelException;
import asist.io.service.IAuthService;
import asist.io.service.IUsuarioService;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class IAuthServiceTest {
    
    @Autowired
    private IAuthService target;
    @Autowired
    private IUsuarioService usuarioService;

    static UsuarioRegDto usuarioRegDto;

    static UsuarioLoginDto usuarioLoginDto;

    @BeforeEach
    public void setUp() {
        usuarioRegDto = new UsuarioRegDto();
        usuarioRegDto.setCorreo("enzo.meneghini@hotmail.com");
        usuarioRegDto.setNombre("Enzo Meneghini");
        usuarioRegDto.setContrasena("contrasena.1");
        usuarioService.guardarUsuario(usuarioRegDto);

        usuarioLoginDto = new UsuarioLoginDto();
        usuarioLoginDto.setCorreo("enzo.meneghini@hotmail.com");
        usuarioLoginDto.setContrasena("contrasena.1");

    }

    @AfterEach
    public void tearDown() {
        usuarioService.eliminarUsuario(usuarioRegDto.getCorreo());
        usuarioRegDto = null;
        usuarioLoginDto = null;
    }

    @Test
    @DisplayName("Test de login")
    public void testAutenticarUsuario(){
        assertNotNull(target.login(usuarioLoginDto).getToken());
        usuarioLoginDto.setContrasena("hola");
        assertThrows( ModelException.class, () -> target.login(usuarioLoginDto).getToken());
    }

}
