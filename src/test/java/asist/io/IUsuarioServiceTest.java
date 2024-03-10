package asist.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import asist.io.dto.passwordDTO.PasswordDTO;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioLoginResDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IUsuarioService;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class IUsuarioServiceTest {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private IUsuarioService target;
    @Autowired
    private PasswordEncoder passwordEncoder;

    static UsuarioPostDTO usuarioRegDto;
    static UsuarioLoginDTO usuarioLoginDto;
    static UsuarioLoginResDTO usuarioLoginResDto;
    static UsuarioGetDTO usuarioGetDto;
    static PasswordDTO passwordDto;
     

    @BeforeEach
    public void setUp() {
        usuarioRegDto = new UsuarioPostDTO();
        usuarioRegDto.setCorreo("enzo.meneghini@hotmail.com");
        usuarioRegDto.setNombre("Enzo Meneghini");
        usuarioRegDto.setContrasena(new PasswordDTO("contrasena.1"));

        usuarioLoginDto = new UsuarioLoginDTO();
        usuarioLoginDto.setCorreo("enzo.meneghini@hotmail.com");
        usuarioLoginDto.setContrasena("contrasena.1");


    }

    @AfterEach
    public void tearDown() {
        usuarioRegDto = null;
        usuarioLoginDto = null;
    }

    @Test
    @DisplayName("Test de registro de usuario")
    public void testGuardarUsuario(){
        logger.info("Iniciando test de registro de usuario");
        assertThrows( ModelException.class, () -> target.buscarUsuario(usuarioRegDto.getCorreo()));

        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        target.eliminarUsuario("enzo.meneghini@hotmail.com");
    }

    @Test
    @DisplayName("Test de eliminar un usuario")
    public void testEliminarUsuario(){
        logger.info("Iniciando test de eliminacion de usuario");
        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        target.eliminarUsuario(usuarioRegDto.getCorreo());
        assertThrows( ModelException.class, () -> target.buscarUsuario(usuarioRegDto.getCorreo()));
    }

    @Test
    @DisplayName("Test de actualizacion de usuario")
    public void testActualizarUsuario(){
        logger.info("Iniciando test de actualizacion de usuario");
        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        usuarioGetDto = target.buscarUsuarioDto(usuarioRegDto.getCorreo());
        usuarioGetDto.setNombre("Enzo Meneghini");
        target.actualizarUsuario(usuarioGetDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getNombre(), usuarioGetDto.getNombre());
        target.eliminarUsuario(usuarioRegDto.getCorreo());
    }

    @Test
    @DisplayName("Test de busqueda de usuario")
    public void testBuscarUsuario(){
        logger.info("Iniciando test de busqueda de usuario");
        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        target.eliminarUsuario(usuarioRegDto.getCorreo());
    }

    @Test
    @DisplayName("Test de Busqueda de Usuario por Correo")
    public void testBuscarUsuarioPorCorreo(){
        logger.info("Iniciando test de busqueda de usuario por correo");
        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuario(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        target.eliminarUsuario(usuarioRegDto.getCorreo());
    }

    @Test
    @DisplayName("Test de Validacion de un usuario")
    public void testValidarUsuario(){
        logger.info("Iniciando test de validacion de usuario");
        target.guardarUsuario(usuarioRegDto);
        target.validarUsuario(target.obtenerTokenPorCorreoTipo(usuarioRegDto.getCorreo(),"VERIFICACION"));
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioLoginDto.getCorreo());
        target.eliminarUsuario(usuarioRegDto.getCorreo());
    }

    @Test
    @DisplayName("Test de cambio de contraseña")
    public void testEnviarCorreoConfirmacion(){
        logger.info("Iniciando test de cambio de contraseña");
        target.guardarUsuario(usuarioRegDto);
        target.enviarOlvideContrasena(usuarioRegDto.getCorreo());
        target.cambiarContrasena(target.obtenerTokenPorCorreoTipo(usuarioRegDto.getCorreo(),"RECUPERACION"), new PasswordDTO("contrasena.2"));
        assertTrue(passwordEncoder.matches("contrasena.2", target.buscarUsuario(usuarioRegDto.getCorreo()).getContrasena()));
        target.eliminarUsuario(usuarioRegDto.getCorreo());
    }


    

    
}
