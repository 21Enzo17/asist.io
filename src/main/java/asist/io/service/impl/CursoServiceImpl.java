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
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoServiceImpl implements ICursoService {
    private static final Logger logger = Logger.getLogger(CursoServiceImpl.class);
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
        if (curso == null) {
            logger.error("Error al registrar el curso: El curso no puede ser nulo");
            throw new ModelException("El curso no puede ser nulo");
        }

        logger.info("Registrando curso: " + curso.getNombre());

        if (curso.getCodigoAsistencia() != null && cursoRepository.existsByCodigoAsistencia(curso.getCodigoAsistencia())) {
            logger.error("Error al registrar el curso: El código de asistencia, " + curso.getCodigoAsistencia() + ", ya esta en uso");
            throw new ModelException("El código de asistencia " + curso.getCodigoAsistencia() + " ya esta en uso");
        }

        if (!usuarioRepository.existsById(curso.getIdUsuario())) {
            logger.error("Error al registrar el curso: El usuario con id " + curso.getIdUsuario() + " no existe");
            throw new ModelException("El usuario con id " + curso.getIdUsuario() + " no existe");
        }

        Usuario usuario = usuarioRepository.findById(curso.getIdUsuario()).get();
        CursoGetDTO cursoRegistrado = CursoMapper.toGetDTO(cursoRepository.save(CursoMapper.toEntity(curso, usuario)));
        logger.info("Curso registrado con éxito, id: " + cursoRegistrado.getId());
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
        if (id == null || id.isBlank() || id.isEmpty()) {
            logger.error("Error al eliminar el curso: El id no puede ser nulo ni vacío");
            throw new ModelException("El id no puede ser nulo ni vacío");
        }

        logger.info("Intentando eliminar el curso con id: " + id);

        if (!cursoRepository.existsById(id)) {
            logger.error("Error al eliminar el curso: El curso con id " + id + " no existe");
            return false;
        }

        cursoRepository.deleteById(id);
        logger.info("Curso eliminado con éxito, id: " + id);
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
        if (curso == null) {
            logger.error("Error al actualizar el curso: El curso no puede ser nulo");
            throw new ModelException("El curso no puede ser nulo");
        }

        if (curso.getId() == null || curso.getId().isBlank() || curso.getId().isEmpty()) {
            logger.error("Error al actualizar el curso: El id no puede ser nulo ni vacío");
            throw new ModelException("El id no puede ser nulo ni vacío");
        }

        logger.info("Actualizando curso con id: " + curso.getId());

        if (curso.getId() != null && !cursoRepository.existsById(curso.getId())) {
            logger.error("Error al actualizar el curso: El curso con id " + curso.getId() + " no existe");
            throw new ModelException("El curso con id " + curso.getId() + " no existe");
        }

        if (curso.getCodigoAsistencia() != null && (obtenerCursoPorCodigoAsistencia(curso.getCodigoAsistencia()) != null && !obtenerCursoPorCodigoAsistencia(curso.getCodigoAsistencia()).getId().equals(curso.getId())) ) {
            logger.error("Error al actualizar el curso: El código de asistencia, " + curso.getCodigoAsistencia() + ", ya esta en uso");
            throw new ModelException("El código de asistencia " + curso.getCodigoAsistencia() + " ya esta en uso");
        }

        CursoGetDTO cursoActualizado = CursoMapper.toGetDTO(cursoRepository.save(CursoMapper.toEntity(curso)));
        logger.info("Curso actualizado con éxito, id: " + cursoActualizado.getId());
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
        if (id == null || id.isBlank() || id.isEmpty()) {
            logger.error("Error al buscar el curso: El id no puede ser nulo ni vacío");
            throw new ModelException("El id no puede ser nulo ni vacío");
        }

        logger.info("Buscando curso con id: " + id);

        if (!cursoRepository.existsById(id)) {
            logger.error("Error al buscar el curso: El curso con id " + id + " no existe");
            return null;
        }

        CursoGetDTO cursoEncontrado = CursoMapper.toGetDTO(cursoRepository.findById(id).get());
        logger.info("Curso encontrado con éxito, id: " + cursoEncontrado.getId());
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
        if (codigoAsistencia == null || codigoAsistencia.isBlank() || codigoAsistencia.isEmpty()) {
            logger.error("Error al buscar el curso por código de asistencia: El código de asistencia no puede ser nulo ni vacío");
            throw new ModelException("El código de asistencia no puede ser nulo ni vacío");
        }

        logger.info("Buscando curso con código de asistencia: " + codigoAsistencia);

        if (!cursoRepository.existsByCodigoAsistencia(codigoAsistencia)) {
            logger.error("Error al buscar el curso por código de asistencia: El curso con código de asistencia " + codigoAsistencia + " no existe");
            throw new ModelException("El curso con código de asistencia " + codigoAsistencia + " no existe");
        }

        CursoGetDTO cursoEncontrado = CursoMapper.toGetDTO(cursoRepository.findByCodigoAsistencia(codigoAsistencia));
        logger.info("Curso con el código de asistencia " + codigoAsistencia + " encontrado con éxito, id: " + cursoEncontrado.getId());
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
        if (id == null || id.isBlank() || id.isEmpty()) {
            logger.error("Error al buscar los cursos: El id del usuario no puede ser nulo ni vacío");
            throw new ModelException("El id del usuario no puede ser nulo ni vacío");
        }

        if (cursoRepository.findByUsuarioId(id).isEmpty()) {
            logger.error("Error al buscar los cursos: El usuario con id " + id + " no tiene cursos registrados");
            throw new ModelException("El usuario con id " + id + " no tiene cursos registrados");
        }

        List<CursoGetDTO> cursosEncontrados = CursoMapper.toGetDTO(cursoRepository.findByUsuarioId(id));

        if (cursosEncontrados.isEmpty()) {
            logger.error("Error al buscar los cursos: El usuario con id " + id + " no tiene cursos registrados");
            throw new ModelException("El usuario con id " + id + " no tiene cursos registrados");
        }

        logger.info("Cursos encontrados con éxito para el usuario con id: " + id);
        return cursosEncontrados;
    }

    /**
     * Obtiene los cursos según una palabra clave que coincida con el nombre
     * @param termino Palabra clave para buscar cursos
     * @return Lista de cursos que contienen la palabra clave
     */
    @Override
    public List<CursoGetDTO> obtenerCursosPorTermino(String termino) throws ModelException {
        logger.info("Buscando cursos con el término: " + termino);

        if (termino == null || termino.isBlank() || termino.isEmpty()) {
            logger.error("Error al buscar los cursos: El término no puede ser nulo ni vacío");
            return List.of();
        }

        List<CursoGetDTO> cursosObtenidos = CursoMapper.toGetDTO(cursoRepository.findByNombreContaining(termino));
        if (cursosObtenidos.isEmpty()) {
            logger.error("Error al buscar los cursos: No se encontraron cursos con el término " + termino);
            throw new ModelException("No se encontraron cursos con el término " + termino);
        }

        logger.info("Cursos encontrados con éxito con el término: " + termino);
        return cursosObtenidos;
    }

    /**
     * Genera un código de asistencia único para un curso
     */
    @Override
    public String generarCodigoAsistencia() throws ModelException {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int longitud = 6;
        String codigo;
        int intentosGeneracionRestantes = 99;

        do {
            codigo = generarCodigo(longitud, chars);
            intentosGeneracionRestantes -= 1;

            if (intentosGeneracionRestantes == 0) {
                logger.error("Error al generar el código de asistencia: Multiples intentos de generación");
                throw new ModelException("No se pudo generar el código de asistencia");
            }
        } while (cursoRepository.existsByCodigoAsistencia(codigo));

        return codigo;
    }

    /**
     * Genera un código aleatorio
     * @param longitud longitud del código
     * @param chars set de carácteres que se usaran en el código
     * @return código generado
     */
    private String generarCodigo(int longitud, String chars) {
        String codigo = "";
        for (int i = 0; i < longitud; i+=1) {
            int index = (int) (Math.random() * chars.length());
            codigo += chars.charAt(index);
        }
        return codigo;
    }
}
