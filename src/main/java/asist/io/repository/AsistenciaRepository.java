package asist.io.repository;


import org.springframework.stereotype.Repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import asist.io.entity.Asistencia;

import java.util.List;
import asist.io.entity.Estudiante;
import java.time.LocalDateTime;



@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, String>  {

    /**
     * Este metodo busca una asistencia por periodo y curso
     * @param fechaInicio fecha de inicio del periodo
     * @param fechaFin fecha de fin del periodo
     * @param curso_id id del curso
     * @return Lista de asistencias 
     */
    @Query("SELECT a FROM Asistencia a WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin AND a.curso.id = :curso_id")
    public List<Asistencia> findyByPeriodoAndCursoId(LocalDateTime fechaInicio, LocalDateTime fechaFin, String curso_id);

    /**
     * Este metodo busca una asistencia por curso
     * @param id id del curso
     * @return Lista de asistencias
     */
    public List<Asistencia> findByCursoId(String id);

    /**
     * Este metodo busca una asistencia por estudiante y curso
     * @param Lu
     * @param curso_id
     * @return Lista de asistencias
     */
    public List<Asistencia> findByEstudianteLuAndCursoId(String Lu, String curso_id);
    
    /**
     * Este metodo busca una asistencia por fecha y estudiante
     * @param fecha
     * @param estudiante
     * @return true si existe, false si no existe
     */
    public Boolean existsByFechaAndEstudiante(LocalDate fecha, Estudiante estudiante);

    /**
     * Este metodo busca una asistencia por fecha, estudiante y horario
     * @param inicioDelDia
     * @param finDelDia
     * @param lu
     * @param horarioId
     * @return Asistencia si existe, null si no existe
     */
    @Query("SELECT a FROM Asistencia a WHERE a.fecha >= :inicioDelDia AND a.fecha < :finDelDia AND a.horario.id = :horarioId AND a.estudiante.lu = :lu")
    public Asistencia findByFechaAndEstudianteLuAndHorarioId(LocalDateTime inicioDelDia, LocalDateTime finDelDia, String lu, String horarioId);


}
