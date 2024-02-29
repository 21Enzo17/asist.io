package asist.io.service.impl;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPatchDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.entity.Usuario;
import asist.io.exception.ModelException;
import asist.io.mapper.CursoMapper;
import asist.io.repository.CursoRepository;
import asist.io.repository.UsuarioRepository;
import asist.io.service.ICursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoServiceImpl implements ICursoService {
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;


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

        if (!usuarioRepository.existsById(curso.getIdUsuario())) throw new ModelException("El usuario con id " + curso.getIdUsuario() + " no existe");

        Usuario usuario = usuarioRepository.findById(curso.getIdUsuario()).get();
        CursoGetDTO cursoRegistrado = CursoMapper.toGetDTO(cursoRepository.save(CursoMapper.toEntity(curso, usuario)));
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
     * Obtiene los cursos que pertenecen a un usuario
     * @param id Id del usuario
     * @return Lista de cursos que pertenecen al usuario
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public List<CursoGetDTO> obtenerCursosPorIdUsuario(String id) throws ModelException {
        if (id == null || id.isBlank() || id.isEmpty()) throw new ModelException("El id no puede ser nulo ni vacío");

        if (cursoRepository.findByUsuarioId(id).isEmpty()) return null;

        List<CursoGetDTO> cursosEncontrados = CursoMapper.toGetDTO(cursoRepository.findByUsuarioId(id));
        if (cursosEncontrados.isEmpty()) throw new ModelException("El usuario con id " + id + " no tiene cursos registrados");
        return cursosEncontrados;
    }

    /**
     * Obtiene los cursos según una palabra clave que coincida con el nombre
     * @param termino Palabra clave para buscar cursos
     * @return Lista de cursos que contienen la palabra clave
     */
    public List<CursoGetDTO> obtenerCursosPorTermino(String termino) throws ModelException {
        if (termino == null || termino.isBlank() || termino.isEmpty()) return List.of();

        List<CursoGetDTO> cursosObtenidos = CursoMapper.toGetDTO(cursoRepository.findByNombreContaining(termino));
        if (cursosObtenidos.isEmpty()) throw new ModelException("No se encontraron cursos con el término " + termino);
        return cursosObtenidos;
    }
}
