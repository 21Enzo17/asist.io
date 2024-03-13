package asist.io.repository;

import asist.io.entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, String> {
    @Query("SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM Inscripcion i WHERE i.estudiante.lu = :estudianteLu AND i.curso.codigoAsistencia = :codigoAsistencia")
    public Boolean existsByEstudianteLuAndCursoCodigoAsistencia(String estudianteLu, String codigoAsistencia);
}
