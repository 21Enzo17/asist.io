package asist.io.repository;


import org.springframework.stereotype.Repository;


import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asist.io.entity.Asistencia;

import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, String>  {

    @Query("SELECT a FROM Asistencia a WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin AND a.curso.id = :curso_id")
    public List<Asistencia> findyByPeriodoAndCursoId(Date fechaInicio, Date fechaFin, String curso_id);

    public List<Asistencia> findByCursoId(String id);

    public List<Asistencia> findByAlumnoIdAndCursoId(String id, String curso_id);


}
