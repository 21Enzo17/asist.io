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
    public Token findByToken(String token);

    public Boolean existsByToken(String token);
    
    @Modifying
    @Transactional
    @Query("delete from Token t where t.fechaExpiracion < ?1")
    public void deleteByFechaExpiracionBefore(LocalDate fecha);

    public Token findByUsuario(Usuario usuario);

    public Token findByUsuarioAndTipo(Usuario usuario, TipoToken tipo);

    public Boolean existsByUsuario(Usuario usuario);



}
