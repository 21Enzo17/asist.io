package asist.io.auth;

import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import asist.io.entity.RefreshToken;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenBlacklistService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Utilidad para gestionar tokens JWT con rotación de claves RSA.
 * 
 * Esta clase maneja la creación, validación y renovación de tokens JWT, utilizando
 * claves RSA que pueden rotarse periódicamente para mejorar la seguridad.
 * Las claves antiguas se archivan para garantizar que los tokens generados
 * anteriormente sigan siendo válidos hasta su expiración.
 */
@Component
public class JwtUtil {
    private final Logger logger = Logger.getLogger(this.getClass());
    
    /**
     * Dotenv para cargar variables de entorno desde archivo .env
     * Se usa como respaldo si no se definen las propiedades en Spring
     */
    private final Dotenv dotenv;
    
    /**
     * Entorno de Spring para acceder a propiedades de configuración
     */
    private final Environment env;
    
    /**
     * Validez del token de acceso en segundos
     */
    private final long accessTokenValidity;
    
    /**
     * Emisor (issuer) del token JWT
     */
    private final String issuer;
    
    /**
     * Audiencia (audience) del token JWT
     */
    private final String audience;
    
    /**
     * Número máximo de claves anteriores a mantener para verificación
     */
    private final int MAX_PREVIOUS_KEYS;
    
    /**
     * Lista de parsers JWT para verificar tokens con claves antiguas
     */
    private List<JwtParser> jwtParsers = new ArrayList<>();
    
    /**
     * Cabecera HTTP donde viene el token JWT
     */
    private final String TOKEN_HEADER;
    
    /**
     * Prefijo del token JWT en la cabecera Authorization
     */
    private final String TOKEN_PREFIX;
    
    /**
     * Servicio para gestionar tokens en lista negra
     */
    private final ITokenBlacklistService tokenBlacklistService;
    
    /**
     * Servicio para gestionar refresh tokens
     */
    private final IRefreshTokenService refreshTokenService;
    
    /**
     * Clave privada actual (para firmar)
     */
    private PrivateKey privateKey;
    
    /**
     * Clave pública actual (para verificar)
     */
    private PublicKey publicKey;
    
    /**
     * Directorio donde se guardan las claves
     */
    private final String KEY_DIRECTORY;
    
    /**
     * Nombre del archivo de clave privada
     */
    private final String PRIVATE_KEY_FILE;
    
    /**
     * Nombre del archivo de clave pública
     */
    private final String PUBLIC_KEY_FILE;
    
    /**
     * Prefijo para archivos de claves archivadas
     */
    private final String KEY_ARCHIVE_PREFIX;
    
    /**
     * Constructor para JwtUtil que utiliza tanto propiedades de Spring como Dotenv como respaldo.
     * 
     * @param tokenBlacklistService Servicio para gestionar tokens en lista negra
     * @param refreshTokenService Servicio para gestionar refresh tokens
     * @param env Entorno Spring para acceder a las propiedades de configuración
     */
    @Autowired
    public JwtUtil(
            ITokenBlacklistService tokenBlacklistService, 
            IRefreshTokenService refreshTokenService,
            Environment env) {
        
        this.tokenBlacklistService = tokenBlacklistService;
        this.refreshTokenService = refreshTokenService;
        this.env = env;
        this.dotenv = Dotenv.load();
        
        // Valores de configuración (primero busca en propiedades de Spring, luego en Dotenv, luego valores por defecto)
        this.accessTokenValidity = getLongProperty("JWT_ACCESS_TOKEN_EXPIRATION", 900); // 15 min por defecto
        this.issuer = getStringProperty("JWT_ISSUER", "asist.io");
        this.audience = getStringProperty("JWT_AUDIENCE", "asist.io-client");
        this.MAX_PREVIOUS_KEYS = getIntProperty("JWT_MAX_PREVIOUS_KEYS", 2);
        
        // Directorios y archivos de claves
        this.KEY_DIRECTORY = getStringProperty("JWT_KEY_DIRECTORY", "config/keys");
        this.PRIVATE_KEY_FILE = getStringProperty("JWT_PRIVATE_KEY_FILE", "jwt_private.key");
        this.PUBLIC_KEY_FILE = getStringProperty("JWT_PUBLIC_KEY_FILE", "jwt_public.key");
        this.KEY_ARCHIVE_PREFIX = getStringProperty("JWT_KEY_ARCHIVE_PREFIX", "archived_key_");
        
        // Cabecera y prefijo del token
        this.TOKEN_HEADER = getStringProperty("JWT_TOKEN_HEADER", "Authorization");
        this.TOKEN_PREFIX = getStringProperty("JWT_TOKEN_PREFIX", "Bearer ");
        
        // Inicializamos las claves RSA
        initRSAKeys();
        
        // Inicializamos los parsers JWT
        initJwtParsers();
    }
    
