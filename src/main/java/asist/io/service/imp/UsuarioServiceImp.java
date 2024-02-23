package asist.io.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import asist.io.dto.usuarioDtos.UsuarioDto;
import asist.io.dto.usuarioDtos.UsuarioRegDto;
import asist.io.entity.Usuario;
import asist.io.exceptions.ModelException;
import asist.io.mapper.UsuarioMapper;
import asist.io.repository.UsuarioRepository;
import asist.io.service.IUsuarioService;

@Service
public class UsuarioServiceImp implements IUsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioMapper usuarioMapper;

    /**
     * Guarda un usuario en la base de datos
     * @param usuario
     * @throws ModelException
     * @return void
     */
    @Override
    public void guardarUsuario(UsuarioRegDto usuario) {
       Usuario usuarioRegistro = new Usuario();
        if(usuarioRepository.findByCorreo(usuario.getCorreo()) == null){
            usuarioRegistro = usuarioMapper.toEntity(usuario);
            usuarioRepository.save(usuarioRegistro);
        }else
            throw new ModelException("Ya existe un usuario con este correo");
             
    }

    /**
     * Elimina un usuario de la base de datos
     * @param correo
     * @throws ModelException
     * @return void
     */
    @Override
    public void eliminarUsuario(String correo) {
        if (usuarioRepository.existsByCorreo(correo)) {
            usuarioRepository.deleteByCorreo(correo);
        }else{
            throw new ModelException("No existe un usuario con este correo");
        }
        
    }

    /**
     * Actualiza un usuario en la base de datos
     * @param usuario
     * @throws ModelException
     * @return void
     */
    @Override
    public void actualizarUsuario(UsuarioDto usuario) {
        if(usuarioRepository.existsByCorreo(usuario.getCorreo())){
            Usuario usuarioActualizado = usuarioMapper.toEntity(usuario);
            usuarioRepository.save(usuarioActualizado);
        }else{
            throw new ModelException("No existe un usuario con este correo");
        }
    }

    /**
     * Busca un usuario en la base de datos
     * @param correo
     * @return UsuarioDto
     */
    @Override
    public UsuarioDto buscarUsuario(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if(usuario != null){
            return usuarioMapper.toDto(usuario);
        }else{
            throw new ModelException("No existe un usuario con este correo");
        }
    }
    

    
}
