package asist.io.mapper;

import org.springframework.stereotype.Component;

import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.entity.Usuario;

@Component
public class UsuarioMapper {


    /**
     * Convierte un usuarioDto a un usuario
     * @param usuario
     * @return
     */
    public static Usuario toEntity(UsuarioGetDTO usuario) {
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
    public static Usuario toEntity(UsuarioPostDTO usuarioReg) {
        Usuario usuario = new Usuario();
        usuario.setCorreo(usuarioReg.getCorreo());
        usuario.setNombre(usuarioReg.getNombre());
        usuario.setContrasena(usuarioReg.getContrasena());
        return usuario;
    }

    /**
     * Convierte un usuario a un usuarioDto
     * @param usuario
     * @return
     */
    public static UsuarioGetDTO toDto(Usuario usuario) {
        UsuarioGetDTO usuarioDto = new UsuarioGetDTO();
        usuarioDto.setId(usuario.getId());
        usuarioDto.setNombre(usuario.getNombre());
        usuarioDto.setCorreo(usuario.getCorreo());
        usuarioDto.setVerificado(usuario.isVerificado());
        return usuarioDto;
    }

}
