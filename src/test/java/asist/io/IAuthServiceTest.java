package asist.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
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

    static UsuarioPostDTO usuarioRegDto;

    static UsuarioLoginDTO usuarioLoginDto;


    @BeforeEach
    public void setUp() {
        usuarioRegDto = new UsuarioPostDTO();
        usuarioRegDto.setCorreo("fenix.meneghini@hotmail.com");
        usuarioRegDto.setNombre("Enzo Meneghini");
        usuarioRegDto.setContrasena("contrasena.1");
        usuarioService.guardarUsuario(usuarioRegDto);

        usuarioLoginDto = new UsuarioLoginDTO();
        usuarioLoginDto.setCorreo("fenix.meneghini@hotmail.com");
        usuarioLoginDto.setContrasena("contrasena.1");

    }

    @AfterEach
    public void tearDown() {
        usuarioService.eliminarUsuario(usuarioRegDto.getCorreo(),"contrasena.1");
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
