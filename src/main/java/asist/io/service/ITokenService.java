package asist.io.service;

import asist.io.entity.Usuario;

public interface ITokenService {
    public String generarToken(String correo,String tipo, Usuario usuario);
    public void validarToken(String token);
    public Usuario obtenerUsuario(String token);
    public void eliminarToken(String token);
    public void borrarTokensVencidos();
    public String obtenerTokenPorUsuarioTipo(Usuario usuario, String tipo);
}
