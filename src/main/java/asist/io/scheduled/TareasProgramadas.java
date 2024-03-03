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

    @Scheduled(cron = "0 0 3 * * ?")
    public void borrarTokensVencidos() {
        logger.info("Borrando tokens vencidos...");
        tokenService.borrarTokensVencidos();
        logger.info("Tokens vencidos borrados exitosamente!");
    }

    
}
