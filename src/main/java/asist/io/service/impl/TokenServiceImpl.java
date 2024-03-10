package asist.io.service.impl;

import java.time.LocalDate;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asist.io.entity.Token;
import asist.io.entity.Usuario;
import asist.io.enums.TipoToken;
import asist.io.exception.ModelException;
import asist.io.repository.TokenRepository;
import asist.io.service.ITokenService;

@Service
public class TokenServiceImpl implements ITokenService{
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private TokenRepository tokenRepository;


    /**
     * Metodo encargado de generar un token. O en caso de tener un token del mismo tipo no vencido retornar este mismo. 
     * @param correo Correo del usuario al que se le va a generar el token.
     * @param tipo Tipo de token que se va a generar (VERIFICACION, RECUPERACION).
     * @param usuario Usuario al que se le va a generar el token.
     * @return Token generado.
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
     * Metodo encargado de validar un token.
     * Se valida que el token exista y que no este vencido.
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
     * Metodo encargado de obtener un usuario a partir de un token.
     * @param token 
     * @return Usuario asociado al token.
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
     * Metodo encargado de eliminar un token.
     * @param token
     */
    @Override
    @SuppressWarnings("null")
    public void eliminarToken(String token) {    
        if(tokenRepository.existsByToken(token)){
            logger.info("Eliminando token: "+token);
            tokenRepository.delete(tokenRepository.findByToken(token));
        }else{
            throw new ModelException("El token no existe");
        }
        
    }

    /**
     * Metodo encargado de borrar los tokens vencidos.
     * Se ejecuta todos los dias a las 03:00 am. mediante un Scheduled.
     */
    @Override
    public void borrarTokensVencidos() {
        logger.info("Borrando tokens vencidos");
        tokenRepository.deleteByFechaExpiracionBefore(LocalDate.now());
    }
    
    /**
     * Metodo encargado de obtener un token por usuario y tipo.
     * @param usuario 
     * @param tipo (VERIFICACION, RECUPERACION).
     * @return Token
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
