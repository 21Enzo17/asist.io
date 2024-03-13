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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final Logger logger =  Logger.getLogger(this.getClass());

    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, ObjectMapper mapper) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    /**
     * Este método se encarga de validar y refrescar el token JWT en cada solicitud HTTP.
     * 
     * @param request La solicitud HTTP entrante. (Contiene el token JWT en la cabecera de autorización)
     * @param response La respuesta HTTP que se enviará.
     * @param filterChain El resto de los filtros en la cadena de filtros de Spring Security.
     * 
     * @throws ServletException Si ocurre un error al procesar la solicitud.
     * @throws IOException Si ocurre un error de entrada/salida.
     * 
     * 
     * El método funciona de la siguiente manera:
     * 1. Resuelve el token JWT de la solicitud HTTP.
     * 2. Si no hay token, permite que la solicitud pase a través del filtro sin más procesamiento.
     * 3. Si hay un token, resuelve las reclamaciones del token.
     * 4. Si las reclamaciones son válidas, autentica al usuario en el contexto de seguridad de Spring.
     * 5. Si ocurre una excepción (por ejemplo, el token no es válido), se devuelve una respuesta HTTP con estado 403 (Prohibido) y detalles del error en formato JSON.
     * 6. Finalmente, permite que la solicitud pase a través del filtro para ser procesada por el resto de la cadena de filtros.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Map<String, Object> errorDetails = new HashMap<>();

        try {
            String accessToken = jwtUtil.resolveToken(request);
            if (accessToken == null ) {
                filterChain.doFilter(request, response);
                return;
            }
            Claims claims = jwtUtil.resolveClaims(request);

            if(claims != null && jwtUtil.validateClaims(claims)){
                String email = claims.getSubject();
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(email,"",new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
            filterChain.doFilter(request, response);

        }catch (Exception e){
            logger.error("Error al autenticar el token JWT: " + e.getMessage());
            errorDetails.put("message", "Authentication Error");
            errorDetails.put("details",e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            mapper.writeValue(response.getWriter(), errorDetails);

        }
        
    }
}
