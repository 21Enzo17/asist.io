package asist.io.service.impl;

import asist.io.dto.inscripcionDTO.InscripcionGetDTO;
import asist.io.dto.inscripcionDTO.InscripcionPostDTO;
import asist.io.entity.Inscripcion;
import asist.io.exception.ModelException;
import asist.io.mapper.InscripcionMapper;
import asist.io.repository.CursoRepository;
import asist.io.repository.EstudianteRepository;
import asist.io.repository.InscripcionRepository;
import asist.io.service.IInscripcionService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscripcionServiceImpl implements IInscripcionService {

    private final static Logger logger = Logger.getLogger(InscripcionServiceImpl.class);

    @Autowired
    private InscripcionRepository inscripcionRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;

    /**
     * Registra una inscripción en la base de datos
     * @param inscripcion Inscripción a registrar
     * @return Inscripción registrada
     * @throws ModelException Si la inscripción es nula o si el id del curso o del estudiante no existen
     */
    @Override
    public InscripcionGetDTO registrarInscripcion(InscripcionPostDTO inscripcion) throws ModelException {
        if (inscripcion == null) {
            logger.error("Error al registrar la inscripción: La inscripción no puede ser nula");
            throw new ModelException("La inscripción no puede ser nula");
        }
        if (inscripcion.getIdEstudiante() == null) {
            logger.error("Error al registrar la inscripción: El id del estudiante no puede ser nulo");
            throw new ModelException("El id del estudiante no puede ser nulo");
        }
        if (inscripcion.getIdCurso() == null) {
            logger.error("Error al registrar la inscripción: El id del curso no puede ser nulo");
            throw new ModelException("El id del curso no puede ser nulo");
        }

        if (cursoRepository.findById(inscripcion.getIdCurso()).orElse(null) == null) {
            logger.error("Error al registrar la inscripción: El curso con el id " + inscripcion.getIdCurso() + " no existe");
            throw new ModelException("El curso con el id " + inscripcion.getIdCurso() + " no existe");
        }
        if (estudianteRepository.findById(inscripcion.getIdEstudiante()).orElse(null) == null) {
            logger.error("Error al registrar la inscripción: El estudiante con el id " + inscripcion.getIdEstudiante() + " no existe");
            throw new ModelException("El estudiante con el id " + inscripcion.getIdEstudiante() + " no existe");
        }


        InscripcionGetDTO inscripcionRegistrada = InscripcionMapper.toGetDTO(inscripcionRepository.save(InscripcionMapper.toEntity(inscripcion, cursoRepository.findById(inscripcion.getIdCurso()).get(), estudianteRepository.findById(inscripcion.getIdEstudiante()).get())));
        logger.info("Inscripción registrada con éxito, id: " + inscripcionRegistrada.getId());
        return inscripcionRegistrada;
    }

    /**
     * Obtiene una inscripción por su id
     * @param id Id de la inscripción a obtener
     * @return Inscripción si existe, null si no existe
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public InscripcionGetDTO obtenerInscripcionPorId(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            logger.error("Error al obtener la inscripción: El id no puede ser nulo, vacío o en blanco");
            throw new ModelException("El id no puede ser nulo, vacío o en blanco");
        }

        Inscripcion inscripcion = inscripcionRepository.findById(id).get();
        InscripcionGetDTO inscripcionEncontrada = InscripcionMapper.toGetDTO(inscripcion);
        logger.info("Inscripción encontrada con éxito, id: " + inscripcionEncontrada.getId());
        return inscripcionEncontrada;
    }

    /**
     * Elimina una inscripción por su id
     * @param id Id de la inscripción a eliminar
     * @return true si se eliminó la inscripción, false si no existe la inscripción
     * @throws ModelException Si el id es nulo o vacío
     */
    @Override
    public boolean eliminarInscripcionPorId(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) {
            logger.error("Error al eliminar la inscripción: El id no puede ser nulo, vacío o en blanco");
            throw new ModelException("El id no puede ser nulo, vacío o en blanco");
        }

        if (!inscripcionRepository.existsById(id)) {
            logger.error("Error al eliminar la inscripción: La inscripción con id " + id + " no existe");
            return false;
        }

        inscripcionRepository.deleteById(id);
        logger.info("Inscripción eliminada con éxito, id: " + id);
        return true;
    }

    /**
     * Determina si una inscripcion existe por codigo de asistencia y lu
     * @param codigoAsistencia Codigo de asistencia
     * @param lu Lu del estudiante
     * No hace nada en caso de existir, en caso de no hacerlo lanza una excepcion ModelException
     */
    @Override
    public void existePorCodigoAsistenciaYLu(String codigoAsistencia, String lu) {
        if (!inscripcionRepository.existsByEstudianteLuAndCursoCodigoAsistencia(lu, codigoAsistencia)){
            logger.error("El alumno con LU " + lu + " no está inscripto en el curso con código de asistencia " + codigoAsistencia);
            throw new ModelException("El alumno con LU " + lu + " no está inscripto en el curso con código de asistencia " + codigoAsistencia);

        }
            
    }
}
