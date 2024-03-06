package asist.io.repository;

import asist.io.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * Obtiene los estudiantes que están inscriptos en un curso
     * @param id Id del curso
     * @return Lista de estudiantes inscriptos en el curso
     */
    @Query("SELECT e FROM Estudiante e INNER JOIN Inscripcion i ON e.id = i.estudiante.id INNER JOIN Curso c ON i.curso.id = c.id WHERE c.id = ?1")
    List<Estudiante> obtenerEstudiantesPorIdCurso(String id);
}