    /**
     * Obtiene una propiedad String primero desde Spring Environment, luego desde Dotenv.
     * 
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no se encuentra la propiedad
     * @return Valor de la propiedad
     */
    private String getStringProperty(String key, String defaultValue) {
        // Primero intentamos desde Spring Environment
        String value = env.getProperty(key);
        
        // Si no está en Spring, intentamos desde Dotenv
        if (value == null) {
            value = dotenv.get(key);
        }
        
        // Si no está en ningún lado, usamos el valor por defecto
        return value != null ? value : defaultValue;
    }
    
    /**
     * Obtiene una propiedad Long primero desde Spring Environment, luego desde Dotenv.
     * 
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no se encuentra la propiedad
     * @return Valor de la propiedad como Long
     */
    private long getLongProperty(String key, long defaultValue) {
        String value = getStringProperty(key, null);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                logger.warn("Error al parsear propiedad " + key + " como Long: " + e.getMessage());
            }
        }
        return defaultValue;
    }
    
    /**
     * Obtiene una propiedad Integer primero desde Spring Environment, luego desde Dotenv.
     * 
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no se encuentra la propiedad
     * @return Valor de la propiedad como Integer
     */
    private int getIntProperty(String key, int defaultValue) {
        String value = getStringProperty(key, null);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Error al parsear propiedad " + key + " como Integer: " + e.getMessage());
            }
        }
        return defaultValue;
    }
    
    /**
     * Inicializa las claves RSA, cargándolas de archivos o generándolas si no existen.
     * Este método asegura que siempre haya un par de claves disponible para firmar y verificar tokens.
     */
    protected void initRSAKeys() {
        try {
            File keyDirectory = new File(KEY_DIRECTORY);
            if (!keyDirectory.exists()) {
                boolean dirCreated = keyDirectory.mkdirs();
                if (!dirCreated) {
                    logger.error("No se pudo crear el directorio de claves: " + KEY_DIRECTORY);
                    throw new IOException("No se pudo crear el directorio de claves");
                }
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
            logger.error("Error inicializando claves RSA: " + e.getMessage(), e);
            throw new RuntimeException("Error inicializando sistema de seguridad", e);
        }
    }

    /**
     * Inicializa los parsers JWT con la clave pública actual y las claves anteriores archivadas.
     * Esto permite verificar tokens firmados con claves anteriores durante el periodo de transición.
     */
    protected void initJwtParsers() {
        // Limpiamos la lista de parsers existentes
        jwtParsers.clear();
        
        // Añadimos el parser para la clave actual
        if (publicKey != null) {
            jwtParsers.add(Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build());
            
            logger.debug("Parser JWT inicializado con clave pública actual");
        }
        
        // Buscamos claves archivadas y añadimos parsers para ellas
        loadArchivedKeys();
    }
    
    /**
     * Carga claves archivadas previas para la verificación de tokens antiguos.
     * Esto asegura que los tokens generados con claves anteriores sigan siendo válidos.
     */
    protected void loadArchivedKeys() {
        try {
            File keyDir = new File(KEY_DIRECTORY);
            if (keyDir.exists() && keyDir.isDirectory()) {
                File[] archivedPublicKeyFiles = keyDir.listFiles((dir, name) -> 
                    name.startsWith(KEY_ARCHIVE_PREFIX) && name.endsWith(".pub"));
                
                if (archivedPublicKeyFiles != null) {
                    // Ordenar por timestamp (más reciente primero)
                    List<File> sortedKeys = new ArrayList<>(List.of(archivedPublicKeyFiles));
                    sortedKeys.sort((f1, f2) -> f2.getName().compareTo(f1.getName()));
                    
                    // Limitar al número máximo de claves anteriores
                    int keysToLoad = Math.min(sortedKeys.size(), MAX_PREVIOUS_KEYS);
                    
                    for (int i = 0; i < keysToLoad; i++) {
                        File keyFile = sortedKeys.get(i);
                        try {
                            PublicKey oldKey = loadPublicKey(keyFile.toPath());
                            jwtParsers.add(Jwts.parserBuilder()
                                    .setSigningKey(oldKey)
                                    .build());
                            logger.info("Cargada clave pública archivada: " + keyFile.getName());
                        } catch (Exception e) {
                            logger.warn("No se pudo cargar la clave archivada: " + keyFile.getName(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error cargando claves archivadas: " + e.getMessage(), e);
        }
    }
    
    /**
     * Carga una clave pública desde un archivo.
     * 
     * @param publicKeyPath Ruta al archivo de clave pública
     * @return Objeto PublicKey cargado desde el archivo
     * @throws IOException Si hay error al leer el archivo
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo RSA
     * @throws InvalidKeySpecException Si la clave no tiene el formato esperado
     */
    protected PublicKey loadPublicKey(Path publicKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(Files.readString(publicKeyPath).trim());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }
    
    /**
     * Genera un nuevo par de claves RSA y las guarda en archivos.
     * 
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo RSA
     * @throws IOException Si hay error al escribir los archivos
     */
    protected void generateAndSaveKeyPair() throws NoSuchAlgorithmException, IOException {
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
     * Carga las claves RSA desde archivos.
     * 
     * @param privateKeyPath Ruta al archivo de clave privada
     * @param publicKeyPath Ruta al archivo de clave pública
     * @throws IOException Si hay error al leer los archivos
     * @throws NoSuchAlgorithmException Si no se encuentra el algoritmo RSA
     * @throws InvalidKeySpecException Si las claves no tienen el formato esperado
     */
    protected void loadKeys(Path privateKeyPath, Path publicKeyPath) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        logger.info("Cargando claves RSA existentes...");
        
        // Cargar clave privada
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(Files.readString(privateKeyPath).trim());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            logger.error("Error cargando clave privada: " + e.getMessage());
            throw e;
        }
        
        // Cargar clave pública
        try {
            publicKey = loadPublicKey(publicKeyPath);
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
     * Método base para crear un token JWT con duración personalizada.
     * 
     * @param user El usuario para el que se va a crear el token
     * @param validityInSeconds Duración del token en segundos
     * @return El token JWT
     */
    protected String createToken(Usuario user, long validityInSeconds) {
        Date tokenCreateTime = new Date();
        Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.SECONDS.toMillis(validityInSeconds));
        String tokenId = UUID.randomUUID().toString();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("userName", user.getNombre());
        claims.put("nonce", UUID.randomUUID().toString()); // Nonce para prevenir ataques de repetición
        
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
     * Intenta verificar el token con todas las claves disponibles.
     * 
     * @param token El token JWT como una cadena de texto
     * @return Las reclamaciones del token JWT
     * @throws JwtException Si el token no puede ser validado con ninguna clave
     */
    protected Claims parseJwtClaims(String token) {
        if (token == null) {
            throw new JwtException("Token JWT nulo");
        }
        
        // Si no hay parsers configurados, lanzar excepción
        if (jwtParsers.isEmpty()) {
            throw new JwtException("No hay claves configuradas para validar tokens JWT");
        }
        
        // Intentar con todas las claves disponibles, comenzando por la actual
        for (JwtParser parser : jwtParsers) {
            try {
                return parser.parseClaimsJws(token).getBody();
            } catch (JwtException e) {
                // Si falla, continuamos con la siguiente clave
                logger.debug("Fallo al validar token con una clave: " + e.getMessage());
                continue;
            }
        }
        
        // Si llegamos aquí, ninguna clave pudo validar el token
        throw new JwtException("No se pudo validar el token con ninguna de las claves disponibles");
    }

    /**
     * Extrae y parsea las reclamaciones de un token JWT de una solicitud HTTP.
     * 
     * @param req La solicitud HTTP
     * @return Las reclamaciones del token JWT o null si no hay token
     * @throws ModelException Si el token es inválido o ha expirado
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
     * 
     * @param request La solicitud HTTP
     * @return El token JWT sin el prefijo "Bearer " o null si no hay token
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
     * 
     * @param claims Las reclamaciones del token JWT
     * @return true si el token es válido
     * @throws AuthenticationException Si hay error al validar el token
     */
    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            if (claims == null) {
                return false;
            }
            
            // Validamos que el token no haya expirado
            boolean notExpired = claims.getExpiration().after(new Date());
            
            // Validamos el issuer
            boolean validIssuer = claims.getIssuer() != null && claims.getIssuer().equals(issuer);
            
            // Validamos el audience
            boolean validAudience = claims.getAudience() != null && claims.getAudience().equals(audience);
            
            return notExpired && validIssuer && validAudience;
        } catch (Exception e) {
            logger.error("Error validando claims del token: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene el correo electrónico del usuario del token JWT.
     * 
     * @param claims Las reclamaciones del token JWT
     * @return El correo electrónico del usuario
     */
    public String getEmail(Claims claims) {
        return claims.getSubject();
    }
    
    /**
     * Invalida un token JWT añadiéndolo a la lista negra.
     * 
     * @param token El token JWT a invalidar
     */
    public void invalidateToken(String token) {
        if (token == null || token.isEmpty()) {
            return;
        }
        
        try {
            Claims claims = parseJwtClaims(token);
            
            // Calculamos el tiempo restante hasta la expiración del token
            long expiryTime = (claims.getExpiration().getTime() - new Date().getTime()) / 1000;
            
            // Solo añadimos a la lista negra si no ha expirado
            if (expiryTime > 0) {
                tokenBlacklistService.addToBlacklist(token, expiryTime);
                logger.debug("Token añadido a la lista negra, expira en " + expiryTime + " segundos");
            }
        } catch (Exception e) {
            // Si el token ya está expirado o es inválido, lo ignoramos
            logger.debug("No se pudo invalidar el token (posiblemente ya expiró): " + e.getMessage());
        }
    }
    
    /**
     * Crea un token de acceso a partir de un refresh token válido.
     * 
     * @param refreshTokenStr El refresh token como cadena
     * @return El nuevo token de acceso
     * @throws ModelException Si el refresh token es inválido
     */
    public String createAccessTokenFromRefreshToken(String refreshTokenStr) {
        return refreshTokenService.validarRefreshToken(refreshTokenStr)
            .map(RefreshToken::getUsuario)
            .map(this::createAccessToken)
            .orElseThrow(() -> new ModelException("Refresh token inválido"));
    }
    
    /**
     * Rota las claves RSA generando un nuevo par de claves.
     * El par anterior se archiva para verificar tokens existentes.
     * 
     * @return true si la rotación fue exitosa, false en caso contrario
     */
    public boolean rotateKeys() {
        try {
            logger.info("Iniciando rotación de claves RSA...");
            
            // Archivo las claves actuales antes de generar nuevas
            archiveCurrentKeys();
            
            // Generar nuevas claves
            generateAndSaveKeyPair();
            
            // Reinicializar los parsers JWT
            initJwtParsers();
            
            logger.info("Rotación de claves RSA completada exitosamente");
            return true;
        } catch (Exception e) {
            logger.error("Error rotando claves RSA: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Archiva las claves actuales antes de una rotación.
     * Solo se archiva la clave pública ya que es lo único necesario para verificar tokens.
     * 
     * @throws IOException Si hay error al escribir el archivo
     */
    protected void archiveCurrentKeys() throws IOException {
        if (publicKey != null) {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String archivedPublicKeyFile = KEY_ARCHIVE_PREFIX + timestamp + ".pub";
            
            // Archivar solo la clave pública (es lo único necesario para verificar)
            try {
                X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
                String publicKeyString = Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded());
                Files.write(Paths.get(KEY_DIRECTORY, archivedPublicKeyFile), publicKeyString.getBytes());
                
                logger.info("Clave pública actual archivada como: " + archivedPublicKeyFile);
                
                // Eliminar claves antiguas que excedan el límite
                cleanOldArchivedKeys();
            } catch (IOException e) {
                logger.error("Error archivando clave pública actual: " + e.getMessage());
                throw e;
            }
        }
    }
    
    /**
     * Elimina claves archivadas antiguas que excedan el límite máximo.
     * Esto evita acumular demasiadas claves antiguas.
     */
    protected void cleanOldArchivedKeys() {
        File keyDir = new File(KEY_DIRECTORY);
        if (keyDir.exists() && keyDir.isDirectory()) {
            File[] archivedKeyFiles = keyDir.listFiles((dir, name) -> 
                name.startsWith(KEY_ARCHIVE_PREFIX));
            
            if (archivedKeyFiles != null && archivedKeyFiles.length > MAX_PREVIOUS_KEYS) {
                // Ordenar por nombre (que incluye timestamp) - más antiguos primero
                List<File> sortedKeys = new ArrayList<>(List.of(archivedKeyFiles));
                sortedKeys.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
                
                // Eliminar las claves más antiguas que excedan el límite
                for (int i = 0; i < sortedKeys.size() - MAX_PREVIOUS_KEYS; i++) {
                    File oldKeyFile = sortedKeys.get(i);
                    if (oldKeyFile.delete()) {
                        logger.info("Clave archivada antigua eliminada: " + oldKeyFile.getName());
                    } else {
                        logger.warn("No se pudo eliminar clave archivada antigua: " + oldKeyFile.getName());
                    }
                }
            }
        }
    }
    
    /**
     * Reinicializa la utilidad JWT, recargando claves y parsers.
     * Útil para pruebas o después de cambios en la configuración.
     */
    public void reInitialize() {
        logger.info("Reinicializando JwtUtil...");
        initRSAKeys();
        initJwtParsers();
        logger.info("JwtUtil reinicializado exitosamente");
    }
    
    /**
     * Solo para pruebas: establece una clave privada personalizada.
     * No se debe usar en producción.
     * 
     * @param privateKey Clave privada personalizada
     */
    protected void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
    
    /**
     * Solo para pruebas: establece una clave pública personalizada.
     * No se debe usar en producción.
     * 
     * @param publicKey Clave pública personalizada
     */
    protected void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}



