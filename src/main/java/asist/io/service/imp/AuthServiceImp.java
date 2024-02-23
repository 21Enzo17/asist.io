package asist.io.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import asist.io.auth.JwtUtil;
import asist.io.dto.usuarioDtos.UsuarioLoginDto;
import asist.io.dto.usuarioDtos.UsuarioLoginResDto;
import asist.io.entity.Usuario;
import asist.io.mapper.UsuarioMapper;
import asist.io.service.IAuthService;

@Service
public class AuthServiceImp implements IAuthService {
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
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginReq.getCorreo(), loginReq.getContrasena()));
        String email = authentication.getName();
        Usuario user = new Usuario();
        user.setCorreo(email);
        String token = jwtUtil.createToken(user);
        UsuarioLoginResDto loginRes = new UsuarioLoginResDto();
        loginRes.setUsuario(usuarioService.buscarUsuario(email));
        loginRes.setToken(token);

        return loginRes;
    }
}
