package asist.io.auth;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import asist.io.service.ICustomUserDetailsService;

@Configuration
public class SecurityConfig  {

    @Autowired
    private ICustomUserDetailsService customUserDetailsService;
    
    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    public SecurityConfig(ICustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }
    /**
     * Bean para encriptar contraseñas
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Bean encargado de la autenticacion
     * @param http
     * @param passwordEncoder
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    /**
     * Bean encargado de la configuracion de la seguridad
     * @param httpSecurity
     * @param authManager
     * @return SecurityFilterChain
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authManager) throws Exception {
        return httpSecurity
            .csrf( config -> {
               config.disable();
            })
            .authorizeHttpRequests(auth ->{
                auth.requestMatchers("/api/v1/auth/**").permitAll();
                auth.requestMatchers("/api/v1/usuario/registro").permitAll();
                auth.requestMatchers("/api/v1/usuario/validar/**").permitAll();
                auth.requestMatchers("/api/v1/usuario/olvide-mi-contrasena").permitAll();
                auth.requestMatchers("/api/v1/usuario/cambiar-contrasena/**").permitAll();
                auth.requestMatchers("/api/v1/usuario/reenviar-correo-confirmacion").permitAll();
                auth.requestMatchers("/testeo").permitAll();
                auth.anyRequest().authenticated();
            })
            .sessionManagement(session ->{
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            })
            .addFilterBefore(jwtAuthorizationFilter,UsernamePasswordAuthenticationFilter.class)
            .build();

        
    }
}

