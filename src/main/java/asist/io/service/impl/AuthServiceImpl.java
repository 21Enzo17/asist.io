package asist.io.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import org.apache.log4j.Logger;
import asist.io.auth.JwtUtil;
import asist.io.dto.usuarioDTO.UsuarioLoginDTO;
import asist.io.dto.usuarioDTO.UsuarioGetLoginDTO;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.service.IAuthService;

@Service
public class AuthServiceImpl implements IAuthService {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioServiceImpl usuarioService;


    /**
     * Metodo encargado del logueo de un usuario
     * @param loginReq Datos del usuario para loguearse
     * @return Datos del usuario logueado (Token y un objeto usuarioDto con sus datos)
     */
    @Override
    public UsuarioGetLoginDTO login(UsuarioLoginDTO loginReq) {
        Authentication authentication;
        try{
           authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getCorreo(), loginReq.getContrasena()));
        }catch(BadCredentialsException e){
            logger.error("Credenciales incorrectas, " + loginReq.getCorreo());
            throw new ModelException("Credenciales incorrectas");
        }
        
        String email = authentication.getName();
        Usuario user = new Usuario();
        user.setCorreo(email);
        String token = jwtUtil.createToken(user);
        UsuarioGetLoginDTO loginRes = new UsuarioGetLoginDTO();
        loginRes.setUsuario(usuarioService.buscarUsuarioDto(email));
        loginRes.setToken(token);
        logger.info("Usuario autenticado correctamente, " + loginRes.getUsuario().getCorreo());
        return loginRes;
    }
}
