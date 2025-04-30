package asist.io.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.exception.ModelException;
import asist.io.service.IAuthService;
import asist.io.service.impl.RefreshTokenServiceImpl;
import asist.io.service.impl.UsuarioServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private IAuthService authService;
    
    @MockBean
    private RefreshTokenServiceImpl refreshTokenService;
    
    @MockBean
    private UsuarioServiceImpl usuarioService;
    
    private UsuarioLoginDTO loginRequest;
    private UsuarioGetLoginDTO loginResponse;
    private UsuarioGetDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba
        loginRequest = new UsuarioLoginDTO();
        loginRequest.setCorreo("test@example.com");
        loginRequest.setContrasena("password123");
        
        usuarioDTO = new UsuarioGetDTO();
        usuarioDTO.setId(UUID.randomUUID().toString()); // Corregido a UUID en lugar de Long
        usuarioDTO.setNombre("Usuario Test");
        usuarioDTO.setCorreo("test@example.com");
        usuarioDTO.setVerificado(true);
        
        loginResponse = new UsuarioGetLoginDTO();
        loginResponse.setUsuario(usuarioDTO);
        loginResponse.setToken("access-token-123");
        loginResponse.setRefreshToken("refresh-token-123");
    }
    
    @Test
    void login_ShouldReturnTokensAndUserInfo_WhenCredentialsAreValid() throws Exception {
        // Preparar
        when(authService.login(any(UsuarioLoginDTO.class))).thenReturn(loginResponse);
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Inicio de sesión exitoso"))
            .andExpect(jsonPath("$.data.token").value("access-token-123"))
            .andExpect(jsonPath("$.data.refreshToken").value("refresh-token-123"))
            .andExpect(jsonPath("$.data.usuario.correo").value("test@example.com"));
    }
    
    @Test
    void login_ShouldReturnError_WhenUserIsNotVerified() throws Exception {
        // Preparar
        usuarioDTO.setVerificado(false);
        when(authService.login(any(UsuarioLoginDTO.class))).thenReturn(loginResponse);
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Usuario no verificado, revise su casilla de email para verificar su cuenta."));
    }
    
    @Test
    void login_ShouldReturnError_WhenCredentialsAreInvalid() throws Exception {
        // Preparar
        when(authService.login(any(UsuarioLoginDTO.class)))
            .thenThrow(new ModelException("Credenciales incorrectas"));
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Nombre de usuario o contraseña inválidos"));
    }
    
    @Test
    void refreshToken_ShouldReturnNewAccessToken_WhenRefreshTokenIsValid() throws Exception {
        // Preparar
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", "valid-refresh-token");
        
        when(authService.refreshToken("valid-refresh-token"))
            .thenReturn("new-access-token-123");
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/refresh-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Token renovado correctamente"))
            .andExpect(jsonPath("$.data.token").value("new-access-token-123"));
    }
    
    @Test
    void refreshToken_ShouldReturnError_WhenRefreshTokenIsMissing() throws Exception {
        // Preparar
        Map<String, String> emptyRequest = new HashMap<>();
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/refresh-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(emptyRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("El refresh token es requerido"));
    }
    
    @Test
    void refreshToken_ShouldReturnError_WhenRefreshTokenIsInvalid() throws Exception {
        // Preparar
        Map<String, String> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", "invalid-refresh-token");
        
        when(authService.refreshToken("invalid-refresh-token"))
            .thenThrow(new ModelException("Refresh token inválido"));
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/refresh-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Error al renovar token: Refresh token inválido"));
    }
    
    @Test
    void logout_ShouldReturnSuccess_WhenTokensAreProvided() throws Exception {
        // Preparar
        Map<String, String> logoutRequest = new HashMap<>();
        logoutRequest.put("token", "access-token-123");
        logoutRequest.put("refreshToken", "refresh-token-123");
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/logout")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(logoutRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Sesión cerrada correctamente"));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void verificarToken_ShouldReturnSuccess_WhenTokenIsValid() throws Exception {
        // Al usar @WithMockUser, Spring Security crea automáticamente un usuario 
        // autenticado en el contexto de seguridad para este test

        mockMvc.perform(get("/api/v1/auth/verificar-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token verificado correctamente"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.authenticated").value(true));
    }
}