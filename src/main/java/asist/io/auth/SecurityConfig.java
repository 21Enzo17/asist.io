package asist.io.auth;



import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import asist.io.service.ICustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
     * Crea un bean de PasswordEncoder para encriptar las contraseñas de los usuarios.
     *
     * @return Un objeto PasswordEncoder que utiliza BCrypt para encriptar las contraseñas. BCrypt es un algoritmo de hash
     *         para contraseñas que utiliza un salto para proteger contra ataques de diccionario y de fuerza bruta.
     *
     * El método funciona de la siguiente manera:
     * 1. Crea un nuevo objeto BCryptPasswordEncoder.
     * 2. Devuelve el objeto BCryptPasswordEncoder.
     *
     * Puedes utilizar este bean para encriptar una contraseña antes de guardarla en la base de datos, y para verificar
     * una contraseña proporcionada por el usuario al autenticarse.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Crea un bean de AuthenticationManager para manejar la autenticación de usuarios.
     *
     * @param http El objeto HttpSecurity que se utiliza para configurar la seguridad web.
     * @param passwordEncoder El codificador de contraseñas que se utiliza para codificar las contraseñas de los usuarios.
     *
     * @return Un objeto AuthenticationManager que se puede utilizar para autenticar a los usuarios.
     *
     * @throws Exception Si ocurre un error al crear el AuthenticationManager.
     *
     * El método funciona de la siguiente manera:
     * 1. Obtiene un objeto AuthenticationManagerBuilder del objeto HttpSecurity.
     * 2. Configura el AuthenticationManagerBuilder para que utilice el servicio de detalles de usuario personalizado y el codificador de contraseñas proporcionado.
     * 3. Construye y devuelve el AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }
    
    /**
     * Clase encargada de manejar la respuesta cuando no se esta autenticado, retornando un error personalizado
     */
    private static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        /**
         * Método que se invoca cuando un usuario no autenticado intenta acceder a un recurso protegido.
         *
         * @param request La solicitud HTTP que el usuario intentó hacer.
         * @param response La respuesta HTTP que se enviará al usuario.
         * @param authException La excepción de autenticación que se lanzó cuando el usuario intentó acceder al recurso.
         *
         * @throws IOException Si ocurre un error al escribir la respuesta.
         *
         * El método funciona de la siguiente manera:
         * 1. Establece el estado de la respuesta HTTP en SC_UNAUTHORIZED (401), indicando que el usuario no está autorizado para acceder al recurso.
         * 2. Establece el tipo de contenido de la respuesta en "application/json".
         * 3. Crea un mensaje de error en formato JSON.
         * 4. Escribe el mensaje de error en el cuerpo de la respuesta.
        */
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String json = String.format("{\"Error\": \"%s\"}", "No estás autorizado para acceder a este recurso");
            response.getWriter().write(json);
        }
    }

    /**
     * Configura la cadena de filtros de seguridad para la aplicación.
     *
     * @param httpSecurity El objeto HttpSecurity que se utiliza para configurar la seguridad web.
     * @param authManager El gestor de autenticación que se utiliza para autenticar a los usuarios.
     *
     * @return Un objeto SecurityFilterChain que representa la cadena de filtros de seguridad configurada.
     *
     * @throws Exception Si ocurre un error al configurar la cadena de filtros de seguridad.
     *
     * El método funciona de la siguiente manera:
     * 1. Configura el punto de entrada de autenticación para manejar las excepciones de autenticación.
     * 2. Configura las reglas de autorización para las rutas de la aplicación.
     * 3. Configura la política de creación de sesiones para ser sin estado.
     * 4. Añade el filtro de autorización JWT antes del filtro de autenticación de nombre de usuario y contraseña.
     * 5. Construye y devuelve la cadena de filtros de seguridad.
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .exceptionHandling(exception ->{
                exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
            })
            .csrf( config ->{
                config.disable();
            })
            .authorizeHttpRequests(auth ->{
                auth.requestMatchers("/api/v1/auth/login").permitAll();
                auth.requestMatchers("/api/v1/usuario/registro").permitAll();
                auth.requestMatchers("/api/v1/usuario/validar/**").permitAll();
                auth.requestMatchers("/api/v1/usuario/olvide-mi-contrasena").permitAll();
                auth.requestMatchers("/api/v1/usuario/cambiar-contrasena/**").permitAll();
                auth.requestMatchers("/api/v1/usuario/reenviar-correo-confirmacion").permitAll();
                auth.requestMatchers("/api/v1/asistencias/registrar").permitAll();
                auth.requestMatchers("/api/v1/cursos/codigo-asistencia/**").permitAll();
                auth.requestMatchers("/api/v1/horarios/obtenerHorarios/**").permitAll();
                auth.anyRequest().authenticated();
            })
            .sessionManagement(session ->{
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            })
            .addFilterBefore(jwtAuthorizationFilter,UsernamePasswordAuthenticationFilter.class)
            .build();

        
    }
}

