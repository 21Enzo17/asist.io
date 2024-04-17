package asist.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.ITokenService;
import asist.io.service.IUsuarioService;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class ITokenServiceTest {
    
    @Autowired
    private ITokenService target;

    @Autowired
    private IUsuarioService usuarioService;

    static UsuarioPostDTO usuarioRegDto;
    
    
    @BeforeEach
    public void setUp() {
        usuarioRegDto = new UsuarioPostDTO();
        usuarioRegDto.setCorreo("fenix.meneghini@hotmail.com");
        usuarioRegDto.setNombre("Enzo Meneghini");
        usuarioRegDto.setContrasena("contrasena.1");
        usuarioService.guardarUsuario(usuarioRegDto);
    }

    @AfterEach
    public void tearDown() {
        usuarioService.eliminarUsuario(usuarioRegDto.getCorreo(),"contrasena.1");
        usuarioRegDto = null;
    }

    /**
     * Como el token se genera al guardar el usuario, solo comprobamos que el token no sea nulo
     */
    @Test
    @DisplayName("Test de generacion de token")
    public void testGenerarToken(){
        assertNotNull(target.obtenerTokenPorUsuarioTipo(usuarioService.buscarUsuario(usuarioRegDto.getCorreo()), "VERIFICACION"));
    }

    @Test
    @DisplayName("Test de eliminacion de token")
    public void testEliminarToken(){
        assertNotNull(target.obtenerTokenPorUsuarioTipo(usuarioService.buscarUsuario(usuarioRegDto.getCorreo()), "VERIFICACION"));
        target.eliminarToken(target.obtenerTokenPorUsuarioTipo(usuarioService.buscarUsuario(usuarioRegDto.getCorreo()), "VERIFICACION"));
        assertThrows(ModelException.class, () -> target.obtenerTokenPorUsuarioTipo(usuarioService.buscarUsuario(usuarioRegDto.getCorreo()), "VERIFICACION"));
    }
}
