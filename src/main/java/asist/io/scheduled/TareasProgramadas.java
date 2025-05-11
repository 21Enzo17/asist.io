package asist.io.scheduled;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import asist.io.auth.JwtUtil;
import asist.io.service.IRefreshTokenService;
import asist.io.service.ITokenService;

@Component
public class TareasProgramadas {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private ITokenService tokenService;
    
    @Autowired
    private IRefreshTokenService refreshTokenService;
    
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Borra los tokens JWT vencidos de la base de datos.
     *
     * Este método se ejecuta automáticamente a las 3 AM todos los días, gracias a la anotación @Scheduled.
     *
     * El método funciona de la siguiente manera:
     * 1. Registra un mensaje en el log indicando que el proceso de borrado de tokens vencidos ha comenzado.
     * 2. Llama al método borrarTokensVencidos del servicio de tokens, que se encarga de borrar los tokens vencidos de la base de datos.
     * 3. Registra un mensaje en el log indicando que el proceso de borrado de tokens vencidos ha terminado exitosamente.
     * 
     * Nota: Ademas se agrego la notacion @EnableScheduling en la clase Application.java para habilitar las tareas programadas
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void borrarTokensVencidos() {
        logger.info("Borrando tokens vencidos...");
        tokenService.borrarTokensVencidos();
        logger.info("Tokens vencidos borrados exitosamente!");
    }
    
    /**
     * Borra los tokens de refresco (refresh tokens) expirados o revocados de la base de datos.
     *
     * Este método se ejecuta automáticamente a las 3:30 AM todos los días, gracias a la anotación @Scheduled.
     *
     * El método funciona de la siguiente manera:
     * 1. Registra un mensaje en el log indicando que el proceso de limpieza de tokens de refresco ha comenzado.
     * 2. Llama al método eliminarTokensExpirados del servicio de tokens de refresco, que elimina los tokens expirados o revocados.
     * 3. Registra un mensaje en el log indicando que el proceso de limpieza ha terminado exitosamente.
     */
    @Scheduled(cron = "0 30 3 * * ?")
    public void eliminarRefreshTokensExpirados() {
        logger.info("Eliminando tokens de refresco expirados o revocados...");
        refreshTokenService.eliminarTokensExpirados();
        logger.info("Tokens de refresco expirados o revocados eliminados exitosamente!");
    }
    
    /**
     * Rota las claves RSA utilizadas para firmar los tokens JWT.
     * 
     * Este método se ejecuta automáticamente el primer día de cada mes a las 2:00 AM.
     * La rotación periódica de claves mejora significativamente la seguridad, ya que limita
     * el tiempo durante el cual un token comprometido podría ser útil para un atacante.
     * 
     * El método funciona de la siguiente manera:
     * 1. Registra un mensaje en el log indicando que el proceso de rotación de claves ha comenzado.
     * 2. Llama al método rotateKeys del servicio JwtUtil, que genera un nuevo par de claves RSA.
     * 3. Registra un mensaje en el log indicando si el proceso de rotación ha sido exitoso o no.
     * 
     * Nota: Esta rotación no invalida tokens existentes, ya que estos seguirán siendo verificados
     * con la clave pública anterior hasta que expiren naturalmente o sean revocados manualmente.
     */
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
}
