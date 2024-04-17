package asist.io.service;

import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.entity.Estudiante;
import asist.io.exception.ModelException;

import java.util.List;

public interface IEstudianteService {
    /**
     * Registra un estudiante en la base de datos
     * @param estudiante Estudiante a registrar
     * @return Estudiante registrado
     * @throws ModelException Si el estudiante es nulo o si el lu del estudiante ya está registrado
     */
    public EstudianteGetDTO registrarEstudiante(EstudiantePostDTO estudiante) throws ModelException;

    /**
     * Registra una lista de estudiantes en la base de datos
     * @param estudiantes Lista de estudiantes a registrar
     * @return Lista de estudiantes registrados
     * @throws ModelException Si la lista de estudiantes es nula o vacía
     */
    public List<EstudianteGetDTO> registrarEstudiantes(List<EstudiantePostDTO> estudiantes) throws ModelException;

    /**
     * Elimina un estudiante en la base de datos
     * @param id Id del estudiante a eliminar
     * @return true si se eliminó el estudiante, false si no existe el estudiante
     * @throws ModelException Si el id es nulo o vacío
     */
    public boolean eliminarEstudiante(String id) throws ModelException;

    /**
     * Elimina una lista de estudiantes en la base de datos
     * @param ids Lista de ids de estudiantes a eliminar
     * @return true si se eliminaron los estudiantes, false si no existe alguno de los estudiantes
     * @throws ModelException Si la lista de ids es nula o vacía
     */
    public boolean eliminarEstudiantes(List<String> ids) throws ModelException;

    /**
     * Obtiene un estudiante por su lu y curso id
     * @param lu Lu del estudiante a obtener
     * @param cursoId Id del curso
     * @return Estudiante si existe, null si no existe
     * @throws ModelException Si el lu es nulo o vacío
     */
    public EstudianteGetDTO obtenerEstudiantePorLuYCursoId(String lu, String cursoId) throws ModelException;

    /**
     * Obtiene un estudiante por id
     * @param id id del estudiante
     * @return Estudiante si existe, null si no existe
     */
    public EstudianteGetDTO obtenerEstudiantePorId(String id) throws ModelException;

    /**
     * Obtiene los estudiantes que están inscriptos en un curso
     * @param id Id del curso
     * @return Lista de estudiantes inscriptos en el curso
     * @throws ModelException Si el id del curso es nulo o vacío
     */
    public List<EstudianteGetDTO> obtenerEstudiantesPorIdCurso(String id) throws ModelException;

    /**
     * Obtiene un estudiante por su lu y curso Id
     * @param lu Lu del estudiante
     * @param cursoId Id del curso
     * @return Estudiante Entity 
     * @throws ModelException Si el estudiante no existe
     */
    public Estudiante obtenerEstudianteEntityPorLuYCursoId(String lu,String cursoId);

    /**
     * Obtiene un estudiante inscripto a un curso
     * @param codigoAsistencia Código de asistencia del curso
     * @param lu Legajo del estudiante
     * @return Estudiante inscripto al curso
     * @throws ModelException Si el id del curso o el lu del estudiante es nulo o vacío
     * @throws ModelException Si el estudiante no está inscripto al curso
     */
    public Estudiante obtenerEstudianteEntityPorCodigoAsistenciaYLu(String codigoAsistencia, String lu);
}
