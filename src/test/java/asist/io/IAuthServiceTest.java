package asist.io;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.service.IAuthService;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;
import asist.io.service.IUsuarioService;
import jakarta.transaction.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Pruebas específicas para el servicio de autenticación basadas en la interfaz IAuthService.
 * 
 * Estas pruebas verifican cada uno de los tres métodos definidos en IAuthService:
 * 1. login - Autenticación de usuario y generación de tokens
 * 2. refreshToken - Renovación de tokens de acceso
 * 3. logout - Invalidación de tokens de acceso y refresh
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class IAuthServiceTest {
    
    @Autowired
    private IAuthService authService;
    
    @Autowired
    private IUsuarioService usuarioService;
    
    @Autowired
    private IRefreshTokenService refreshTokenService;
    
    @Autowired
    private ITokenBlacklistService tokenBlacklistService;

    private UsuarioPostDTO usuarioRegDto;
    private UsuarioLoginDTO usuarioLoginDto;
    private List<String> testUsers = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        // Crear un usuario de prueba verificado
        usuarioRegDto = new UsuarioPostDTO();
        usuarioRegDto.setCorreo("test.user@example.com");
        usuarioRegDto.setNombre("Test User");
        usuarioRegDto.setContrasena("SecurePass.1");
        
        // Guardar usuario
        usuarioService.guardarUsuario(usuarioRegDto);
        
        // Buscar el usuario guardado y verificarlo
        Usuario usuario = usuarioService.buscarUsuario(usuarioRegDto.getCorreo());
        usuario.setVerificado(true);
        testUsers.add(usuario.getCorreo());
        
        // Preparar credenciales de login
        usuarioLoginDto = new UsuarioLoginDTO();
        usuarioLoginDto.setCorreo("test.user@example.com");
        usuarioLoginDto.setContrasena("SecurePass.1");
    }

    @AfterEach
    public void tearDown() {
        // Limpiar todos los usuarios de prueba creados
        for (String email : testUsers) {
            try {
                usuarioService.eliminarUsuario(email, "SecurePass.1");
            } catch (Exception e) {
                // Ignorar errores de limpieza
            }
        }
        testUsers.clear();
        usuarioRegDto = null;
        usuarioLoginDto = null;
    }

    // ================ PRUEBAS DEL MÉTODO LOGIN ================

    @Test
    @DisplayName("login: Con credenciales válidas debe devolver tokens y datos del usuario")
    public void testLogin_WithValidCredentials() {
        // Ejecutar login con credenciales correctas
        UsuarioGetLoginDTO response = authService.login(usuarioLoginDto);
        
        // Verificar respuesta
        assertNotNull(response, "La respuesta no debe ser nula");
        assertNotNull(response.getToken(), "El token de acceso debe existir");
        assertNotNull(response.getRefreshToken(), "El refresh token debe existir");
        assertNotNull(response.getUsuario(), "Los datos del usuario deben existir");
        assertEquals("test.user@example.com", response.getUsuario().getCorreo(), 
                "El correo del usuario debe coincidir");
        assertTrue(response.getUsuario().getVerificado(), 
                "El usuario debe estar verificado");
        
        // Verificar que el refresh token está en la base de datos
        assertTrue(refreshTokenService.existsByToken(response.getRefreshToken()),
                "El refresh token debe estar almacenado en la base de datos");
    }

    @Test
    @DisplayName("login: Con contraseña incorrecta debe lanzar excepción")
    public void testLogin_WithInvalidPassword() {
        // Preparar credenciales con contraseña incorrecta
        UsuarioLoginDTO invalidLogin = new UsuarioLoginDTO();
        invalidLogin.setCorreo("test.user@example.com");
        invalidLogin.setContrasena("WrongPassword123");
        
        // Verificar que se lanza excepción
        assertThrows(ModelException.class, () -> authService.login(invalidLogin),
                "Debe lanzar excepción con contraseña incorrecta");
    }
    
    @Test
    @DisplayName("login: Con usuario inexistente debe lanzar excepción")
    public void testLogin_WithNonexistentUser() {
        // Preparar credenciales con usuario inexistente
        UsuarioLoginDTO nonexistentUser = new UsuarioLoginDTO();
        nonexistentUser.setCorreo("nonexistent@example.com");
        nonexistentUser.setContrasena("SecurePass.1");
        
        // Verificar que se lanza excepción
        assertThrows(ModelException.class, () -> authService.login(nonexistentUser),
                "Debe lanzar excepción con usuario inexistente");
    }
    
    @Test
    @DisplayName("login: Con usuario no verificado debe devolver estado correcto")
    public void testLogin_WithUnverifiedUser() {
        // Crear usuario sin verificar
        UsuarioPostDTO unverifiedUser = new UsuarioPostDTO();
        unverifiedUser.setCorreo("unverified@example.com");
        unverifiedUser.setNombre("Unverified User");
        unverifiedUser.setContrasena("SecurePass.1");
        
        // Guardar usuario sin verificar
        usuarioService.guardarUsuario(unverifiedUser);
        testUsers.add(unverifiedUser.getCorreo());
        
        // Preparar login
        UsuarioLoginDTO loginDto = new UsuarioLoginDTO();
        loginDto.setCorreo("unverified@example.com");
        loginDto.setContrasena("SecurePass.1");
        
        // Ejecutar y verificar
        UsuarioGetLoginDTO response = authService.login(loginDto);
        assertNotNull(response, "Debe devolver respuesta aunque el usuario no esté verificado");
        assertFalse(response.getUsuario().getVerificado(), 
                "Debe indicar que el usuario no está verificado");
    }
    
    @Test
    @DisplayName("login: Múltiples logins del mismo usuario deben generar diferentes tokens")
    public void testLogin_MultipleLoginsOfSameUser() {
        // Primer login
        UsuarioGetLoginDTO firstLogin = authService.login(usuarioLoginDto);
        
        // Segundo login
        UsuarioGetLoginDTO secondLogin = authService.login(usuarioLoginDto);
        
        // Verificar que los tokens son diferentes
        assertNotEquals(firstLogin.getToken(), secondLogin.getToken(),
                "Los tokens de acceso deben ser diferentes en cada login");
        assertNotEquals(firstLogin.getRefreshToken(), secondLogin.getRefreshToken(),
                "Los refresh tokens deben ser diferentes en cada login");
        
        // Verificar que ambos tokens son válidos
        assertDoesNotThrow(() -> authService.refreshToken(firstLogin.getRefreshToken()),
                "El primer refresh token debe seguir siendo válido");
        assertDoesNotThrow(() -> authService.refreshToken(secondLogin.getRefreshToken()),
                "El segundo refresh token debe ser válido");
    }

    // ================ PRUEBAS DEL MÉTODO REFRESH TOKEN ================
    
    @Test
    @DisplayName("refreshToken: Con token válido debe devolver nuevo token de acceso")
    public void testRefreshToken_WithValidToken() {
        // Obtener un refresh token válido mediante login
        UsuarioGetLoginDTO loginResponse = authService.login(usuarioLoginDto);
        String refreshToken = loginResponse.getRefreshToken();
        String oldAccessToken = loginResponse.getToken();
        
        // Ejecutar refresh token
        String newAccessToken = authService.refreshToken(refreshToken);
        
        // Verificar nuevo token
        assertNotNull(newAccessToken, "El nuevo token de acceso no debe ser nulo");
        assertNotEquals(oldAccessToken, newAccessToken, 
                "El nuevo token debe ser diferente al anterior");
    }
    
    @Test
    @DisplayName("refreshToken: Con token nulo debe lanzar excepción")
    public void testRefreshToken_WithNullToken() {
        assertThrows(ModelException.class, () -> authService.refreshToken(null),
                "Debe lanzar excepción con token nulo");
    }
    
    @Test
    @DisplayName("refreshToken: Con token vacío debe lanzar excepción")
    public void testRefreshToken_WithEmptyToken() {
        assertThrows(ModelException.class, () -> authService.refreshToken(""),
                "Debe lanzar excepción con token vacío");
    }
    
    @Test
    @DisplayName("refreshToken: Con token inválido debe lanzar excepción")
    public void testRefreshToken_WithInvalidToken() {
        // Token aleatorio que no existe en el sistema
        String invalidToken = UUID.randomUUID().toString();
        
        assertThrows(ModelException.class, () -> authService.refreshToken(invalidToken),
                "Debe lanzar excepción con token inválido");
    }
    
    @Test
    @DisplayName("refreshToken: Con token expirado debe lanzar excepción")
    public void testRefreshToken_WithExpiredToken() {
        // Obtener un refresh token válido
        UsuarioGetLoginDTO loginResponse = authService.login(usuarioLoginDto);
        String refreshToken = loginResponse.getRefreshToken();
        
        // Expirar manualmente el token
        RefreshToken token = refreshTokenService.validarRefreshToken(refreshToken).get();
        token.setFechaExpiracion(Instant.now().minusSeconds(86400)); // Expirado hace 1 día
        
        // Intentar usar el token expirado
        assertThrows(ModelException.class, () -> authService.refreshToken(refreshToken),
                "Debe lanzar excepción con token expirado");
    }
    
    @Test
    @DisplayName("refreshToken: Con token revocado debe lanzar excepción")
    public void testRefreshToken_WithRevokedToken() {
        // Obtener un refresh token válido
        UsuarioGetLoginDTO loginResponse = authService.login(usuarioLoginDto);
        String refreshToken = loginResponse.getRefreshToken();
        
        // Revocar manualmente el token
        RefreshToken token = refreshTokenService.validarRefreshToken(refreshToken).get();
        token.setRevocado(true);
        
        // Intentar usar el token revocado
        assertThrows(ModelException.class, () -> authService.refreshToken(refreshToken),
                "Debe lanzar excepción con token revocado");
    }
    
    @Test
    @DisplayName("refreshToken: Múltiples renovaciones con el mismo token deben funcionar")
    public void testRefreshToken_WithMultipleRefreshes() {
        // Obtener un refresh token válido
        UsuarioGetLoginDTO loginResponse = authService.login(usuarioLoginDto);
        String refreshToken = loginResponse.getRefreshToken();
        
        // Realizar múltiples renovaciones con el mismo token
        String token1 = authService.refreshToken(refreshToken);
        String token2 = authService.refreshToken(refreshToken);
        String token3 = authService.refreshToken(refreshToken);
        
        // Verificar que todos los tokens son diferentes
        assertNotNull(token1);
        assertNotNull(token2);
        assertNotNull(token3);
        assertNotEquals(token1, token2);
        assertNotEquals(token2, token3);
        assertNotEquals(token1, token3);
    }

    // ================ PRUEBAS DEL MÉTODO LOGOUT ================
    
    @Test
    @DisplayName("logout: Con access token y refresh token debe invalidar ambos")
    public void testLogout_WithBothTokens() {
        // Obtener tokens mediante login
        UsuarioGetLoginDTO loginResponse = authService.login(usuarioLoginDto);
        String accessToken = loginResponse.getToken();
        String refreshToken = loginResponse.getRefreshToken();
        
        // Ejecutar logout
        authService.logout(accessToken, refreshToken);
        
        // Verificar que el access token está en la lista negra
        assertTrue(tokenBlacklistService.isBlacklisted(accessToken),
                "El access token debe estar en la lista negra después del logout");
        
        // Verificar que el refresh token ha sido revocado
        assertThrows(ModelException.class, () -> authService.refreshToken(refreshToken),
                "El refresh token debe ser inválido después del logout");
    }
    
    @Test
    @DisplayName("logout: Con solo access token debe añadirlo a la lista negra")
    public void testLogout_WithOnlyAccessToken() {
        // Obtener tokens mediante login
        UsuarioGetLoginDTO loginResponse = authService.login(usuarioLoginDto);
        String accessToken = loginResponse.getToken();
        
        // Ejecutar logout con solo access token
        authService.logout(accessToken, null);
        
        // Verificar que el access token está en la lista negra
        assertTrue(tokenBlacklistService.isBlacklisted(accessToken),
                "El access token debe estar en la lista negra después del logout");
    }
    
    @Test
    @DisplayName("logout: Con solo refresh token debe revocarlo")
    public void testLogout_WithOnlyRefreshToken() {
        // Obtener tokens mediante login
        UsuarioGetLoginDTO loginResponse = authService.login(usuarioLoginDto);
        String refreshToken = loginResponse.getRefreshToken();
        
        // Ejecutar logout con solo refresh token
        authService.logout(null, refreshToken);
        
        // Verificar que el refresh token ha sido revocado
        assertThrows(ModelException.class, () -> authService.refreshToken(refreshToken),
                "El refresh token debe ser inválido después del logout");
    }
    
    @Test
    @DisplayName("logout: Con token de formato Bearer debe funcionar correctamente")
    public void testLogout_WithBearerToken() {
        // Obtener tokens mediante login
        UsuarioGetLoginDTO loginResponse = authService.login(usuarioLoginDto);
        String accessToken = loginResponse.getToken();
        String refreshToken = loginResponse.getRefreshToken();
        
        // Añadir prefijo Bearer al token
        String bearerToken = "Bearer " + accessToken;
        
        // Ejecutar logout
        authService.logout(bearerToken, refreshToken);
        
        // Verificar que el access token está en la lista negra
        assertTrue(tokenBlacklistService.isBlacklisted(accessToken),
                "El access token debe estar en la lista negra después del logout");
        
        // Verificar que el refresh token ha sido revocado
        assertThrows(ModelException.class, () -> authService.refreshToken(refreshToken),
                "El refresh token debe ser inválido después del logout");
    }
    
    @Test
    @DisplayName("logout: Con tokens vacíos no debe lanzar excepción")
    public void testLogout_WithEmptyTokens() {
        // Verificar que no lanza excepción
        assertDoesNotThrow(() -> authService.logout("", ""),
                "No debe lanzar excepción con tokens vacíos");
        assertDoesNotThrow(() -> authService.logout(null, null),
                "No debe lanzar excepción con tokens nulos");
    }
}
