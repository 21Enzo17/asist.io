package asist.io.auth;

import com.fasterxml.jackson.databind.ObjectMapper;

import asist.io.entity.Usuario;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
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

    private final JwtUtil jwtUtil;
    private final ObjectMapper mapper;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, ObjectMapper mapper) {
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
    }

    /**
     * Metodo para validar el token
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     * @return void
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

                Usuario usuario = new Usuario();
                usuario.setCorreo(email);
                usuario.setNombre((String) claims.get("userName"));
                String newAccessToken = jwtUtil.createToken(usuario);
                response.addHeader("Authorization", "Bearer " + newAccessToken);
            }

        }catch (Exception e){
            errorDetails.put("message", "Authentication Error");
            errorDetails.put("details",e.getMessage());
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            
            mapper.writeValue(response.getWriter(), errorDetails);

        }
        filterChain.doFilter(request, response);
    }
}
