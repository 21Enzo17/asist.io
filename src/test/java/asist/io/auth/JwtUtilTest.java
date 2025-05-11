package asist.io.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;

import io.jsonwebtoken.Claims;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    
    @Mock
    private ITokenBlacklistService tokenBlacklistService;
    
    @Mock
    private IRefreshTokenService refreshTokenService;
    
    @Mock
    private Environment env;
    
    private Usuario testUser;
    
    @TempDir
    Path tempDir; // Directorio temporal para las pruebas de rotación de claves

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil(tokenBlacklistService, refreshTokenService, env);
        
        // Configurar rutas de claves para pruebas
        Path keysPath = tempDir.resolve("keys");
        try {
            Files.createDirectories(keysPath);
        } catch (IOException e) {
            fail("No se pudo crear el directorio temporal para claves: " + e.getMessage());
        }
        
        // Usar ReflectionTestUtils para establecer las rutas de claves para pruebas
        ReflectionTestUtils.setField(jwtUtil, "KEY_DIRECTORY", keysPath.toString());
        ReflectionTestUtils.setField(jwtUtil, "PRIVATE_KEY_FILE", "jwt_private.key");
        ReflectionTestUtils.setField(jwtUtil, "PUBLIC_KEY_FILE", "jwt_public.key");
        ReflectionTestUtils.setField(jwtUtil, "KEY_ARCHIVE_PREFIX", "archived_key_");
        
        // Forzar la inicialización con las nuevas rutas
        try {
            jwtUtil.initRSAKeys();
            jwtUtil.initJwtParsers();
        } catch (Exception e) {
            fail("Error al inicializar las claves para pruebas: " + e.getMessage());
        }
        
        // Crear un usuario de prueba
        testUser = new Usuario();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setNombre("Usuario Test");
        testUser.setCorreo("test@example.com");
    }

    @Test
    void createAccessToken_ShouldReturnValidToken() {
        // Ejecutar
        String token = jwtUtil.createAccessToken(testUser);
        
        // Verificar
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }
    
    @Test
    void validateClaims_ShouldReturnTrue_WhenClaimsAreValid() {
        // Preparar
        String token = jwtUtil.createAccessToken(testUser);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        
        // Ejecutar
        Claims claims = jwtUtil.resolveClaims(request);
        boolean isValid = jwtUtil.validateClaims(claims);
        
        // Verificar
        assertTrue(isValid);
        assertEquals("test@example.com", jwtUtil.getEmail(claims));
    }
    
    @Test
    void resolveToken_ShouldReturnToken_WhenHeaderIsValid() {
        // Preparar
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");
        
        // Ejecutar
        String token = jwtUtil.resolveToken(request);
        
        // Verificar
        assertEquals("test-token", token);
    }
    
    @Test
    void resolveToken_ShouldReturnNull_WhenHeaderIsMissing() {
        // Preparar
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        // Ejecutar
        String token = jwtUtil.resolveToken(request);
        
        // Verificar
        assertNull(token);
    }
    
    @Test
    void createAccessTokenFromRefreshToken_ShouldReturnNewToken() {
        // Preparar
        String refreshTokenStr = "valid-refresh-token";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(testUser);
        
        when(refreshTokenService.validarRefreshToken(refreshTokenStr))
            .thenReturn(Optional.of(refreshToken));
        
        // Ejecutar
        String newToken = jwtUtil.createAccessTokenFromRefreshToken(refreshTokenStr);
        
        // Verificar
        assertNotNull(newToken);
        verify(refreshTokenService, times(1)).validarRefreshToken(refreshTokenStr);
    }
    
    @Test
    void createAccessTokenFromRefreshToken_ShouldThrowException_WhenRefreshTokenIsInvalid() {
        // Preparar
        String invalidRefreshToken = "invalid-refresh-token";
        when(refreshTokenService.validarRefreshToken(invalidRefreshToken))
            .thenReturn(Optional.empty());
        
        // Ejecutar y verificar
        assertThrows(ModelException.class, () -> {
            jwtUtil.createAccessTokenFromRefreshToken(invalidRefreshToken);
        });
    }
    
    @Test
    void invalidateToken_ShouldAddTokenToBlacklist() {
        // Preparar
        String token = jwtUtil.createAccessToken(testUser);
        
        // Ejecutar
        jwtUtil.invalidateToken(token);
        
        // Verificar
        verify(tokenBlacklistService, times(1)).addToBlacklist(any(String.class), anyLong());
    }
    
    @Test
    void createAccessToken_ShouldUseRS256Algorithm() {
        // Ejecutar
        String token = jwtUtil.createAccessToken(testUser);
        
        // Verificar que el token usa RS256
        // Extraemos el header para verificar el algoritmo
        String[] parts = token.split("\\.");
        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        
        assertTrue(header.contains("RS256"), "El token debe usar el algoritmo RS256");
    }
    
    @Test
    void createAccessToken_ShouldIncludeNonce() {
        // Ejecutar
        String token = jwtUtil.createAccessToken(testUser);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        
        // Verificar
        Claims claims = jwtUtil.resolveClaims(request);
        assertNotNull(claims.get("nonce"), "El token debe incluir un nonce");
    }
    
    @Test
    void rotateKeys_ShouldGenerateNewKeysAndArchiveOld() throws IOException {
        // Preparar - guardar la ruta del archivo de clave pública antigua
        String keysDir = (String)ReflectionTestUtils.getField(jwtUtil, "KEY_DIRECTORY");
        File keysDirFile = new File(keysDir);
        int initialFileCount = keysDirFile.listFiles(f -> f.getName().startsWith("archived_key_")) == null ? 0 :
            keysDirFile.listFiles(f -> f.getName().startsWith("archived_key_")).length;
        
        // Crear un token con la clave actual
        String oldToken = jwtUtil.createAccessToken(testUser);
        
        // Ejecutar - rotar claves
        boolean result = jwtUtil.rotateKeys();
        
        // Verificar
        assertTrue(result, "La rotación de claves debe ser exitosa");
        
        // Verificar que hay un archivo de clave archivada adicional
        int newFileCount = keysDirFile.listFiles(f -> f.getName().startsWith("archived_key_")).length;
        assertEquals(initialFileCount + 1, newFileCount, "Debe haber una clave pública archivada adicional");
        
        // El nuevo token debe ser válido
        String newToken = jwtUtil.createAccessToken(testUser);
        assertNotNull(newToken);
        
        // Los tokens deben ser diferentes
        assertNotEquals(oldToken, newToken);
        
        // El token antiguo debe seguir siendo válido
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + oldToken);
        
        // Verificamos que no lanza excepción
        Claims claims = null;
        try {
            claims = jwtUtil.resolveClaims(request);
        } catch (Exception e) {
            fail("El token antiguo debería seguir siendo válido después de la rotación de claves");
        }
        
        assertTrue(jwtUtil.validateClaims(claims), "El token antiguo debe seguir siendo válido");
    }
    
    @Test
    void validateClaims_ShouldValidateIssuerAndAudience() {
        // Configuramos los valores esperados en el JwtUtil
        String expectedIssuer = "asist.io";
        String expectedAudience = "asist.io-client";
        
        ReflectionTestUtils.setField(jwtUtil, "issuer", expectedIssuer);
        ReflectionTestUtils.setField(jwtUtil, "audience", expectedAudience);
        
        // Preparar
        String token = jwtUtil.createAccessToken(testUser);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        
        // Ejecutar
        Claims claims = jwtUtil.resolveClaims(request);
        
        // Verificar
        assertEquals(expectedIssuer, claims.getIssuer(), "El emisor debe ser correcto");
        assertEquals(expectedAudience, claims.getAudience(), "El público debe ser correcto");
    }
    
    @Test
    void validateToken_ShouldReturnTrue_WhenTokenIsValid() {
        // Preparar
        String token = jwtUtil.createAccessToken(testUser);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        
        // Ejecutar
        boolean isValid = validateTokenHelper(request);
        
        // Verificar
        assertTrue(isValid, "El token debe ser válido");
    }
    
    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsMalformed() {
        // Preparar
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalid.token.format");
        
        // Ejecutar
        boolean isValid = validateTokenHelper(request);
        
        // Verificar
        assertFalse(isValid, "El token malformado debe ser inválido");
    }
    
    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInBlacklist() {
        // Preparar
        String token = jwtUtil.createAccessToken(testUser);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        
        when(tokenBlacklistService.isBlacklisted(any(String.class))).thenReturn(true);
        
        // Ejecutar
        boolean isValid = validateTokenHelper(request);
        
        // Verificar
        assertFalse(isValid, "El token en la lista negra debe ser inválido");
    }
    
    // Helper method para sustituir el método validateToken que ya no existe
    private boolean validateTokenHelper(HttpServletRequest request) {
        try {
            Claims claims = jwtUtil.resolveClaims(request);
            if (claims == null) return false;
            return jwtUtil.validateClaims(claims);
        } catch (Exception e) {
            return false;
        }
    }
    
    @Test
    void loadArchivedKeys_ShouldLoadKeysCorrectly() throws IOException {
        // Preparar - rotar claves para crear una clave archivada
        jwtUtil.rotateKeys();
        
        // Ejecutar - forzar una recarga de claves archivadas
        jwtUtil.loadArchivedKeys();
        
        // Verificar - indirectamente verificando que un token firmado con la clave anterior sigue siendo válido
        // Crear token con la clave actual
        String token = jwtUtil.createAccessToken(testUser);
        
        // Rotar claves de nuevo
        jwtUtil.rotateKeys();
        
        // El token anterior debe seguir siendo válido
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        Claims claims = jwtUtil.resolveClaims(request);
        assertTrue(jwtUtil.validateClaims(claims), "El token firmado con una clave anterior debe seguir siendo válido");
    }
}