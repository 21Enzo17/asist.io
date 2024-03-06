package asist.io.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface ICustomUserDetailsService extends UserDetailsService{


    /**
     * Metodo encargado de obtener los detalles de un usuario, este metodo es usado por
     * spring security.
     * @param username Nombre de usuario
     * @return Detalles del usuario 
     */
    public UserDetails loadUserByUsername(String username);

    
}
