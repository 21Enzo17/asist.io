package asist.io.service.imp;

import java.time.LocalDate;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asist.io.entity.Token;
import asist.io.entity.Usuario;
import asist.io.enums.TipoToken;
import asist.io.exceptions.ModelException;
import asist.io.repository.TokenRepository;
import asist.io.service.ITokenService;

@Service
public class TokenServiceImp implements ITokenService{
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private TokenRepository tokenRepository;


    /**
     * Genera un token para un usuario, en caso de ya tener un token valido del mismo tipo, retorna el token existente
     * @param correo
     * @param tipo
     * @param usuario
     * @return String
     */
    @Override
    public String generarToken(String correo, String tipo, Usuario usuario) {
        Token token = tokenRepository.findByUsuarioAndTipo(usuario, TipoToken.valueOf(tipo.toUpperCase()));
        if (token != null && token.getFechaExpiracion().isAfter(LocalDate.now())) {
            logger.info("El usuario ya tiene un token de este tipo, retornando token");
            return token.getToken();
        }else{
            logger.info("Generando token para el usuario: "+usuario.getCorreo());
            token = new Token(UUID.randomUUID().toString(),usuario,TipoToken.valueOf(tipo.toUpperCase()));
            tokenRepository.save(token);
            return token.getToken();
        }
        
    }

    /**
     * Valida un token, si el token no existe o ha expirado, lanza una excepcion
     * @param token
     */
    @Override
    public void validarToken(String token) {
        Token tokenValidar = tokenRepository.findByToken(token);
        logger.info("Validando token: "+token);
        if(tokenValidar == null){
            throw new ModelException("El token no es valido, por favor genere un nuevo token");
        }else if(tokenValidar.getFechaExpiracion().isBefore(LocalDate.now())){
            throw new ModelException("El token ha expirado, por favor genere un nuevo token");
        }
    }

    /**
     * Retorna el usuario asociado a un token
     * @param token
     * @return Usuario
     */
    @Override
    public Usuario obtenerUsuario(String token) {
        try{
            return tokenRepository.findByToken(token).getUsuario();
        }catch(Exception e){
            throw new ModelException("No existe un token asociado a este usuario");
        }
        
    }

    /**
     * Elimina un token en caso de que exista
     * @param token
     */
    @Override
    public void eliminarToken(String token) {    
        if(tokenRepository.existsByToken(token)){
            logger.info("Eliminando token: "+token);
            tokenRepository.delete(tokenRepository.findByToken(token));
        }else{
            throw new ModelException("El token no existe");
        }
        
    }

    /**
     * Borra los tokens vencidos
     */
    @Override
    public void borrarTokensVencidos() {
        logger.info("Borrando tokens vencidos");
        tokenRepository.deleteByFechaExpiracionBefore(LocalDate.now());
    }
    
    /**
     * Retorna el token asociado a un usuario y de un tipo especifico
     * @param usuario
     * @param tipo
     */
    @Override
    public String obtenerTokenPorUsuarioTipo(Usuario usuario, String tipo) {
        try{
            return tokenRepository.findByUsuarioAndTipo(usuario, TipoToken.valueOf(tipo)).getToken();
        }catch(Exception e){
            throw new ModelException("El usuario no tiene un token asociado, "+ e.getMessage());
        }
    }
}
