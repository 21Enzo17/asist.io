package asist.io.repository;


import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asist.io.entity.Asistencia;

import java.util.List;
import asist.io.entity.Estudiante;


@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, String>  {

    @Query("SELECT a FROM Asistencia a WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin AND a.curso.id = :curso_id")
    public List<Asistencia> findyByPeriodoAndCursoId(LocalDate fechaInicio, LocalDate fechaFin, String curso_id);

    public List<Asistencia> findByCursoId(String id);

    public List<Asistencia> findByEstudianteLuAndCursoId(String Lu, String curso_id);

    public Boolean existsByFechaAndEstudiante(LocalDate fecha, Estudiante estudiante);

    

}
