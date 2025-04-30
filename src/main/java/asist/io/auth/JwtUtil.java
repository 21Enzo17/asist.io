package asist.io.auth;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

@Component
public class JwtUtil {
    private final Logger logger = Logger.getLogger(this.getClass());
    private final Dotenv dotenv = Dotenv.load();
    
    private final String secretKey;
    private final long accessTokenValidity;
    private final String issuer;
    private final String audience;
    
    private final JwtParser jwtParser;
    
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    
    private final ITokenBlacklistService tokenBlacklistService;
    private final IRefreshTokenService refreshTokenService;

    @Autowired
    public JwtUtil(ITokenBlacklistService tokenBlacklistService, IRefreshTokenService refreshTokenService) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.refreshTokenService = refreshTokenService;
        
        // Cargamos configuraciones desde variables de entorno
        this.secretKey = dotenv.get("JWT_SECRET_KEY", "as1st10_s3cr3t_k3y_m0r3_s3cur3_th4n_b3f0r3");
        this.accessTokenValidity = Long.parseLong(dotenv.get("JWT_ACCESS_TOKEN_EXPIRATION", "1800")); // 30 min default
        this.issuer = dotenv.get("JWT_ISSUER", "asist.io");
        this.audience = dotenv.get("JWT_AUDIENCE", "asist.io-client");
        
        // Inicializamos el parser JWT con la clave segura
        Key key = getSigningKey();
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }
    
    /**
     * Obtiene la clave de firma para JWT basada en la clave secreta
     * @return Clave de firma segura para HS512
     */
    private Key getSigningKey() {
        try {
            // Generamos una clave segura basada en secretKey pero con longitud suficiente para HS512
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] keyBytes = md.digest(secretKey.getBytes(StandardCharsets.UTF_8));
            
            // Usamos Keys.hmacShaKeyFor que verifica que la clave sea suficientemente fuerte
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            // Si SHA-512 no está disponible (muy poco probable), usaremos un método alternativo
            logger.error("Error al crear clave de firma: " + e.getMessage());
            
            // Generamos una clave segura usando SecretKey directamente para HS512
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    /**
     * Crea un token JWT de acceso para un usuario específico.
     * 
     * @param user El usuario para el que se va a crear el token
     * @return El token JWT de acceso
     */
    public String createAccessToken(Usuario user) {
        return createToken(user, accessTokenValidity);
    }
    
    /**
     * Método base para crear un token JWT con duración personalizada
     * 
     * @param user El usuario para el que se va a crear el token
     * @param validityInSeconds Duración del token en segundos
     * @return El token JWT
     */
    private String createToken(Usuario user, long validityInSeconds) {
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.SECONDS.toMillis(validityInSeconds));
        String tokenId = UUID.randomUUID().toString();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", user.getNombre());
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getCorreo())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(tokenCreateTime)
                .setId(tokenId)
                .setExpiration(tokenValidity)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Parsea las reclamaciones (claims) de un token JWT.
     * @param token El token JWT como una cadena de texto
     * @return Las reclamaciones del token JWT
     */
    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    /**
     * Extrae y parsea las reclamaciones de un token JWT de una solicitud HTTP.
     * @param req La solicitud HTTP
     * @return Las reclamaciones del token JWT
     */
    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token == null) return null;
            
            // Verificamos si el token está en la lista negra
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new ModelException("El token ha sido revocado");
            }
            
            return parseJwtClaims(token);
        } catch (ExpiredJwtException ex) {
            throw new ModelException("El token ha expirado");
        } catch (MalformedJwtException ex) {
            throw new ModelException("El token es inválido");
        } catch (UnsupportedJwtException ex) {
            throw new ModelException("Tipo de token no soportado");
        } catch (IllegalArgumentException ex) {
            throw new ModelException("Token JWT vacío");
        } catch (ModelException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ModelException("Error al procesar el token: " + ex.getMessage());
        }
    }

    /**
     * Extrae el token JWT de la cabecera Authorization de la solicitud HTTP.
     * @param request La solicitud HTTP
     * @return El token JWT sin el prefijo "Bearer "
     */
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * Valida las reclamaciones de un token JWT.
     * @param claims Las reclamaciones del token JWT
     * @return true si el token es válido
     */
    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            // Validamos que el token no haya expirado
            boolean notExpired = claims.getExpiration().after(new Date());
            
            // Validamos el issuer
            boolean validIssuer = claims.getIssuer().equals(issuer);
            
            // Validamos el audience
            boolean validAudience = claims.getAudience().equals(audience);
            
            return notExpired && validIssuer && validAudience;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Obtiene el correo electrónico del usuario del token JWT.
     * @param claims Las reclamaciones del token JWT
     * @return El correo electrónico del usuario
     */
    public String getEmail(Claims claims) {
        return claims.getSubject();
    }
    
    /**
     * Invalida un token JWT añadiéndolo a la lista negra
     * @param token El token JWT a invalidar
     */
    public void invalidateToken(String token) {
        try {
            Claims claims = parseJwtClaims(token);
            
            // Calculamos el tiempo restante hasta la expiración del token
            long expiryTime = (claims.getExpiration().getTime() - new Date().getTime()) / 1000;
            
            // Solo añadimos a la lista negra si no ha expirado
            if (expiryTime > 0) {
                tokenBlacklistService.addToBlacklist(token, expiryTime);
            }
        } catch (Exception e) {
            // Si el token ya está expirado o es inválido, ignoramos
        }
    }
    
    /**
     * Crea un token de acceso a partir de un refresh token válido
     * @param refreshTokenStr El refresh token como cadena
     * @return El nuevo token de acceso
     */
    public String createAccessTokenFromRefreshToken(String refreshTokenStr) {
        return refreshTokenService.validarRefreshToken(refreshTokenStr)
            .map(RefreshToken::getUsuario)
            .map(this::createAccessToken)
            .orElseThrow(() -> new ModelException("Refresh token inválido"));
    }
}



