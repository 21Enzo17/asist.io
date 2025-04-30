package asist.io.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private final Dotenv dotenv = Dotenv.load();
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(dotenv.get("REDIS_HOST", "localhost"));
        config.setPort(Integer.parseInt(dotenv.get("REDIS_PORT", "6379")));
        
        String password = dotenv.get("REDIS_PASSWORD");
        if (password != null && !password.isEmpty()) {
            config.setPassword(password);
        }
        
        config.setDatabase(Integer.parseInt(dotenv.get("REDIS_DATABASE", "0")));
        
        return new LettuceConnectionFactory(config);
    }
    
    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}