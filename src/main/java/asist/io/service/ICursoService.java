package asist.io.service;

import asist.io.entity.Curso;
import asist.io.exception.ModelException;

public interface ICursoService {
    /**
     * Registra un curso en la base de datos
     * @param curso Curso a registrar
     * @return Curso registrado
     * @throws ModelException Si el curso es nulo
     */
    public Curso registrarCurso(Curso curso) throws ModelException;

    /**
     * Elimina un curso de la base de datos
     * @param id Id del curso a eliminar
     * @return true si el curso fue eliminado, false si no existe el curso
     * @throws ModelException Si el id es nulo o vacío
     */
    public boolean eliminarCurso(String id) throws ModelException;

    /**
     * Actualiza un curso en la base de datos
     * @param curso curso con datos actualizados
     * @return Curso actualizado
     * @throws ModelException Si el curso es nulo, el id es nulo o vacío o el curso no existe
     */
    public Curso actualizarCurso(Curso curso) throws ModelException;

    /**
     * Obtiene un curso por su id
     * @param id Id del curso
     * @return Curso si existe, null si no existe
     * @throws ModelException Si el id es nulo o vacío
     */
    public Curso obtenerCursoPorId(String id) throws ModelException;

    /**
     * Obtiene un curso por su código de asistencia
     * @param codigoAsistencia Código de asistencia del curso
     * @return Curso si existe
     * @throws ModelException Si el código de asistencia es nulo o vacío, o el curso no existe
     */
    public Curso ObtenerCursoPorCodigoAsistencia(String codigoAsistencia) throws ModelException;

    /**
     * Obtiene un curso por el id de un usuario
     * @param id Id del usuario
     * @return Curso si existe, null si no existe
     * @throws ModelException Si el id es nulo o vacío
     */
    public Curso obtenerCursoPorIdUsuario(String id) throws ModelException;
}