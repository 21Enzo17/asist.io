package asist.io.service;

public interface ITokenBlacklistService {
    
    /**
     * Añade un token a la lista negra
     * @param token Token JWT a añadir a la lista negra
     * @param expiryTime Tiempo en segundos hasta que el token expire
     */
    void addToBlacklist(String token, long expiryTime);
    
    /**
     * Verifica si un token está en la lista negra
     * @param token Token JWT a verificar
     * @return true si está en la lista negra, false en caso contrario
     */
    boolean isBlacklisted(String token);
}