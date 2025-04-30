# Documentación del Sistema de Seguridad - Asist.io

## Índice
1. [Arquitectura de Seguridad](#arquitectura-de-seguridad)
2. [Autenticación con JWT](#autenticación-con-jwt)
3. [Seguridad de Tokens](#seguridad-de-tokens)
4. [Refresh Tokens](#refresh-tokens)
5. [Lista Negra de Tokens (Blacklisting)](#lista-negra-de-tokens)
6. [Rotación de Claves Criptográficas](#rotación-de-claves-criptográficas)
7. [Tareas Programadas de Mantenimiento](#tareas-programadas-de-mantenimiento)
8. [Protección Contra Ataques Comunes](#protección-contra-ataques-comunes)
9. [Implementaciones Técnicas Específicas](#implementaciones-técnicas-específicas)
10. [Mejores Prácticas Implementadas](#mejores-prácticas-implementadas)

## Arquitectura de Seguridad

La seguridad de Asist.io está construida sobre una arquitectura multicapa que implementa el modelo de defensa en profundidad. Utiliza un enfoque basado en tokens JWT (JSON Web Tokens) con criptografía asimétrica, complementado con un sistema de refresh tokens y una lista negra de tokens revocados.


### Componentes Principales:

- **JwtUtil**: Núcleo del sistema de seguridad que maneja la generación, validación y revocación de tokens JWT.
- **JwtAuthorizationFilter**: Filtro de Spring Security que intercepta todas las peticiones y valida los tokens JWT.
- **AuthService**: Servicio que maneja la lógica de autenticación, generación de tokens y gestión de sesiones.
- **RefreshTokenService**: Gestiona los tokens de actualización para renovar la sesión sin requerir credenciales.
- **TokenBlacklistService**: Servicio que mantiene una lista negra de tokens revocados en Redis.
- **SecurityConfig**: Configuración general de seguridad de la aplicación.

## Autenticación con JWT

El sistema utiliza tokens JWT firmados con RSA (algoritmo RS256) para autenticar las solicitudes a la API. Este enfoque proporciona una autenticación sin estado (stateless) que es escalable y segura.

### Flujo de Autenticación:

1. **Inicio de Sesión**:
   ```
   POST /api/v1/auth/login
   {
     "correo": "usuario@ejemplo.com",
     "contrasena": "contraseña_segura"
   }
   ```

2. **Respuesta del Servidor**:
   ```json
   {
     "status": "success",
     "data": {
       "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
       "refreshToken": "8f7e6d5c-4b3a-2a1c-0z9y-8x7w6v5u4t3s",
       "usuario": {
         "id": "550e8400-e29b-41d4-a716-446655440000",
         "nombre": "Usuario Ejemplo",
         "correo": "usuario@ejemplo.com"
       }
     }
   }
   ```

3. **Acceso a Recursos Protegidos**:
   Las solicitudes a endpoints protegidos deben incluir el token JWT en el encabezado de Autorización:
   ```
   GET /api/v1/recursos/protegidos
   Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

4. **Verificación de Token**:
   El filtro `JwtAuthorizationFilter` intercepta cada solicitud, extrae el token, verifica su firma y validez, y establece el contexto de seguridad si es válido.

## Seguridad de Tokens

Los tokens JWT emitidos por Asist.io incorporan múltiples capas de seguridad:

### Estructura del Token:

```json
{
  "alg": "RS256",
  "typ": "JWT"
}
{
  "sub": "usuario@ejemplo.com",
  "userName": "Usuario Ejemplo",
  "nonce": "550e8400-e29b-41d4-a716-446655440000",
  "iss": "asist.io",
  "aud": "asist.io-client",
  "iat": 1619802612,
  "exp": 1619803512,
  "jti": "a71bdc2e-f7b9-4c06-9257-d1695e099b3c"
}
```

### Características Clave:

1. **Algoritmo RS256**: Utiliza criptografía asimétrica con una clave privada para la firma y una clave pública para la verificación.
2. **Tiempo de Expiración Corto**: Los tokens de acceso expiran tras 15 minutos, minimizando la ventana de vulnerabilidad.
3. **Nonce Único**: Cada token incluye un valor aleatorio único (UUID) para prevenir ataques de reproducción.
4. **Claims Validados**: Se validan emisor (issuer), audiencia, tiempo de expiración y otros claims.
5. **ID Único**: Cada token tiene un identificador único (jti) para facilitar la revocación si es necesario.

## Refresh Tokens

Para mejorar la experiencia del usuario sin comprometer la seguridad, el sistema utiliza refresh tokens de larga duración que permiten obtener nuevos tokens de acceso sin volver a introducir credenciales.

### Características:

1. **Almacenamiento**: Los refresh tokens se guardan en la base de datos con una asociación directa al usuario.
2. **Formato**: Utilizan UUIDs como identificadores para máxima seguridad.
3. **Validación**: Se validan en cada uso para asegurar que no estén expirados ni revocados.
4. **Revocación en Cascada**: Al cerrar sesión o cambiar contraseña, se revocan todos los refresh tokens del usuario.

### Proceso de Renovación:

1. Cuando el token de acceso expira, el cliente envía el refresh token:
   ```
   POST /api/v1/auth/refresh-token
   {
     "refreshToken": "8f7e6d5c-4b3a-2a1c-0z9y-8x7w6v5u4t3s"
   }
   ```

2. Si el refresh token es válido, se emite un nuevo token de acceso:
   ```json
   {
     "status": "success",
     "data": {
       "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
     }
   }
   ```

## Lista Negra de Tokens

Asist.io implementa una lista negra de tokens en Redis para revocar tokens activos de forma inmediata cuando sea necesario, como en cierres de sesión o situaciones de seguridad comprometida.

### Características:

1. **Almacenamiento Eficiente**: Utiliza Redis como almacén clave-valor de alto rendimiento.
2. **Expiración Automática**: Los tokens en la lista negra se eliminan automáticamente cuando alcanzan su tiempo original de expiración.
3. **Verificación Rápida**: Cada solicitud verifica en tiempo constante O(1) si el token está en la lista negra.

### Implementación:

```java
@Override
public void addToBlacklist(String token, long expiryTime) {
    String key = TOKEN_PREFIX + token;
    redisTemplate.opsForValue().set(key, "blacklisted", expiryTime, TimeUnit.SECONDS);
}

@Override
public boolean isBlacklisted(String token) {
    String key = TOKEN_PREFIX + token;
    Boolean exists = redisTemplate.hasKey(key);
    return exists != null && exists;
}
```

## Rotación de Claves Criptográficas

El sistema implementa rotación automática de claves criptográficas para mitigar el riesgo de compromiso de claves a largo plazo.

### Características:

1. **Rotación Periódica**: Las claves RSA se rotan automáticamente el primer día de cada mes.
2. **Claves RSA de 2048 bits**: Se generan claves seguras según estándares actuales.
3. **Almacenamiento Seguro**: Las claves se almacenan en el sistema de archivos en la ruta `config/keys/`.

### Implementación:

La rotación se realiza mediante una tarea programada:

```java
@Scheduled(cron = "0 0 2 1 * ?") // Primer día de cada mes a las 2:00 AM
public void rotarClavesRSA() {
    logger.info("Iniciando rotación periódica de claves RSA para JWT...");
    boolean success = jwtUtil.rotateKeys();
    
    if (success) {
        logger.info("Rotación de claves RSA completada exitosamente");
    } else {
        logger.error("Error durante la rotación de claves RSA");
    }
}
```

## Tareas Programadas de Mantenimiento

El sistema incluye varias tareas programadas para mantener la limpieza y eficiencia del sistema de seguridad.

### Tareas Implementadas:

1. **Limpieza de Tokens JWT Vencidos**:
   ```java
   @Scheduled(cron = "0 0 3 * * ?") // 3:00 AM diariamente
   public void borrarTokensVencidos() {
       tokenService.borrarTokensVencidos();
   }
   ```

2. **Limpieza de Refresh Tokens Expirados**:
   ```java
   @Scheduled(cron = "0 30 3 * * ?") // 3:30 AM diariamente
   public void eliminarRefreshTokensExpirados() {
       refreshTokenService.eliminarTokensExpirados();
   }
   ```

3. **Rotación de Claves RSA** (como se mencionó anteriormente).

## Protección Contra Ataques Comunes

El sistema está diseñado para resistir diversos tipos de ataques:

### 1. Ataques de Reproducción (Replay Attacks)
- **Protección**: Uso de nonce único en cada token.
- **Beneficio**: Impide que un token interceptado pueda ser reutilizado.

### 2. Ataques Man-in-the-Middle
- **Protección**: Firma RSA con clave privada.
- **Beneficio**: Imposibilita la modificación de tokens sin la clave privada.

### 3. Ataques de Fuerza Bruta
- **Protección**: Uso de BCrypt para hash de contraseñas.
- **Beneficio**: Hace computacionalmente prohibitivo el descifrado de contraseñas.

### 4. Robo de Tokens
- **Protección**: Tiempo de expiración corto (15 minutos) y lista negra.
- **Beneficio**: Minimiza la ventana de uso de tokens robados.

### 5. Ataques de Sesión
- **Protección**: Arquitectura stateless con JWT.
- **Beneficio**: Elimina vulnerabilidades asociadas al manejo de sesiones en servidor.

## Implementaciones Técnicas Específicas

### Generación de Claves RSA:

```java
private void generateAndSaveKeyPair() throws NoSuchAlgorithmException, IOException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048); // Tamaño de clave recomendado
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    
    privateKey = keyPair.getPrivate();
    publicKey = keyPair.getPublic();
    
    // Guardar clave privada
    PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
    String privateKeyString = Base64.getEncoder().encodeToString(pkcs8EncodedKeySpec.getEncoded());
    Files.write(Paths.get(KEY_DIRECTORY, PRIVATE_KEY_FILE), privateKeyString.getBytes());
    
    // Guardar clave pública
    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
    String publicKeyString = Base64.getEncoder().encodeToString(x509EncodedKeySpec.getEncoded());
    Files.write(Paths.get(KEY_DIRECTORY, PUBLIC_KEY_FILE), publicKeyString.getBytes());
}
```

### Creación de Token JWT:

```java
private String createToken(Usuario user, long validityInSeconds) {
    Date tokenCreateTime = new Date();
    Date tokenValidity = new Date(tokenCreateTime.getTime() + TimeUnit.SECONDS.toMillis(validityInSeconds));
    String tokenId = UUID.randomUUID().toString();
    
    Map<String, Object> claims = new HashMap<>();
    claims.put("userName", user.getNombre());
    claims.put("nonce", UUID.randomUUID().toString());
    
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
```

### Validación de Claims:

```java
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
```

## Mejores Prácticas Implementadas

1. **Criptografía Moderna**: Uso de algoritmos actuales y seguros (RSA-2048, BCrypt).
2. **Principio de Privilegio Mínimo**: Cada token contiene solo los permisos necesarios.
3. **Defensa en Profundidad**: Múltiples capas de seguridad (tokens de corta duración, criptografía asimétrica, lista negra, validación de claims).
4. **Rotación de Credenciales**: Rotación automática de claves de firma.
5. **Encriptación de Contraseñas**: Hash de contraseñas con BCrypt y salt aleatorio.
6. **Validación Completa**: Todos los tokens y datos de entrada son completamente validados.
7. **Mantenimiento Automático**: Tareas programadas para limpieza y rotación de claves.
8. **Auditoría**: Registro detallado de eventos de seguridad críticos.

---

Este documento proporciona una visión general de la arquitectura de seguridad de Asist.io. Para detalles sobre la implementación específica de cualquier componente, consulte el código fuente correspondiente en el repositorio.