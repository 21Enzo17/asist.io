package asist.io.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import asist.io.entity.Horario;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, String>{
    
    
}
