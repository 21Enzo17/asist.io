package asist.io.auth;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import asist.io.entity.Usuario;
import asist.io.exceptions.ModelException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {


    private final String secret_key = "mysecretkey";
    private long accessTokenValidity = 21600;

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil(){
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }


    /**
     * Metodo para crear un token
     * @param user
     * @return
     */
    public String createToken(Usuario user) {
        Claims claims = Jwts.claims().setSubject(user.getCorreo());
        claims.put("userName",user.getNombre());
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.SECONDS.toMillis(accessTokenValidity));
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(tokenValidity)
                .signWith(SignatureAlgorithm.HS256, secret_key)
                .compact();
    }

    /**
     * Metodo para parsear el token
     * @param token
     * @return
     */
    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }


    /**
     * Este método resolveClaims se utiliza para extraer y parsear las reclamaciones (claims) de un token JWT de una solicitud HTTP.
     * @param req
     * @return
     */
    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token != null) {
                return parseJwtClaims(token);
            }
            return null;
        } catch (ExpiredJwtException ex) {
            /*req.setAttribute("expired", ex.getMessage());
            throw ex;*/
            throw new ModelException("El token ha expirado");
        } catch (Exception ex) {
            /*req.setAttribute("invalid", ex.getMessage());
            throw ex;*/
            throw new ModelException("El token es invalido");
        }
    }

    /**
     * Este método resolveToken se utiliza para extraer el 
     * token JWT de la cabecera Authorization de la solicitud HTTP
     * @param request
     * @return
     */
    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * Metodo para validar la fecha de exclamacion del token
     * @param claims
     * @return
     * @throws AuthenticationException
     */
    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Metodo para obtener el email del token
     * @param claims
     * @return String
     */
    public String getEmail(Claims claims) {
        return claims.getSubject();
    }
}

    /*
    No entendi pero parece que es para obtener los roles del usuario, parecia importante

    public List<String> getRoles(Claims claims) {
        return claims.get("roles", List.class);
    }
     */
    


