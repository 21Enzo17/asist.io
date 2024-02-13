package asist.io.repository;

import asist.io.entity.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository extends JpaRepository<Curso, String> {
    /**
     * Busca un curso por su código de asistencia
     * @param codigoAsistencia Código de asistencia del curso
     * @return Curso
     */
    @Query("SELECT c FROM Curso c WHERE c.codigoAsistencia = ?1")
    public Curso findByCodigoAsistencia(String codigoAsistencia);

    /**
     * Verifica si existe un curso con el código de asistencia dado
     * @param codigoAsistencia Código de asistencia a verificar
     * @return true si existe, false si no
     */
    @Query("SELECT c FROM Curso c WHERE c.codigoAsistencia = ?1")
    public boolean existsByCodigoAsistencia(String codigoAsistencia);
}
