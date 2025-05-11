package asist.io.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import asist.io.entity.Usuario;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Pruebas para el mecanismo de rotación de claves JWT.
 */
public class JwtKeyRotationTest {
    
    private JwtUtil jwtUtil;
    
    @Mock
    private ITokenBlacklistService tokenBlacklistService;
    
    @Mock
    private IRefreshTokenService refreshTokenService;
    
    @Mock
    private Environment env;
    
    @TempDir
    Path tempDir; // Directorio temporal para las pruebas de claves
    
    private Usuario testUser;
    private String tokenBeforeRotation;
    
    /**
     * Configuración inicial antes de cada prueba
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil(tokenBlacklistService, refreshTokenService, env);
        
        // Configurar directorio temporal para las claves
        Path keysPath = tempDir.resolve("keys");
        try {
            Files.createDirectories(keysPath);
        } catch (IOException e) {
            fail("No se pudo crear el directorio temporal para claves: " + e.getMessage());
        }
        
        // Configurar JwtUtil para usar el directorio temporal
        ReflectionTestUtils.setField(jwtUtil, "KEY_DIRECTORY", keysPath.toString());
        ReflectionTestUtils.setField(jwtUtil, "PRIVATE_KEY_FILE", "jwt_private.key");
        ReflectionTestUtils.setField(jwtUtil, "PUBLIC_KEY_FILE", "jwt_public.key");
        ReflectionTestUtils.setField(jwtUtil, "KEY_ARCHIVE_PREFIX", "archived_key_");
        ReflectionTestUtils.setField(jwtUtil, "MAX_PREVIOUS_KEYS", 3); // Valor para prueba
        
        // Iniciar manualmente JwtUtil con la nueva configuración
        try {
            jwtUtil.initRSAKeys();
            jwtUtil.initJwtParsers();
        } catch (Exception e) {
            fail("Error al inicializar JwtUtil: " + e.getMessage());
        }
        
        // Configurar mock de TokenBlacklistService
        when(tokenBlacklistService.isBlacklisted(any())).thenReturn(false);
        
        // Configurar usuario de prueba
        testUser = new Usuario();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setNombre("Test User");
        testUser.setCorreo("test@example.com");
    }
    
    /**
     * Prueba que un token generado antes de la rotación sigue siendo válido después
     */
    @Test
    public void testTokensValidityAfterRotation() {
        // Generar un token antes de la rotación
        tokenBeforeRotation = jwtUtil.createAccessToken(testUser);
        assertNotNull(tokenBeforeRotation, "El token debe generarse correctamente");
        
        // Rotar las claves
        boolean rotationSuccess = jwtUtil.rotateKeys();
        assertTrue(rotationSuccess, "La rotación de claves debe ser exitosa");
        
        // Generar un nuevo token después de la rotación
        String tokenAfterRotation = jwtUtil.createAccessToken(testUser);
        assertNotNull(tokenAfterRotation, "El token post-rotación debe generarse correctamente");
        
        // Verificar que ambos tokens son diferentes
        assertNotEquals(tokenBeforeRotation, tokenAfterRotation, 
                "Los tokens antes y después de la rotación deben ser diferentes");
        
        // Verificar que el token anterior sigue siendo válido
        HttpServletRequest mockRequestOld = createMockRequestWithToken(tokenBeforeRotation);
        Claims claimsOld = jwtUtil.resolveClaims(mockRequestOld);
        assertTrue(jwtUtil.validateClaims(claimsOld), 
                "El token generado antes de la rotación debe seguir siendo válido");
        
        // Verificar que el nuevo token también es válido
        HttpServletRequest mockRequestNew = createMockRequestWithToken(tokenAfterRotation);
        Claims claimsNew = jwtUtil.resolveClaims(mockRequestNew);
        assertTrue(jwtUtil.validateClaims(claimsNew),
                "El token generado después de la rotación debe ser válido");
    }
    
    /**
     * Prueba que el sistema mantiene solo un número limitado de claves archivadas
     */
    @Test
    public void testKeyLimitEnforcement() throws Exception {
        // Obtener el directorio de claves
        String keysDirPath = (String)ReflectionTestUtils.getField(jwtUtil, "KEY_DIRECTORY");
        File keysDir = new File(keysDirPath);
        int maxPreviousKeys = (int)ReflectionTestUtils.getField(jwtUtil, "MAX_PREVIOUS_KEYS");
        
        // Realizar varias rotaciones (más que el límite permitido)
        int rotationsToPerform = maxPreviousKeys + 2;
        String[] generatedTokens = new String[rotationsToPerform];
        
        // Generar un token con la clave inicial y luego rotar varias veces
        generatedTokens[0] = jwtUtil.createAccessToken(testUser);
        
        for (int i = 1; i < rotationsToPerform; i++) {
            jwtUtil.rotateKeys();
            generatedTokens[i] = jwtUtil.createAccessToken(testUser);
        }
        
        // Verificar que el número de archivos de claves archivadas no excede el máximo
        File[] archivedKeyFiles = keysDir.listFiles((dir, name) -> 
            name.startsWith((String)ReflectionTestUtils.getField(jwtUtil, "KEY_ARCHIVE_PREFIX")));
        
        assertNotNull(archivedKeyFiles, "Debe haber archivos de claves archivadas");
        assertTrue(archivedKeyFiles.length <= maxPreviousKeys, 
                "El número de claves archivadas no debe exceder el máximo configurado");
        
        // Verificar que el token más antiguo ya no es válido (debería haberse eliminado su clave)
        HttpServletRequest oldestRequest = createMockRequestWithToken(generatedTokens[0]);
        
        try {
            jwtUtil.resolveClaims(oldestRequest);
            fail("Se esperaba una excepción para el token más antiguo que debería ser inválido");
        } catch (Exception e) {
            // Esperamos una excepción aquí, ya que la clave más antigua debería haberse eliminado
            assertTrue(true);
        }
        
        // Verificar que los tokens más recientes siguen siendo válidos
        for (int i = generatedTokens.length - maxPreviousKeys; i < generatedTokens.length; i++) {
            HttpServletRequest recentRequest = createMockRequestWithToken(generatedTokens[i]);
            Claims claims = null;
            try {
                claims = jwtUtil.resolveClaims(recentRequest);
                assertNotNull(claims, "Las claims para un token reciente deben ser válidas");
            } catch (Exception e) {
                fail("No se esperaba una excepción para tokens recientes: " + e.getMessage());
            }
        }
    }
    
    /**
     * Crea una solicitud mock con un token JWT
     */
    private HttpServletRequest createMockRequestWithToken(String token) {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getHeader("Authorization")).thenReturn("Bearer " + token);
        return mockRequest;
    }
}