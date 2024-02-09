package asist.io.service;

import asist.io.entity.Estudiante;
import asist.io.exception.ModelException;

public interface IEstudianteService {
    /**
     * Registra un estudiante en la base de datos
     * @param estudiante Estudiante a registrar
     * @return Estudiante registrado
     * @throws ModelException Si el estudiante es nulo o si el lu del estudiante ya está registrado
     */
    public Estudiante registrarEstudiante(Estudiante estudiante) throws ModelException;

    /**
     * Elimina un estudiante en la base de datos
     * @param id Id del estudiante a eliminar
     * @return true si se eliminó el estudiante, false si no existe el estudiante
     * @throws ModelException
     */
    public boolean eliminarEstudiante(String id) throws ModelException;

    /**
     * Obtiene un estudiante por su lu
     * @param lu Lu del estudiante a obtener
     * @return Estudiante si existe, null si no existe
     * @throws ModelException Si el lu es nulo o vacío
     */
    public Estudiante obtenerEstudiantePorLu(String lu) throws ModelException;
}
