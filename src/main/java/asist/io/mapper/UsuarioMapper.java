package asist.io.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioRegDTO;
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
    public Usuario toEntity(UsuarioGetDTO usuario) {
        Usuario usuarioDto = new Usuario();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setCorreo(usuario.getCorreo());
        usuarioDto.setVerificado(usuario.getVerificado());
        return usuarioDto;
    }


    /**
     * Convierte un usuarioRegDto a un usuario
     * @param usuarioReg
     * @return
     */
    public Usuario toEntity(UsuarioRegDTO usuarioReg) {
        Usuario usuario = new Usuario();
        usuario.setCorreo(usuarioReg.getCorreo());
        usuario.setNombre(usuarioReg.getNombre());
        usuario.setContrasena(passwordEncoder.encode(usuarioReg.getContrasena().getPassword()));
        return usuario;
    }

    /**
     * Convierte un usuario a un usuarioDto
     * @param usuario
     * @return
     */
    public UsuarioGetDTO toDto(Usuario usuario) {
        UsuarioGetDTO usuarioDto = new UsuarioGetDTO();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setCorreo(usuario.getCorreo());
        usuarioDto.setVerificado(usuario.isVerificado());
        return usuarioDto;
    }

}
