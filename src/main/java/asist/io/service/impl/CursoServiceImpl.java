package asist.io.service.impl;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPatchDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.exception.ModelException;
import asist.io.mapper.CursoMapper;
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
    public CursoGetDTO registrarCurso(CursoPostDTO curso) throws ModelException {
        if (curso == null) throw new ModelException("El curso no puede ser nulo");

        if (curso.getCodigoAsistencia() != null && cursoRepository.existsByCodigoAsistencia(curso.getCodigoAsistencia())) throw new ModelException("El código de asistencia " + curso.getCodigoAsistencia() + " ya esta en uso");

        CursoGetDTO cursoRegistrado = CursoMapper.toGetDTO(cursoRepository.save(CursoMapper.toEntity(curso)));
        return cursoRegistrado;
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
    public CursoGetDTO actualizarCurso(CursoPatchDTO curso) throws ModelException {
        if (curso == null) throw new ModelException("El curso no puede ser nulo");

        if (curso.getId() == null || curso.getId().isBlank() || curso.getId().isEmpty()) throw new ModelException("El id no puede ser nulo ni vacío");

        if (curso.getId() != null && !cursoRepository.existsById(curso.getId())) throw new ModelException("El curso con id " + curso.getId() + " no existe");

        if (curso.getCodigoAsistencia() != null && (obtenerCursoPorCodigoAsistencia(curso.getCodigoAsistencia()) != null && !obtenerCursoPorCodigoAsistencia(curso.getCodigoAsistencia()).getId().equals(curso.getId())) ) throw new ModelException("El código de asistencia, " + curso.getCodigoAsistencia() + ",ya esta en uso");

        CursoGetDTO cursoActualizado = CursoMapper.toGetDTO(cursoRepository.save(CursoMapper.toEntity(curso)));
        return cursoActualizado;
    }

    /**
     * Obtiene un curso por su id
     * @param id Id del curso
     * @return Curso si existe, null si no existe
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public CursoGetDTO obtenerCursoPorId(String id) throws ModelException {
        if (id == null || id.isBlank() || id.isEmpty()) throw new ModelException("El id no puede ser nulo ni vacío");

        if (!cursoRepository.existsById(id)) return null;

        CursoGetDTO cursoEncontrado = CursoMapper.toGetDTO(cursoRepository.findById(id).get());
        return cursoEncontrado;
    }

    /**
     * Obtiene un curso por su código de asistencia
     * @param codigoAsistencia Código de asistencia del curso
     * @return Curso si existe
     * @throws ModelException Si el código de asistencia es nulo o vacío, o el curso no existe
     */
    @Override
    public CursoGetDTO obtenerCursoPorCodigoAsistencia(String codigoAsistencia) throws ModelException {
        if (codigoAsistencia == null || codigoAsistencia.isBlank() || codigoAsistencia.isEmpty()) throw new ModelException("El código de asistencia no puede ser nulo ni vacío");

        if (!cursoRepository.existsByCodigoAsistencia(codigoAsistencia)) return null;

        CursoGetDTO cursoEncontrado = CursoMapper.toGetDTO(cursoRepository.findByCodigoAsistencia(codigoAsistencia));
        return cursoEncontrado;
    }

    /**
     * Obtiene un curso por el id de un usuario
     * @param id Id del usuario
     * @return Curso si existe, null si no existe
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public CursoGetDTO obtenerCursoPorIdUsuario(String id) throws ModelException {
        throw new ModelException("Not implemented yet");
    }
}
