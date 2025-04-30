package asist.io.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

class TokenBlacklistServiceImplTest {

    private TokenBlacklistServiceImpl tokenBlacklistService;
    
    @Mock
    private RedisTemplate<String, String> redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        tokenBlacklistService = new TokenBlacklistServiceImpl(redisTemplate);
    }

    @Test
    void addToBlacklist_ShouldAddTokenToRedis() {
        // Preparar
        String token = "test-token";
        long expiryTime = 3600; // 1 hora
        
        // Ejecutar
        tokenBlacklistService.addToBlacklist(token, expiryTime);
        
        // Verificar
        verify(valueOperations).set(
            eq("blacklisted_token:" + token), 
            eq("blacklisted"), 
            eq(expiryTime), 
            eq(TimeUnit.SECONDS)
        );
    }
    
    @Test
    void isBlacklisted_ShouldReturnTrue_WhenTokenExists() {
        // Preparar
        String token = "blacklisted-token";
        when(redisTemplate.hasKey("blacklisted_token:" + token)).thenReturn(true);
        
        // Ejecutar
        boolean result = tokenBlacklistService.isBlacklisted(token);
        
        // Verificar
        assertTrue(result);
        verify(redisTemplate).hasKey("blacklisted_token:" + token);
    }
    
    @Test
    void isBlacklisted_ShouldReturnFalse_WhenTokenDoesNotExist() {
        // Preparar
        String token = "valid-token";
        when(redisTemplate.hasKey("blacklisted_token:" + token)).thenReturn(false);
        
        // Ejecutar
        boolean result = tokenBlacklistService.isBlacklisted(token);
        
        // Verificar
        assertFalse(result);
        verify(redisTemplate).hasKey("blacklisted_token:" + token);
    }
    
    @Test
    void isBlacklisted_ShouldReturnFalse_WhenNullIsReturned() {
        // Preparar
        String token = "null-result-token";
        when(redisTemplate.hasKey("blacklisted_token:" + token)).thenReturn(null);
        
        // Ejecutar
        boolean result = tokenBlacklistService.isBlacklisted(token);
        
        // Verificar
        assertFalse(result);
        verify(redisTemplate).hasKey("blacklisted_token:" + token);
    }
}