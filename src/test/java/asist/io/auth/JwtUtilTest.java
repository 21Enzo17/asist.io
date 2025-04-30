package asist.io.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    
    @Mock
    private ITokenBlacklistService tokenBlacklistService;
    
    @Mock
    private IRefreshTokenService refreshTokenService;
    
    private Usuario testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtUtil = new JwtUtil(tokenBlacklistService, refreshTokenService);
        
        // Crear un usuario de prueba
        testUser = new Usuario();
        testUser.setId(UUID.randomUUID().toString()); // Corregido a UUID en lugar de Long
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
        assertFalse(header.contains("HS512"), "El token no debe usar el algoritmo HS512");
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
    void rotateKeys_ShouldGenerateNewKeys() {
        // Preparar - crear un token con la clave actual
        String oldToken = jwtUtil.createAccessToken(testUser);
        
        // Ejecutar - rotar claves
        boolean result = jwtUtil.rotateKeys();
        
        // Verificar
        assertTrue(result, "La rotación de claves debe ser exitosa");
        
        // El nuevo token debe ser válido
        String newToken = jwtUtil.createAccessToken(testUser);
        assertNotNull(newToken);
        
        // Los tokens deben ser diferentes debido a las diferentes claves y nonce
        assertNotEquals(oldToken, newToken);
    }
    
    @Test
    void validateClaims_ShouldValidateIssuerAndAudience() {
        // Preparar
        String token = jwtUtil.createAccessToken(testUser);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        
        // Ejecutar
        Claims claims = jwtUtil.resolveClaims(request);
        
        // Verificar
        assertEquals("asist.io", claims.getIssuer(), "El issuer debe ser correcto");
        assertEquals("asist.io-client", claims.getAudience(), "El audience debe ser correcto");
    }
    
    @Test
    void tokenExpiration_ShouldBeSetTo15Minutes() {
        // Preparar
        String token = jwtUtil.createAccessToken(testUser);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        
        // Ejecutar
        Claims claims = jwtUtil.resolveClaims(request);
        
        // Verificar que la diferencia entre issued at y expiration es 15 minutos (900 segundos)
        long issuedAtMillis = claims.getIssuedAt().getTime();
        long expirationMillis = claims.getExpiration().getTime();
        long differenceSeconds = (expirationMillis - issuedAtMillis) / 1000;
        
        // Permitimos un pequeño margen de 5 segundos para la ejecución de la prueba
        assertTrue(differenceSeconds >= 895 && differenceSeconds <= 905, 
                   "El token debe expirar en aproximadamente 15 minutos (900 segundos)");
    }
}