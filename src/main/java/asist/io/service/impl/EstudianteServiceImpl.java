package asist.io.service.impl;

import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.entity.Estudiante;
import asist.io.exception.ModelException;
import asist.io.mapper.EstudianteMapper;
import asist.io.repository.CursoRepository;
import asist.io.repository.EstudianteRepository;
import asist.io.service.IEstudianteService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@SuppressWarnings("null")
public class EstudianteServiceImpl implements IEstudianteService {

    private final static Logger logger = Logger.getLogger(EstudianteServiceImpl.class);

    @Autowired
    private EstudianteRepository estudianteRepository;
    @Autowired
    private CursoRepository cursoRepository;

    /**
     * Registra un estudiante en la base de datos
     * @param estudiante Estudiante a registrar
     * @return Estudiante registrado
     * @throws ModelException Si el estudiante es nulo o si el lu del estudiante ya está registrado
     */
    @Override
    public EstudianteGetDTO registrarEstudiante(EstudiantePostDTO estudiante) throws ModelException {
        if (estudiante == null) {
            logger.error("Error al registrar el estudiante: El estudiante no puede ser nulo");
            throw new ModelException("El estudiante no puede ser nulo");
        }

        if (estudianteRepository.existsByLuAndCursoId(estudiante.getLu(), estudiante.getCursoId())) {
            logger.error("Error al registrar el estudiante: El lu, " + estudiante.getLu() + ", ya esta en uso en este curso");
            throw new ModelException("El lu " + estudiante.getLu() + " ya esta en uso en este curso");
        }

        if(cursoRepository.existsById(estudiante.getCursoId()) == false){
            logger.error("Error al registrar el estudiante: El curso con id " + estudiante.getCursoId() + " no existe");
            throw new ModelException("El curso con id " + estudiante.getCursoId() + " no existe");
        }
        
        EstudianteGetDTO estudianteRegistrado = EstudianteMapper.toGetDTO(estudianteRepository.save(EstudianteMapper.toEntity(estudiante,cursoRepository.findById(estudiante.getCursoId()).get())));
        logger.info("Estudiante registrado con éxito, id: " + estudianteRegistrado.getId());
        return estudianteRegistrado;
    }

    /**
     * Registra una lista de estudiantes en la base de datos
     * @param estudiantes Lista de estudiantes a registrar
     * @return Lista de estudiantes registrados
     * @throws ModelException Si la lista de estudiantes es nula o vacía
     */
    public List<EstudianteGetDTO> registrarEstudiantes(List<EstudiantePostDTO> estudiantes) throws ModelException {
        if (estudiantes == null || estudiantes.isEmpty()) {
            logger.error("Error al registrar los estudiantes: La lista de estudiantes no puede ser nula ni vacía");
            throw new ModelException("La lista de estudiantes no puede ser nula ni vacía");
        }
        if(!cursoRepository.existsById(estudiantes.get(0).getCursoId())){
            logger.error("Error al registrar los estudiantes: El curso con id " + estudiantes.get(0).getCursoId() + " no existe");
            throw new ModelException("El curso con id " + estudiantes.get(0).getCursoId() + " no existe");
        }

        List<EstudianteGetDTO> estudiantesRegistrados = EstudianteMapper.toGetDTO(estudianteRepository.saveAll(EstudianteMapper.toEntity(estudiantes,cursoRepository.findById(estudiantes.get(0).getCursoId()).get())));
        logger.info("Estudiantes registrados con éxito");
        return estudiantesRegistrados;
    }

    /**
     * Elimina un estudiante en la base de datos
     * @param id Id del estudiante a eliminar
     * @return true si se eliminó el estudiante, false si no existe el estudiante
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public boolean eliminarEstudiante(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            logger.error("Error al eliminar el estudiante: El id no puede ser nulo ni vacío");
            throw new ModelException("El id no puede ser nulo ni vacío");
        }

        if (!estudianteRepository.existsById(id)) {
            logger.error("Error al eliminar el estudiante: El estudiante con id " + id + " no existe");
            return false;
        }

        estudianteRepository.deleteById(id);
        logger.info("Estudiante eliminado con éxito, id: " + id);
        return true;
    }

    /**
     * Elimina una lista de estudiantes en la base de datos
     * @param ids Lista de ids de estudiantes a eliminar
     * @return true si se eliminaron los estudiantes, false si no existe alguno de los estudiantes
     * @throws ModelException Si la lista de ids es nula o vacía
     */
    public boolean eliminarEstudiantes(List<String> ids) throws ModelException {
        if (ids == null || ids.isEmpty()) {
            logger.error("Error al eliminar los estudiantes: La lista de ids no puede ser nula ni vacía");
            throw new ModelException("La lista de ids no puede ser nula ni vacía");
        }


        for (String id : ids) {
            if (!estudianteRepository.existsById(id)) {
                logger.error("Error al eliminar los estudiantes: El estudiante con id " + id + " no existe");
                return false;
            }
        }

        estudianteRepository.deleteAllById(ids);
        logger.info("Estudiantes eliminados con éxito");
        return true;
    }

