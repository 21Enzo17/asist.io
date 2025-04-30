package asist.io.scheduled;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import asist.io.auth.JwtUtil;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenService;

class TareasProgramadasTest {

    @InjectMocks
    private TareasProgramadas tareasProgramadas;
    
    @Mock
    private ITokenService tokenService;
    
    @Mock
    private IRefreshTokenService refreshTokenService;
    
    @Mock
    private JwtUtil jwtUtil;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void borrarTokensVencidos_ShouldCallTokenService() {
        // Ejecutar
        tareasProgramadas.borrarTokensVencidos();
        
        // Verificar
        verify(tokenService, times(1)).borrarTokensVencidos();
    }
    
    @Test
    void eliminarRefreshTokensExpirados_ShouldCallRefreshTokenService() {
        // Ejecutar
        tareasProgramadas.eliminarRefreshTokensExpirados();
        
        // Verificar
        verify(refreshTokenService, times(1)).eliminarTokensExpirados();
    }
    
    @Test
    void rotarClavesRSA_ShouldCallJwtUtil() {
        // Preparar
        when(jwtUtil.rotateKeys()).thenReturn(true);
        
        // Ejecutar
        tareasProgramadas.rotarClavesRSA();
        
        // Verificar
        verify(jwtUtil, times(1)).rotateKeys();
    }
    
    @Test
    void rotarClavesRSA_ShouldHandleFailure() {
        // Preparar
        when(jwtUtil.rotateKeys()).thenReturn(false);
        
        // Ejecutar - No debería lanzar excepción
        tareasProgramadas.rotarClavesRSA();
        
        // Verificar
        verify(jwtUtil, times(1)).rotateKeys();
    }
}