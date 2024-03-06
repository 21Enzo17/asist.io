package asist.io.service;

import asist.io.entity.Usuario;

public interface ITokenService {

    /**
     * Metodo encargado de generar un token. O en caso de tener un token del mismo tipo no vencido retornar este mismo. 
     * @param correo Correo del usuario al que se le va a generar el token.
     * @param tipo Tipo de token que se va a generar (VERIFICACION, RECUPERACION).
     * @param usuario Usuario al que se le va a generar el token.
     * @return Token generado.
     */
    public String generarToken(String correo,String tipo, Usuario usuario);

    /**
     * Metodo encargado de validar un token.
     * Se valida que el token exista y que no este vencido.
     * @param token 
     */
    public void validarToken(String token);

    /**
     * Metodo encargado de obtener un usuario a partir de un token.
     * @param token 
     * @return Usuario asociado al token.
     */
    public Usuario obtenerUsuario(String token);

    /**
     * Metodo encargado de eliminar un token.
     * @param token
     */
    public void eliminarToken(String token);

    /**
     * Metodo encargado de borrar los tokens vencidos.
     * Se ejecuta todos los dias a las 03:00 am. mediante un Scheduled.
     */
    public void borrarTokensVencidos();

    /**
     * Metodo encargado de obtener un token por usuario y tipo.
     * @param usuario 
     * @param tipo (VERIFICACION, RECUPERACION).
     * @return Token
     */
    public String obtenerTokenPorUsuarioTipo(Usuario usuario, String tipo);
}
