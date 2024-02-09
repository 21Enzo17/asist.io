package asist.io.repository;

import asist.io.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, String> {
    /**
     * Busca un estudiante por su legajo
     * @param lu legajo del estudiante
     * @return estudiante con el legajo especificado
     */
    @Query("SELECT e FROM Estudiante e WHERE e.lu = ?1")
    public Estudiante findByLu(String lu);

    /**
     * Verifica si existe un estudiante con el legajo especificado
     * @param lu legajo del estudiante
     * @return true si existe un estudiante con el legajo especificado, false si no existe
     */
    public boolean existsByLu(String lu);
}
