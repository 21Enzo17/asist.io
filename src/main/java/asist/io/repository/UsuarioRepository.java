package asist.io.repository;

import asist.io.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    public Usuario findByCorreo(String correo);

    public Usuario deleteByCorreo(String correo);

    public Boolean existsByCorreo(String correo);

}
