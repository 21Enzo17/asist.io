package asist.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import asist.io.dto.ContrasenaDTO.ContrasenaDTO;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioPatchDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IUsuarioService;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class IUsuarioServiceTest {

    @Autowired
    private IUsuarioService target;
    @Autowired
    private PasswordEncoder passwordEncoder;

    static UsuarioPostDTO usuarioRegDto;
    static UsuarioLoginDTO usuarioLoginDto;
    static UsuarioGetLoginDTO usuarioLoginResDto;
    static UsuarioGetDTO usuarioGetDto;
    static UsuarioPatchDTO usuarioPatchDto;

    static String contrasena = "contrasena.1";

     

    @BeforeEach
    public void setUp() {
        usuarioRegDto = new UsuarioPostDTO();
        usuarioRegDto.setCorreo("fenix.meneghini@hotmail.com");
        usuarioRegDto.setNombre("Enzo Meneghini");
        usuarioRegDto.setContrasena(contrasena);

        usuarioLoginDto = new UsuarioLoginDTO();
        usuarioLoginDto.setCorreo("fenix.meneghini@hotmail.com");
        usuarioLoginDto.setContrasena(contrasena);

        usuarioPatchDto = new UsuarioPatchDTO();
        usuarioPatchDto.setNombre("Test");


    }

    @AfterEach
    public void tearDown() {
        usuarioRegDto = null;
        usuarioLoginDto = null;
    }

    @Test
    @DisplayName("Test de registro de usuario")
    public void testGuardarUsuario(){
        assertThrows( ModelException.class, () -> target.buscarUsuario(usuarioRegDto.getCorreo()));

        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        System.out.println("CONTRASEÑAAAAA: " + contrasena);
        target.eliminarUsuario("fenix.meneghini@hotmail.com", contrasena);
    }

    @Test
    @DisplayName("Test de eliminar un usuario")
    public void testEliminarUsuario(){
        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        target.eliminarUsuario(usuarioRegDto.getCorreo(), "contrasena.1");
        assertThrows( ModelException.class, () -> target.buscarUsuario(usuarioRegDto.getCorreo()));
    }

    @Test
    @DisplayName("Test de actualizacion de usuario")
    public void testActualizarUsuario(){
        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        usuarioGetDto = target.buscarUsuarioDto(usuarioRegDto.getCorreo());
        usuarioGetDto.setNombre("Enzo Meneghini");
        usuarioPatchDto.setId(usuarioGetDto.getId());
        target.actualizarUsuario(usuarioPatchDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getNombre(), usuarioPatchDto.getNombre());
        target.eliminarUsuario(usuarioRegDto.getCorreo(),contrasena);
    }

    @Test
    @DisplayName("Test de busqueda de usuario")
    public void testBuscarUsuario(){
        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        target.eliminarUsuario(usuarioRegDto.getCorreo(),contrasena);
    }

    @Test
    @DisplayName("Test de Busqueda de Usuario por Correo")
    public void testBuscarUsuarioPorCorreo(){
        target.guardarUsuario(usuarioRegDto);
        assertEquals(target.buscarUsuario(usuarioRegDto.getCorreo()).getCorreo(), usuarioRegDto.getCorreo());
        target.eliminarUsuario(usuarioRegDto.getCorreo(),contrasena);
    }

    @Test
    @DisplayName("Test de Validacion de un usuario")
    public void testValidarUsuario(){
        target.guardarUsuario(usuarioRegDto);
        target.validarUsuario(target.obtenerTokenPorCorreoTipo(usuarioRegDto.getCorreo(),"VERIFICACION"));
        assertEquals(target.buscarUsuarioDto(usuarioRegDto.getCorreo()).getCorreo(), usuarioLoginDto.getCorreo());
        target.eliminarUsuario(usuarioRegDto.getCorreo(),contrasena);
    }

    @Test
    @DisplayName("Test de cambio de contraseña")
    public void testEnviarCorreoConfirmacion(){
        target.guardarUsuario(usuarioRegDto);
        target.enviarOlvideContrasena(usuarioRegDto.getCorreo());
        target.cambiarContrasena(target.obtenerTokenPorCorreoTipo(usuarioRegDto.getCorreo(),"RECUPERACION"), new ContrasenaDTO("contrasena.2"));
        assertTrue(passwordEncoder.matches("contrasena.2", target.buscarUsuario(usuarioRegDto.getCorreo()).getContrasena()));
        target.eliminarUsuario(usuarioRegDto.getCorreo(),"contrasena.2");
    }


    

    
}
