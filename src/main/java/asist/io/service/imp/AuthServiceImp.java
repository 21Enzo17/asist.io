package asist.io.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import org.apache.log4j.Logger;
import asist.io.auth.JwtUtil;
import asist.io.dto.usuarioDtos.UsuarioLoginDto;
import asist.io.dto.usuarioDtos.UsuarioLoginResDto;
import asist.io.entity.Usuario;
import asist.io.exceptions.ModelException;
import asist.io.service.IAuthService;

@Service
public class AuthServiceImp implements IAuthService {
    private final Logger logger =  Logger.getLogger(this.getClass());

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioServiceImp usuarioService;


    /**
     * Metodo para autenticar un usuario
     * @param loginReq
     * @return UsuarioLoginResDto
     * @throws Exception
     * @return UsuarioLoginResDto
     */
    @Override
    public UsuarioLoginResDto login(UsuarioLoginDto loginReq) {
        Authentication authentication;
        try{
           authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getCorreo(), loginReq.getContrasena()));
        }catch(BadCredentialsException e){
            logger.error("Credenciales incorrectas");
            throw new ModelException("Credenciales incorrectas");
        }
        
        String email = authentication.getName();
        Usuario user = new Usuario();
        user.setCorreo(email);
        String token = jwtUtil.createToken(user);
        UsuarioLoginResDto loginRes = new UsuarioLoginResDto();
        loginRes.setUsuario(usuarioService.buscarUsuarioDto(email));
        loginRes.setToken(token);
        logger.info("Usuario autenticado correctamente, " + loginRes.getUsuario().getCorreo());
        return loginRes;
    }
}
