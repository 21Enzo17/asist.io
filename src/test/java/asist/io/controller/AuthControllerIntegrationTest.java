package asist.io.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
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
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import asist.io.auth.JwtUtil;
import asist.io.config.TestSecurityConfig;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.exception.ModelException;
import asist.io.service.IAuthService;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;
import asist.io.service.IUsuarioService;

@WebMvcTest(controllers = AuthController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private IAuthService authService;
    
    @MockBean
    private IRefreshTokenService refreshTokenService;
    
    @MockBean
    private IUsuarioService usuarioService;
    
    @MockBean
    private ITokenBlacklistService tokenBlacklistService;
    
    @MockBean
    private JwtUtil jwtUtil;
    
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
        usuarioDTO.setId(UUID.randomUUID().toString());
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
            .with(SecurityMockMvcRequestPostProcessors.csrf())
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
            .with(SecurityMockMvcRequestPostProcessors.csrf())
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
            .with(SecurityMockMvcRequestPostProcessors.csrf())
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
            .with(SecurityMockMvcRequestPostProcessors.csrf())
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
            .with(SecurityMockMvcRequestPostProcessors.csrf())
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
            .with(SecurityMockMvcRequestPostProcessors.csrf())
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
        
        doNothing().when(authService).logout(anyString(), anyString());
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/logout")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(logoutRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Sesión cerrada correctamente"));
    }
    
    @Test
    void logout_ShouldReturnSuccess_WhenHeaderTokenIsUsed() throws Exception {
        // Preparar
        Map<String, String> logoutRequest = new HashMap<>();
        logoutRequest.put("refreshToken", "refresh-token-123");
        
        doNothing().when(authService).logout(anyString(), anyString());
        
        // Ejecutar y verificar
        mockMvc.perform(post("/api/v1/auth/logout")
            .with(SecurityMockMvcRequestPostProcessors.csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(logoutRequest))
            .header("Authorization", "Bearer access-token-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Sesión cerrada correctamente"));
            
        // Verificar que se llamó al servicio con los tokens correctos
        verify(authService).logout(anyString(), eq("refresh-token-123"));
    }
    
    @Test
    @WithMockUser(username = "test@example.com")
    void verificarToken_ShouldReturnSuccess_WhenTokenIsValid() throws Exception {
        mockMvc.perform(get("/api/v1/auth/verificar-token")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token verificado correctamente"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.authenticated").value(true));
    }
    
    @Test
    void verificarToken_ShouldReturnError_WhenNoAuthentication() throws Exception {
        // Para este caso usamos la configuración que devuelve 401 Unauthorized
        mockMvc.perform(get("/api/v1/auth/verificar-token")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isUnauthorized());
    }
}