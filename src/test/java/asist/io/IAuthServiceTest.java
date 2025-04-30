package asist.io;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
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
    private UsuarioGetLoginDTO loginResponse;

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
        loginResponse = null;
    }

    @Test
    @DisplayName("Test de login")
    public void testAutenticarUsuario() {
        // Obtenemos la respuesta completa del login
        loginResponse = target.login(usuarioLoginDto);
        
        // Verificamos que devuelva un token de acceso
        assertNotNull(loginResponse.getToken(), "El token de acceso no debe ser nulo");
        
        // Verificamos que devuelva un refresh token
        assertNotNull(loginResponse.getRefreshToken(), "El refresh token no debe ser nulo");
        
        // Verificamos que falle con credenciales incorrectas
        usuarioLoginDto.setContrasena("hola");
        assertThrows(ModelException.class, () -> target.login(usuarioLoginDto));
    }
    
    @Test
    @DisplayName("Test de refresh token")
    public void testRefreshToken() {
        // Primero hacemos login para obtener un refresh token válido
        loginResponse = target.login(usuarioLoginDto);
        String refreshToken = loginResponse.getRefreshToken();
        
        // Verificamos que el refresh token no sea nulo
        assertNotNull(refreshToken, "El refresh token no debe ser nulo");
        
        // Probamos renovar el token de acceso con el refresh token
        String newAccessToken = target.refreshToken(refreshToken);
        
        // Verificamos que el nuevo token de acceso no sea nulo
        assertNotNull(newAccessToken, "El nuevo token de acceso no debe ser nulo");
        
        // Verificamos que falle con un refresh token inválido
        String invalidRefreshToken = "token-invalido";
        assertThrows(ModelException.class, () -> target.refreshToken(invalidRefreshToken));
        
        // Verificamos que falle si no se proporciona un refresh token
        assertThrows(ModelException.class, () -> target.refreshToken(null));
    }
    
    @Test
    @DisplayName("Test de logout")
    public void testLogout() {
        // Primero hacemos login para obtener tokens
        loginResponse = target.login(usuarioLoginDto);
        String accessToken = loginResponse.getToken();
        String refreshToken = loginResponse.getRefreshToken();
        
        // Ejecutamos el logout con ambos tokens
        target.logout(accessToken, refreshToken);
        
        // Verificamos que el refresh token ya no es válido intentando usarlo
        assertThrows(ModelException.class, () -> target.refreshToken(refreshToken));
    }
}
