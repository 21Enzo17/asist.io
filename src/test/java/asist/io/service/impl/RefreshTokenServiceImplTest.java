package asist.io.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.repository.RefreshTokenRepository;

class RefreshTokenServiceImplTest {

    private RefreshTokenServiceImpl refreshTokenService;
    
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    
    private Usuario testUser;
    private RefreshToken validToken;
    private RefreshToken expiredToken;
    private RefreshToken revokedToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refreshTokenService = new RefreshTokenServiceImpl(refreshTokenRepository);
        
        // Configurar usuario de prueba
        testUser = new Usuario();
        testUser.setId(UUID.randomUUID().toString());
        testUser.setNombre("Usuario Test");
        testUser.setCorreo("test@example.com");
        
        // Configurar tokens de prueba
        validToken = new RefreshToken();
        validToken.setId(UUID.randomUUID()); // Usando UUID en lugar de Long
        validToken.setToken("valid-token");
        validToken.setUsuario(testUser);
        validToken.setFechaExpiracion(Instant.now().plusSeconds(3600)); // Expira en 1 hora
        validToken.setRevocado(false);
        
        expiredToken = new RefreshToken();
        expiredToken.setId(UUID.randomUUID());
        expiredToken.setToken("expired-token");
        expiredToken.setUsuario(testUser);
        expiredToken.setFechaExpiracion(Instant.now().minusSeconds(3600)); // Expiró hace 1 hora
        expiredToken.setRevocado(false);
        
        revokedToken = new RefreshToken();
        revokedToken.setId(UUID.randomUUID());
        revokedToken.setToken("revoked-token");
        revokedToken.setUsuario(testUser);
        revokedToken.setFechaExpiracion(Instant.now().plusSeconds(3600)); // Expira en 1 hora
        revokedToken.setRevocado(true);
    }

    @Test
    void crearRefreshToken_DeberiaRetornarNuevoRefreshToken() {
        // Guardar el momento inicial antes de la operación
        Instant momentoAntes = Instant.now();
        
        // Configurar mock
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(i -> i.getArguments()[0]);
        
        // Ejecutar
        RefreshToken result = refreshTokenService.crearRefreshToken(testUser);
        
        // Verificar
        assertNotNull(result);
        assertEquals(testUser, result.getUsuario());
        assertFalse(result.isRevocado()); // Usar isRevocado() para campo booleano generado por Lombok
        
        // Verificar que la fecha de expiración esté en el futuro
        assertTrue(result.getFechaExpiracion().isAfter(momentoAntes), 
                "La fecha de expiración debe ser posterior al momento de creación del token");
                
        verify(refreshTokenRepository, times(1)).revokeAllUserTokens(testUser);
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }
    
    @Test
    void validarRefreshToken_DeberiaRetornarToken_CuandoTokenEsValido() {
        // Configurar
        when(refreshTokenRepository.findByToken("valid-token")).thenReturn(Optional.of(validToken));
        
        // Ejecutar
        Optional<RefreshToken> result = refreshTokenService.validarRefreshToken("valid-token");
        
        // Verificar
        assertTrue(result.isPresent());
        assertEquals(validToken, result.get());
    }
    
    @Test
    void validarRefreshToken_DeberiaRetornarVacio_CuandoTokenEstaExpirado() {
        // Configurar
        when(refreshTokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expiredToken));
        
        // Ejecutar
        Optional<RefreshToken> result = refreshTokenService.validarRefreshToken("expired-token");
        
        // Verificar
        assertTrue(result.isEmpty());
    }
    
    @Test
    void validarRefreshToken_DeberiaRetornarVacio_CuandoTokenEstaRevocado() {
        // Configurar
        when(refreshTokenRepository.findByToken("revoked-token")).thenReturn(Optional.of(revokedToken));
        
        // Ejecutar
        Optional<RefreshToken> result = refreshTokenService.validarRefreshToken("revoked-token");
        
        // Verificar
        assertTrue(result.isEmpty());
    }
    
    @Test
    void validarRefreshToken_DeberiaRetornarVacio_CuandoTokenNoExiste() {
        // Configurar
        when(refreshTokenRepository.findByToken("nonexistent-token")).thenReturn(Optional.empty());
        
        // Ejecutar
        Optional<RefreshToken> result = refreshTokenService.validarRefreshToken("nonexistent-token");
        
        // Verificar
        assertTrue(result.isEmpty());
    }
    
    @Test
    void revocarTodosLosTokensDelUsuario_DeberiaLlamarAlRepositorio() {
        // Ejecutar
        refreshTokenService.revocarTodosLosTokensDelUsuario(testUser);
        
        // Verificar
        verify(refreshTokenRepository, times(1)).revokeAllUserTokens(testUser);
    }
    
    @Test
    void eliminarTokensExpirados_DeberiaLlamarAlRepositorio() {
        // Ejecutar
        refreshTokenService.eliminarTokensExpirados();
        
        // Verificar
        verify(refreshTokenRepository, times(1)).deleteAllExpiredOrRevoked(any(Instant.class));
    }
}