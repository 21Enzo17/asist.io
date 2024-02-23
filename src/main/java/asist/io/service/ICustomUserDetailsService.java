package asist.io.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface ICustomUserDetailsService extends UserDetailsService{

    public UserDetails loadUserByUsername(String username);

    
}
