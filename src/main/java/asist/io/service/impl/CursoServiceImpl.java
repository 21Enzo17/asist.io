package asist.io.service.impl;

import asist.io.entity.Curso;
import asist.io.exception.ModelException;
import asist.io.repository.CursoRepository;
import asist.io.service.ICursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CursoServiceImpl implements ICursoService {
    @Autowired
    private CursoRepository cursoRepository;


    /**
     * Registra un curso en la base de datos
     * @param curso Curso a registrar
     * @return Curso registrado
     * @throws ModelException Si el curso es nulo
     */
    @Override
    public Curso registrarCurso(Curso curso) throws ModelException {
        if (curso == null) throw new ModelException("El curso no puede ser nulo");

        if (curso.getId() != null && cursoRepository.existsById(curso.getId())) throw new ModelException("El id " + curso.getId() + " del curso ya existe");

        if (curso.getCodigoAsistencia() != null && cursoRepository.existsByCodigoAsistencia(curso.getCodigoAsistencia())) throw new ModelException("El código de asistencia " + curso.getCodigoAsistencia() + " ya esta en uso");

        return cursoRepository.save(curso);
    }

    /**
     * Elimina un curso de la base de datos
     * @param id Id del curso a eliminar
     * @return true si el curso fue eliminado, false si no existe el curso
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public boolean eliminarCurso(String id) throws ModelException {
        if (id == null || id.isBlank() || id.isEmpty()) throw new ModelException("El id no puede ser nulo ni vacío");

        if (!cursoRepository.existsById(id)) return false;

        cursoRepository.deleteById(id);
        return true;
    }

    /**
     * Actualiza un curso en la base de datos
     * @param curso curso con datos actualizados
     * @return Curso actualizado
     * @throws ModelException Si el curso es nulo, el id es nulo o vacío o el curso no existe
     */
    @Override
    public Curso actualizarCurso(Curso curso) throws ModelException {
        if (curso == null) throw new ModelException("El curso no puede ser nulo");

        if (curso.getId() == null || curso.getId().isBlank() || curso.getId().isEmpty()) throw new ModelException("El id no puede ser nulo ni vacío");

        if (!cursoRepository.existsById(curso.getId())) throw new ModelException("El curso con id " + curso.getId() + " no existe");

        if (curso.getCodigoAsistencia() != null && cursoRepository.existsByCodigoAsistencia(curso.getCodigoAsistencia())) throw new ModelException("El código de asistencia, " + curso.getCodigoAsistencia() + ",ya esta en uso");

        return cursoRepository.save(curso);
    }

    /**
     * Obtiene un curso por su id
     * @param id Id del curso
     * @return Curso si existe, null si no existe
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public Curso obtenerCursoPorId(String id) throws ModelException {
        if (id == null || id.isBlank() || id.isEmpty()) throw new ModelException("El id no puede ser nulo ni vacío");

        return cursoRepository.findById(id).orElse(null);
    }

    /**
     * Obtiene un curso por su código de asistencia
     * @param codigoAsistencia Código de asistencia del curso
     * @return Curso si existe
     * @throws ModelException Si el código de asistencia es nulo o vacío, o el curso no existe
     */
    @Override
    public Curso ObtenerCursoPorCodigoAsistencia(String codigoAsistencia) throws ModelException {
        if (codigoAsistencia == null || codigoAsistencia.isBlank() || codigoAsistencia.isEmpty()) throw new ModelException("El código de asistencia no puede ser nulo ni vacío");

        if (!cursoRepository.existsByCodigoAsistencia(codigoAsistencia)) throw new ModelException("El curso con el código de asistencia " + codigoAsistencia + " no existe");

        return cursoRepository.findByCodigoAsistencia(codigoAsistencia);
    }

    /**
     * Obtiene un curso por el id de un usuario
     * @param id Id del usuario
     * @return Curso si existe, null si no existe
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public Curso obtenerCursoPorIdUsuario(String id) throws ModelException {
        throw new ModelException("Not implemented yet");
    }
}
