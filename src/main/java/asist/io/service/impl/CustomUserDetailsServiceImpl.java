package asist.io.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import asist.io.entity.Usuario;
import asist.io.repository.UsuarioRepository;
import asist.io.service.ICustomUserDetailsService;

@Service
public class CustomUserDetailsServiceImpl implements ICustomUserDetailsService{
    @Autowired
    private UsuarioRepository userRepository;


    /**
     * Metodo encargado de obtener los detalles de un usuario, este metodo es usado por
     * spring security.
     * @param username Nombre de usuario
     * @return Detalles del usuario 
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario user = userRepository.findByCorreo(email);
        if (user == null) {
            throw new UsernameNotFoundException("Usuario no encontrado"); // Aqui no se puede retornar la modelException por un problema con este metodo
        }
        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getCorreo())
                        .password(user.getContrasena())
                        .build();
        return userDetails;
    }

}