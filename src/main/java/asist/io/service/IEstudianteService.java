package asist.io.service;

import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
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
     * Elimina un estudiante en la base de datos
     * @param id Id del estudiante a eliminar
     * @return true si se eliminó el estudiante, false si no existe el estudiante
     * @throws ModelException Si el id es nulo o vacío
     */
    public boolean eliminarEstudiante(String id) throws ModelException;

    /**
     * Obtiene un estudiante por su lu
     * @param lu Lu del estudiante a obtener
     * @return Estudiante si existe, null si no existe
     * @throws ModelException Si el lu es nulo o vacío
     */
    public EstudianteGetDTO obtenerEstudiantePorLu(String lu) throws ModelException;

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
}
