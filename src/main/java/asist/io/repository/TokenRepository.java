package asist.io.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asist.io.entity.Token;
import asist.io.entity.Usuario;
import asist.io.enums.TipoToken;
import jakarta.transaction.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String>{
    
    /**
     * Busca un token por el token 
     * @param token token a buscar
     * @return Token
     */
    public Token findByToken(String token);

    /**
     * Verifica si existe un token con el token dado
     * @param token token a buscar
     * @return true si existe, false si no
     */
    public Boolean existsByToken(String token);
    
    /**
     * Elimina los tokens que tengan una fecha de expiracion menor a la fecha dada
     * @param fecha
     */
    @Modifying
    @Transactional
    @Query("delete from Token t where t.fechaExpiracion < ?1")
    public void deleteByFechaExpiracionBefore(LocalDate fecha);

    /**
     * Busca un token por usuario
     * @param usuario
     * @return Token
     */
    public Token findByUsuario(Usuario usuario);

    /**
     * Busca un token por usuario y tipo
     * @param usuario    
     * @param tipo
     * @return Token
     */
    public Token findByUsuarioAndTipo(Usuario usuario, TipoToken tipo);

    /**
     * Verifica si existe un token con el usuario dado
     * @param usuario usuario a buscar
     * @return true si existe, false si no
     */
    public Boolean existsByUsuario(Usuario usuario);



}
