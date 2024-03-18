package asist.io.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import asist.io.entity.Horario;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, String>{
    
    /**
     * Busca los horarios de un curso
     * @param cursoId Id del curso
     * @return Lista de horarios
     */
    public List<Horario> findByCursoId(String cursoId);

    /**
     * Busca si hay horarios en un dia y hora especifica que se superponen con un horario
     * @param cursoId Id del curso
     * @param dia Dia de la semana
     * @param entrada Hora de entrada
     * @param salida Hora de salida
     * @return Lista de horarios que se superponen
     */
    @Query("SELECT h FROM Horario h WHERE h.curso.id = :cursoId AND h.dia = :dia AND ((h.entrada <= :entrada AND h.salida > :entrada) OR (h.entrada < :salida AND h.salida >= :salida) OR (h.entrada >= :entrada AND h.salida <= :salida))")
    public List<Horario> findOverlappingHorarios(String cursoId, DayOfWeek dia, LocalTime entrada,  LocalTime salida);

    /**
     * Busca un horario que coincida por dia y hora
     * @param codigoAsistencia Codigo de asistencia del curso
     * @param dia Dia de la semana
     * @param hora Hora a buscar
     * @return Horario que coincide
     */
    @Query("SELECT h FROM Horario h WHERE h.curso.codigoAsistencia = :codigoAsistencia AND h.dia = :dia AND h.entrada <= :hora AND h.salida > :hora")
    public Horario findHorarioContainingTime(String codigoAsistencia, DayOfWeek dia, LocalTime hora);

    /**
     * Busca los horarios de un curso en un dia especifico
     * @param cursoId Id del curso
     * @param dia Dia de la semana
     * @return Lista de horarios
     */
    public List<Horario> findByCursoIdAndDia(String cursoId, DayOfWeek dia);
}