    /**
     * Obtiene un estudiante por su lu y curso id
     * @param lu Lu del estudiante a obtener
     * @param cursoId Id del curso
     * @return Estudiante si existe, null si no existe
     * @throws ModelException Si el lu es nulo o vacío
     */
    @Override
    public EstudianteGetDTO obtenerEstudiantePorLuYCursoId(String lu, String cursoId)throws ModelException {
        if (lu == null || lu.isEmpty() || lu.isBlank()) {
            logger.error("Error al obtener el estudiante: El lu no puede ser nulo ni vacío");
            throw new ModelException("El lu no puede ser nulo ni vacío");
        }

        if (!estudianteRepository.existsByLuAndCursoId(lu,cursoId)) {
            logger.error("Error al obtener el estudiante: El estudiante con el lu " + lu + " no existe");
            return null;
        }

        EstudianteGetDTO estudianteEncontrado = EstudianteMapper.toGetDTO(estudianteRepository.findByLuAndCursoId(lu, cursoId));
        logger.info("Estudiante encontrado con éxito, lu: " + lu);
        return estudianteEncontrado;
    }

    /**
     * Obtiene un estudiante por id
     * @param id id del estudiante
     * @return Estudiante si existe, null si no existe
     */
    @Override
    public EstudianteGetDTO obtenerEstudiantePorId(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            logger.error("Error al obtener el estudiante: El id no puede ser nulo ni vacío");
            throw new ModelException("El id no puede ser nulo ni vacío");
        }

        if (!estudianteRepository.existsById(id)) {
            logger.error("Error al obtener el estudiante: El estudiante con el id " + id + " no existe");
            throw new ModelException("El estudiante con el id " + id + " no existe");
        }

        EstudianteGetDTO estudianteEncontrado = EstudianteMapper.toGetDTO(estudianteRepository.findById(id).get());
        logger.info("Estudiante encontrado con éxito, id: " + id);
        return estudianteEncontrado;
    }

    /**
     * Obtiene los estudiantes que están inscriptos en un curso
     * @param id Id del curso
     * @return Lista de estudiantes inscriptos en el curso
     * @throws ModelException Si el id del curso es nulo o vacío
     */
    @Override
    public List<EstudianteGetDTO> obtenerEstudiantesPorIdCurso(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            logger.error("Error al obtener los estudiantes: El id del curso no puede ser nulo ni vacío");
            throw new ModelException("El id del curso no puede ser nulo ni vacío");
        }

        if (!cursoRepository.existsById(id)) {
            logger.error("Error al obtener los estudiantes: El curso con el id " + id + " no existe");
            throw new ModelException("El curso con el id " + id + " no existe");
        }

        List<EstudianteGetDTO> estudiantesEncontrados = EstudianteMapper.toGetDTO(estudianteRepository.findByCursoId(id));
        estudiantesEncontrados.sort(Comparator.comparing(EstudianteGetDTO::getNombre));
        logger.info("Estudiantes encontrados con éxito con cursoId: " + id);
        return estudiantesEncontrados;
    }

    /**
     * Obtiene un estudiante por su lu y curso Id
     * @param lu Lu del estudiante
     * @param cursoId Id del curso
     * @return Estudiante Entity 
     * @throws ModelException Si el estudiante no existe
     */
    @Override
    public Estudiante obtenerEstudianteEntityPorLuYCursoId(String lu,String cursoId) {
        Estudiante estudiante = estudianteRepository.findByLuAndCursoId(lu,cursoId);
        if(estudiante == null){
            logger.error("El estudiante con el lu " + lu + " no existe");
            throw new ModelException("El estudiante con el lu " + lu + " no existe");
        }
        logger.info("Estudiante encontrado con éxito, lu: " + lu);
        return estudiante;
    }

    /**
     * Obtiene un estudiante inscripto a un curso
     * @param codigoAsistencia Código de asistencia del curso
     * @param lu Legajo del estudiante
     * @return Estudiante inscripto al curso
     * @throws ModelException Si el id del curso o el lu del estudiante es nulo o vacío
     * @throws ModelException Si el estudiante no está inscripto al curso
     */
    @Override
    public Estudiante obtenerEstudianteEntityPorCodigoAsistenciaYLu(String codigoAsistencia, String lu) {
        if (codigoAsistencia == null || codigoAsistencia.isEmpty() || codigoAsistencia.isBlank()) {
            logger.error("Error al obtener el estudiante: El id del curso no puede ser nulo ni vacío");
            throw new ModelException("El id del curso no puede ser nulo ni vacío");
        }

        if (lu == null || lu.isEmpty() || lu.isBlank()) {
            logger.error("Error al obtener el estudiante: El lu no puede ser nulo ni vacío");
            throw new ModelException("El lu no puede ser nulo ni vacío");
        }
        Estudiante estudiante = estudianteRepository.findByCursoCodigoAsistenciaAndLu(codigoAsistencia, lu);
        if(estudiante == null){
            logger.error("El estudiante con el lu " + lu + " no existe");
            throw new ModelException("El estudiante con el lu " + lu + " no existe");
        }
        logger.info("Estudiante encontrado con éxito, lu: " + lu);
        return estudiante;
    }


}
