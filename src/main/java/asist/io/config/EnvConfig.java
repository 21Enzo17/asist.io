package asist.io.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class EnvConfig {

    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);

    @Autowired
    private Environment springEnv;

    @PostConstruct
    public void init() {
        try {
            // Cargar variables de .env y establecerlas como variables de sistema
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
            
            // Establecer todas las variables del .env como propiedades del sistema
            dotenv.entries().forEach(entry -> {
                if (System.getProperty(entry.getKey()) == null) {
                    System.setProperty(entry.getKey(), entry.getValue());
                }
            });
            
            logger.info("Variables de entorno cargadas correctamente desde .env");
        } catch (Exception e) {
            logger.warn("No se pudo cargar el archivo .env: {}", e.getMessage());
        }
    }
}