package asist.io.scheduled;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import asist.io.service.ITokenService;

@Component
public class TareasProgramadas {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private ITokenService tokenService;

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

    
}
