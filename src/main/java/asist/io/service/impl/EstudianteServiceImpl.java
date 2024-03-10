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

import java.util.List;

@Service
public class EstudianteServiceImpl implements IEstudianteService {
    private final Logger logger =  Logger.getLogger(this.getClass());

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
        if (estudiante == null) throw new ModelException("El estudiante no puede ser nulo");

        if (estudianteRepository.existsByLu(estudiante.getLu())) throw new ModelException("El lu del alumno con el LU " + estudiante.getLu() + " ya esta registrado");

        EstudianteGetDTO estudianteRegistrado = EstudianteMapper.toGetDTO(estudianteRepository.save(EstudianteMapper.toEntity(estudiante)));
        return estudianteRegistrado;
    }

    /**
     * Registra una lista de estudiantes en la base de datos
     * @param estudiantes Lista de estudiantes a registrar
     * @return Lista de estudiantes registrados
     * @throws ModelException Si la lista de estudiantes es nula o vacía
     */
    public List<EstudianteGetDTO> registrarEstudiantes(List<EstudiantePostDTO> estudiantes) throws ModelException {
        if (estudiantes == null || estudiantes.isEmpty()) throw new ModelException("La lista de estudiantes no puede ser nula ni vacía");

        List<EstudianteGetDTO> estudiantesRegistrados = EstudianteMapper.toGetDTO(estudianteRepository.saveAll(EstudianteMapper.toEntity(estudiantes)));
        return estudiantesRegistrados;
    }

    /**
     * Elimina un estudiante en la base de datos
     * @param id Id del estudiante a eliminar
     * @return true si se eliminó el estudiante, false si no existe el estudiante
     * @throws ModelException
     */
    @Override
    public boolean eliminarEstudiante(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) throw new ModelException("El id no puedo ser nulo ni vacío");

        if (!estudianteRepository.existsById(id)) return false;

        estudianteRepository.deleteById(id);
        return true;
    }

    /**
     * Elimina una lista de estudiantes en la base de datos
     * @param ids Lista de ids de estudiantes a eliminar
     * @return true si se eliminaron los estudiantes, false si no existe alguno de los estudiantes
     * @throws ModelException Si la lista de ids es nula o vacía
     */
    public boolean eliminarEstudiantes(List<String> ids) throws ModelException {
        if (ids == null || ids.isEmpty()) throw new ModelException("La lista de ids no puede ser nula ni vacía");


        for (String id : ids) {
            if (!estudianteRepository.existsById(id)) return false;
        }

        estudianteRepository.deleteAllById(ids);
        return true;
    }

    /**
     * Obtiene un estudiante por su lu
     * @param lu Lu del estudiante a obtener
     * @return Estudiante si existe, null si no existe
     * @throws ModelException Si el lu es nulo o vacío
     */
    @Override
    public EstudianteGetDTO obtenerEstudiantePorLu(String lu) throws ModelException {
        if (lu == null || lu.isEmpty() || lu.isBlank()) throw new ModelException("El lu no puede ser nulo ni vacío");

        if (!estudianteRepository.existsByLu(lu)) throw new ModelException("El estudiante con el lu " + lu + " no existe");

        EstudianteGetDTO estudianteEncontrado = EstudianteMapper.toGetDTO(estudianteRepository.findByLu(lu));
        return estudianteEncontrado;
    }

    /**
     * Obtiene un estudiante por id
     * @param id id del estudiante
     * @return Estudiante si existe, null si no existe
     */
    @Override
    public EstudianteGetDTO obtenerEstudiantePorId(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) throw new ModelException("El id del estudiante no puede ser nulo ni vacío");

        if (!estudianteRepository.existsById(id)) throw new ModelException("El estudiante con el id " + id + " no existe");

        EstudianteGetDTO estudianteEncontrado = EstudianteMapper.toGetDTO(estudianteRepository.findById(id).get());
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
        if (id == null || id.isEmpty() || id.isBlank()) throw new ModelException("El id del curso no puede ser nulo ni vacío");

        if (!cursoRepository.existsById(id)) throw new ModelException("El curso con el id " + id + " no existe");

        List<EstudianteGetDTO> estudiantesEncontrados = EstudianteMapper.toGetDTO(estudianteRepository.obtenerEstudiantesPorIdCurso(id));
        return estudiantesEncontrados;
    }

    /**
     * Obtiene un estudiante por su lu
     * @param lu Lu del estudiante
     * @return Estudiante Entity 
     * @throws ModelException Si el estudiante no existe
     */
    @Override
    public Estudiante obtenerEstudianteEntityPorLu(String lu) {
        Estudiante estudiante = estudianteRepository.findByLu(lu);
        if(estudiante == null){
            logger.error("El estudiante con el lu " + lu + " no existe");
            throw new ModelException("El estudiante con el lu " + lu + " no existe");
        }
        return estudiante;
    }
}
