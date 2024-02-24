package asist.io.service.impl;

import asist.io.dto.EstudianteGetDTO;
import asist.io.dto.EstudiantePostDTO;
import asist.io.exception.ModelException;
import asist.io.mapper.EstudianteMapper;
import asist.io.repository.CursoRepository;
import asist.io.repository.EstudianteRepository;
import asist.io.service.IEstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstudianteServiceImpl implements IEstudianteService {
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
     * Obtiene un estudiante por su lu
     * @param lu Lu del estudiante a obtener
     * @return Estudiante si existe, null si no existe
     * @throws ModelException Si el lu es nulo o vacío
     */
    @Override
    public EstudianteGetDTO obtenerEstudiantePorLu(String lu) throws ModelException {
        if (lu == null || lu.isEmpty() || lu.isBlank()) throw new ModelException("El lu no puede ser nulo ni vacío");

        if (!estudianteRepository.existsByLu(lu)) return null;

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
}
