package asist.io.repository;

import asist.io.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, String> {
    /**
     * Busca un estudiante por su legajo en un curso especifico
     * @param lu legajo del estudiante
     * @param cursoId Id del curso
     * @return estudiante con el legajo especificado
     */
    public Estudiante findByLuAndCursoId(String lu, String cursoId);

    /**
     * Verifica si existe un estudiante con el legajo especificado en un curso
     * @param lu legajo del estudiante
     * @param cursoId Id del curso
     * @return true si existe un estudiante con el legajo especificado en el curso especificado, false si no existe
     */
    public boolean existsByLuAndCursoId(String lu, String cursoId);

    /**
     * Obtiene los estudiantes que están inscriptos en un curso
     * @param id Id del curso
     * @return Lista de estudiantes inscriptos en el curso
     */
    List<Estudiante> findByCursoId(String id);

    /**
     * Obtiene un estudiante inscripto a un curso mediante el codigo de asistencia del curso
     * @param codigoAsistencia Código de asistencia del curso
     * @param lu Legajo del estudiante
     * @return Estudiante inscripto al curso
     */
    public Estudiante findByCursoCodigoAsistenciaAndLu(String codigoAsistencia, String lu);

    /**
     * Elimina los estudiantes inscriptos a un curso
     * @param cursoId Id del curso
     */
    public void deleteAllByCursoId(String cursoId);

    public void deleteByLuAndCursoId(String lu, String cursoId);
    public void deleteAllByLuIn(List<String> lu);

}
