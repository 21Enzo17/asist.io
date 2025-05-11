package asist.io.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

import asist.io.service.ITokenBlacklistService;

@Service
public class TokenBlacklistServiceImpl implements ITokenBlacklistService {

    private final Logger logger = Logger.getLogger(this.getClass());
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String TOKEN_PREFIX = "blacklisted_token:";

    @Autowired
    public TokenBlacklistServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void addToBlacklist(String token, long expiryTime) {
        String key = TOKEN_PREFIX + token;
        logger.info("Añadiendo token a la lista negra: " + token);
        
        // Almacenar el token en Redis con un tiempo de expiración igual al tiempo restante de vida del token
        redisTemplate.opsForValue().set(key, "blacklisted", expiryTime, TimeUnit.SECONDS);
    }

    @Override
    public boolean isBlacklisted(String token) {
        String key = TOKEN_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        return exists != null && exists;
    }
}