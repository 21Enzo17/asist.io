package asist.io.service;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPatchDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.entity.Curso;
import asist.io.exception.ModelException;
import asist.io.exception.filters.HttpException;

import java.util.List;

public interface ICursoService {
    /**
     * Registra un curso en la base de datos
     * @param curso Curso a registrar
     * @return Curso registrado
     * @throws HttpException Si el curso es nulo
     */
    public CursoGetDTO registrarCurso(CursoPostDTO curso) throws HttpException;

    /**
     * Elimina un curso de la base de datos
     * @param id Id del curso a eliminar
     * @return true si el curso fue eliminado, false si no existe el curso
     * @throws HttpException Si el id es nulo o vacío
     */
    public boolean eliminarCurso(String id) throws HttpException;

    /**
     * Actualiza un curso en la base de datos
     * @param curso curso con datos actualizados
     * @return Curso actualizado
     * @throws HttpException Si el curso es nulo, el id es nulo o vacío o el curso no existe
     */
    public CursoGetDTO actualizarCurso(CursoPatchDTO curso) throws HttpException;

    /**
     * Obtiene un curso por su id
     * @param id Id del curso
     * @return Curso si existe, null si no existe
     * @throws HttpException Si el id es nulo o vacío
     */
    public CursoGetDTO obtenerCursoPorId(String id) throws HttpException;

    /**
     * Obtiene un curso ENTITY por su id
     * @param id Id del curso 
     * @return Curso si existe, null si no existe
     * @throws ModelException
     */
    public Curso obtenerCursoEntityPorId(String id);

    /**
     * Obtiene un curso por su código de asistencia
     * @param codigoAsistencia Código de asistencia del curso
     * @return Curso si existe en formato CursoGetDTO
     * @throws HttpException Si el código de asistencia es nulo o vacío, o el curso no existe
     */
    public CursoGetDTO obtenerCursoPorCodigoAsistencia(String codigoAsistencia) throws HttpException;


    /**
     * Obtiene los cursos que pertenecen a un usuario
     * @param id Id del usuario
     * @return Lista de cursos que pertenecen al usuario
     * @throws HttpException Si el id es nulo o vacío
     */
    public List<CursoGetDTO> obtenerCursosPorIdUsuario(String id) throws HttpException;

    /**
     * Obtiene los cursos según una palabra clave que coincida con el nombre
     * @param termino Palabra clave para buscar cursos
     * @param usuarioId Id del usuario
     * @return Lista de cursos que contienen la palabra clave
     * @throws HttpException Si el término es nulo o vacío
     */
    public List<CursoGetDTO> obtenerCursosPorTerminoYUsuario(String termino,String usuarioId) throws HttpException;

    /**
     * Encuentra un curso por su código de asistencia
     * @param codigoAsistencia Código de asistencia del curso
     * @return Entidad Curso si existe
     * @throws HttpException Si el código de asistencia es nulo o vacío
     */
    public Curso obtenerCursoEntityPorCodigoAsistencia(String codigoAsistencia);


    /**
     * Determina si un curso existe por su id
     * @param id Id del curso
     * No hace nada en caso de existir, en caso de no hacerlo lanza una excepcion ModelException
     */
    public void existePorId(String id);

    /**
     * Genera un código de asistencia único para un curso
     * @throws ModelException Si no se pudo generar el código de asistencia
     * debido a multiples intentos fallidos
     */
    public String generarCodigoAsistencia() throws HttpException;

}
