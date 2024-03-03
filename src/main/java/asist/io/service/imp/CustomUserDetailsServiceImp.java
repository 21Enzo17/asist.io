package asist.io.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import asist.io.entity.Usuario;
import asist.io.repository.UsuarioRepository;
import asist.io.service.ICustomUserDetailsService;

@Service
public class CustomUserDetailsServiceImp implements ICustomUserDetailsService{
    @Autowired
    private UsuarioRepository userRepository;


    /**
     * Metodo encargado de buscar un usuario por su correo y retornarlo como UserDetails, usado por AuthenticationManager
     * @param email
     * @return UserDetails
     * @throws UsernameNotFoundException
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