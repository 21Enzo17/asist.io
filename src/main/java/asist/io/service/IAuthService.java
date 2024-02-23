package asist.io.service;

import asist.io.dto.usuarioDtos.UsuarioLoginDto;
import asist.io.dto.usuarioDtos.UsuarioLoginResDto;

public interface IAuthService {
    public UsuarioLoginResDto login(UsuarioLoginDto loginReq);
}
