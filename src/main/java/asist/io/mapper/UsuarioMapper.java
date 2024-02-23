package asist.io.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import asist.io.dto.usuarioDtos.UsuarioDto;
import asist.io.dto.usuarioDtos.UsuarioRegDto;
import asist.io.entity.Usuario;

@Component
public class UsuarioMapper {

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Convierte un usuarioDto a un usuario
     * @param usuario
     * @return
     */
    public Usuario toEntity(UsuarioDto usuario) {
        Usuario usuarioDto = new Usuario();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setCorreo(usuario.getCorreo());
        return usuarioDto;
    }


    /**
     * Convierte un usuarioRegDto a un usuario
     * @param usuarioReg
     * @return
     */
    public Usuario toEntity(UsuarioRegDto usuarioReg) {
        Usuario usuario = new Usuario();
        usuario.setCorreo(usuarioReg.getCorreo());
        usuario.setNombre(usuarioReg.getNombre());
        usuario.setContrasena(passwordEncoder.encode(usuarioReg.getContrasena()));
        return usuario;
    }

    /**
     * Convierte un usuario a un usuarioDto
     * @param usuario
     * @return
     */
    public UsuarioDto toDto(Usuario usuario) {
        UsuarioDto usuarioDto = new UsuarioDto();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setCorreo(usuario.getCorreo());
        return usuarioDto;
    }

}
