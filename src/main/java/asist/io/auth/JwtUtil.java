package asist.io.auth;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import asist.io.entity.Usuario;
import asist.io.exception.ModelException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {


    private final String secret_key = "mysecretkey"; // cambiar en entorno de produccion
    private long accessTokenValidity = 21_600; // 6 horas, 21600 segundos

    private final JwtParser jwtParser;

    private final String TOKEN_HEADER = "Authorization";
    private final String TOKEN_PREFIX = "Bearer ";

    public JwtUtil(){
        this.jwtParser = Jwts.parser().setSigningKey(secret_key);
    }


    /**
     * Crea un token JWT para un usuario especifico.
     * 
     * @param user El usuario para el que se va a crear el token. Este objeto Usuario debe contener
     *             el correo electrónico del usuario, que se utilizará como sujeto del token,
     *             y el nombre del usuario, que se añadirá a las reclamaciones del token.
     * 
     * @return El token JWT como una cadena de texto. Este token incluye las reclamaciones
     *         especificadas, está firmado con el algoritmo HS256 y la clave secreta, y tiene un
     *         tiempo de validez igual a accessTokenValidity. El token está en formato compacto,
     *         por lo que puede ser enviado en una solicitud HTTP.
     * 
     * El método funciona de la siguiente manera:
     * 1. Crea un conjunto de reclamaciones JWT y establece el sujeto del token como el correo electrónico del usuario.
     * 2. Añade el nombre del usuario a las reclamaciones.
     * 3. Crea dos objetos Date: uno para el momento de creación del token y otro para el momento de expiración del token.
     * 4. Construye el token JWT con las reclamaciones, la fecha de expiración, el algoritmo de firma y la clave secreta.
     * 5. Devuelve el token JWT en formato compacto.
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
     * Parsea las reclamaciones (claims) de un token JWT.
     *
     * @param token El token JWT como una cadena de texto. Este token debe estar en formato compacto,
     *              como se devuelve por el método createToken.
     *
     * @return Las reclamaciones del token JWT como un objeto Claims. Este objeto incluye todas las
     *         reclamaciones que se añadieron al token cuando se creó, como el sujeto, la fecha de
     *         expiración, etc. Si el token no es válido (por ejemplo, si está mal formado, si la
     *         firma no coincide, si ha expirado, etc.), este método lanzará una excepción.
     *
     * El método funciona de la siguiente manera:
     * 1. Utiliza el objeto jwtParser para parsear el token JWT.
     * 2. Devuelve las reclamaciones del token.
     */
    private Claims parseJwtClaims(String token) {
        return jwtParser.parseClaimsJws(token).getBody();
    }


    /**
     * Extrae y parsea las reclamaciones (claims) de un token JWT de una solicitud HTTP.
     *
     * @param req La solicitud HTTP de la que se extraerá el token JWT. Esta solicitud debe contener
     *            el token JWT en el encabezado de autorización, con el prefijo "Bearer ".
     *
     * @return Las reclamaciones del token JWT como un objeto Claims. Este objeto incluye todas las
     *         reclamaciones que se añadieron al token cuando se creó, como el sujeto, la fecha de
     *         expiración, etc. Si el token no está presente en la solicitud, este método devuelve null.
     *
     * @throws ModelException Si el token ha expirado o es inválido. El mensaje de la excepción
     *                        proporciona más detalles sobre el error.
     *
     * El método funciona de la siguiente manera:
     * 1. Utiliza el método resolveToken para extraer el token JWT de la solicitud HTTP.
     * 2. Si el token está presente, utiliza el método parseJwtClaims para parsear las reclamaciones del token.
     * 3. Si ocurre una excepción ExpiredJwtException, lanza una ModelException indicando que el token ha expirado.
     * 4. Si ocurre cualquier otra excepción, lanza una ModelException indicando que el token es inválido.
     */
    public Claims resolveClaims(HttpServletRequest req) {
        try {
            String token = resolveToken(req);
            if (token == null) return null;
            return parseJwtClaims(token);
        } catch (ExpiredJwtException ex) {
            throw new ModelException("El token ha expirado");
        } catch (Exception ex) {
            throw new ModelException("El token es invalido");
        }
    }

    /**
     * Extrae el token JWT de la cabecera Authorization de la solicitud HTTP.
     *
     * @param request La solicitud HTTP de la que se extraerá el token JWT. Esta solicitud debe contener
     *                el token JWT en el encabezado de autorización, con el prefijo "Bearer ".
     *
     * @return El token JWT como una cadena de texto, sin el prefijo "Bearer ". Si el encabezado de autorización
     *         no está presente en la solicitud, o no comienza con "Bearer ", este método devuelve null.
     *
     * El método funciona de la siguiente manera:
     * 1. Obtiene el valor del encabezado de autorización de la solicitud HTTP.
     * 2. Si el encabezado de autorización está presente y comienza con "Bearer ", extrae el token JWT quitando el prefijo "Bearer ".
     * 3. Devuelve el token JWT, o null si el encabezado de autorización no es válido.
     */
    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * Valida la fecha de expiración de las reclamaciones (claims) de un token JWT.
     *
     * @param claims Las reclamaciones del token JWT como un objeto Claims. Este objeto debe incluir
     *               la fecha de expiración del token.
     *
     * @return true si el token aún no ha expirado (es decir, la fecha de expiración es posterior a la fecha actual),
     *         false en caso contrario.
     *
     * @throws AuthenticationException Si ocurre un error al obtener la fecha de expiración de las reclamaciones.
     *                                 Por ejemplo, si las reclamaciones no incluyen una fecha de expiración,
     *                                 este método lanzará una excepción.
     *
     * El método funciona de la siguiente manera:
     * 1. Obtiene la fecha de expiración de las reclamaciones.
     * 2. Compara la fecha de expiración con la fecha actual.
     * 3. Devuelve true si la fecha de expiración es posterior a la fecha actual, false en caso contrario.
     */
    public boolean validateClaims(Claims claims) throws AuthenticationException {
        try {
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Obtiene el correo electrónico del usuario a partir de las reclamaciones (claims) de un token JWT.
     *
     * @param claims Las reclamaciones del token JWT como un objeto Claims. Este objeto debe incluir
     *               el sujeto del token, que se utiliza como el correo electrónico del usuario.
     *
     * @return El correo electrónico del usuario como una cadena de texto. Si las reclamaciones no incluyen
     *         un sujeto, este método devuelve null.
     *
     * El método funciona de la siguiente manera:
     * 1. Obtiene el sujeto de las reclamaciones con el método getSubject().
     * 2. Devuelve el sujeto, que se utiliza como el correo electrónico del usuario.
     */
    public String getEmail(Claims claims) {
        return claims.getSubject();
    }
}

    /*
     Metodo encargado de obtener los roles de un usuario, mantengo el metodo en caso de un futuro implementar roles

    public List<String> getRoles(Claims claims) {
        return claims.get("roles", List.class);
    }
     */
    


