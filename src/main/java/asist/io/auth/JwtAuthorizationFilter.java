package asist.io.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import asist.io.dto.response.ApiResponse;
import asist.io.exception.ModelException;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final Logger logger = Logger.getLogger(this.getClass());

    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, ObjectMapper mapper) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    /**
     * Este método se encarga de validar el token JWT en cada solicitud HTTP.
     * 
     * @param request La solicitud HTTP entrante.
     * @param response La respuesta HTTP que se enviará.
     * @param filterChain El resto de los filtros en la cadena de filtros de Spring Security.
     * 
     * @throws ServletException Si ocurre un error al procesar la solicitud.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // Intentamos resolver el token de la solicitud
            String accessToken = jwtUtil.resolveToken(request);
            
            // Si no hay token, permitimos el paso (las rutas no protegidas serán manejadas por SecurityConfig)
            if (accessToken == null) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // Resolvemos y validamos las claims del token
            Claims claims = jwtUtil.resolveClaims(request);
            
            if (claims != null && jwtUtil.validateClaims(claims)) {
                String email = jwtUtil.getEmail(claims);
                
                // Autenticamos al usuario en el contexto de Spring Security
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, "", new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            
            filterChain.doFilter(request, response);
        } catch (ModelException e) {
            logger.error("Error de autenticación JWT: " + e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Error de autenticación");
        } catch (Exception e) {
            logger.error("Error inesperado al procesar token JWT: " + e.getMessage());
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Error de autenticación");
        }
    }
    
    /**
     * Envía una respuesta de error formateada al cliente
     * 
     * @param response La respuesta HTTP
     * @param status El código de estado HTTP
     * @param message Mensaje principal de error
     * @throws IOException Si ocurre un error al escribir la respuesta
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ApiResponse<Object> errorResponse = ApiResponse.error(message);
        
        mapper.writeValue(response.getWriter(), errorResponse);
    }
}
