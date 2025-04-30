package asist.io.auth;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {
    private final Logger logger = Logger.getLogger(this.getClass());
    private final Dotenv dotenv = Dotenv.load();
    
    private final long accessTokenValidity;
    private final String issuer;
    private final String audience;
    
    private final JwtParser jwtParser;
    
    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";
    
    private final ITokenBlacklistService tokenBlacklistService;
    private final IRefreshTokenService refreshTokenService;
    
    private PrivateKey privateKey;
    private PublicKey publicKey;
    
    private final String KEY_DIRECTORY = "config/keys";
    private final String PRIVATE_KEY_FILE = "jwt_private.key";
    private final String PUBLIC_KEY_FILE = "jwt_public.key";

    @Autowired
    public JwtUtil(ITokenBlacklistService tokenBlacklistService, IRefreshTokenService refreshTokenService) {
        this.tokenBlacklistService = tokenBlacklistService;
        this.refreshTokenService = refreshTokenService;
        
        // Cargamos configuraciones desde variables de entorno
        this.accessTokenValidity = Long.parseLong(dotenv.get("JWT_ACCESS_TOKEN_EXPIRATION", "900")); // 15 min default
        this.issuer = dotenv.get("JWT_ISSUER", "asist.io");
        this.audience = dotenv.get("JWT_AUDIENCE", "asist.io-client");
        
        // Inicializamos las claves RSA
        initRSAKeys();
        
        // Inicializamos el parser JWT con la clave pública
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build();
    }
    
    /**
     * Inicializa las claves RSA, cargándolas de archivos o generándolas si no existen
     */
    private void initRSAKeys() {
        try {
            File keyDirectory = new File(KEY_DIRECTORY);
            if (!keyDirectory.exists()) {
                keyDirectory.mkdirs();
            }
            
            Path privateKeyPath = Paths.get(KEY_DIRECTORY, PRIVATE_KEY_FILE);
            Path publicKeyPath = Paths.get(KEY_DIRECTORY, PUBLIC_KEY_FILE);
            
            // Si los archivos de claves no existen, generamos un nuevo par de claves
            if (!Files.exists(privateKeyPath) || !Files.exists(publicKeyPath)) {
                generateAndSaveKeyPair();
            } else {
                // Cargamos las claves existentes
                loadKeys(privateKeyPath, publicKeyPath);
            }
        } catch (Exception e) {
            logger.error("Error inicializando claves RSA: " + e.getMessage());
            throw new RuntimeException("Error inicializando sistema de seguridad", e);
        }
    }
    
    /**
     * Genera un nuevo par de claves RSA y las guarda en archivos
     */
    private void generateAndSaveKeyPair() throws NoSuchAlgorithmException, IOException {
        logger.info("Generando nuevo par de claves RSA...");
        
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Tamaño de clave recomendado para seguridad
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
        
        // Guardar clave privada
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
            String privateKeyString = Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
            Files.write(Paths.get(KEY_DIRECTORY, PRIVATE_KEY_FILE), privateKeyString.getBytes());
        } catch (IOException e) {
            logger.error("Error guardando clave privada: " + e.getMessage());
            throw e;
        }
        
        // Guardar clave pública
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            String publicKeyString = Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded());
            Files.write(Paths.get(KEY_DIRECTORY, PUBLIC_KEY_FILE), publicKeyString.getBytes());
        } catch (IOException e) {
            logger.error("Error guardando clave pública: " + e.getMessage());
            throw e;
        }
        
        logger.info("Par de claves RSA generado y guardado exitosamente");
    }
    
    /**
     * Carga las claves RSA desde archivos
     */
    private void loadKeys(Path privateKeyPath, Path publicKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        logger.info("Cargando claves RSA existentes...");
        
        // Cargar clave privada
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readString(privateKeyPath));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            logger.error("Error cargando clave privada: " + e.getMessage());
            throw e;
        }
        
        // Cargar clave pública
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(Files.readString(publicKeyPath));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);
        } catch (Exception e) {
            logger.error("Error cargando clave pública: " + e.getMessage());
            throw e;
        }
        
        logger.info("Claves RSA cargadas exitosamente");
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
        claims.put("nonce", UUID.randomUUID().toString()); // Añadimos un nonce para prevenir ataques de repetición
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getCorreo())
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(tokenCreateTime)
                .setId(tokenId)
                .setExpiration(tokenValidity)
                .signWith(privateKey, SignatureAlgorithm.RS256)
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
    
    /**
     * Rota las claves RSA generando un nuevo par de claves
     * @return true si la rotación fue exitosa
     */
    public boolean rotateKeys() {
        try {
            generateAndSaveKeyPair();
            return true;
        } catch (Exception e) {
            logger.error("Error rotando claves RSA: " + e.getMessage());
            return false;
        }
    }
}



