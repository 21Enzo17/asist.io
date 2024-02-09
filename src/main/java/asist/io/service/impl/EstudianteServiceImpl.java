package asist.io.service.impl;

import asist.io.entity.Estudiante;
import asist.io.exception.ModelException;
import asist.io.repository.EstudianteRepository;
import asist.io.service.IEstudianteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstudianteServiceImpl implements IEstudianteService {
    @Autowired
    private EstudianteRepository estudianteRepository;

    /**
     * Registra un estudiante en la base de datos
     * @param estudiante Estudiante a registrar
     * @return Estudiante registrado
     * @throws ModelException Si el estudiante es nulo o si el lu del estudiante ya está registrado
     */
    @Override
    public Estudiante registrarEstudiante(Estudiante estudiante) throws ModelException {
        if (estudiante == null) throw new ModelException("El estudiante no puede ser nulo");

        if (estudianteRepository.existsByLu(estudiante.getLu())) throw new ModelException("El lu del alumno ya esta registrado");

        return estudianteRepository.save(estudiante);
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
    public Estudiante obtenerEstudiantePorLu(String lu) throws ModelException {
        if (lu == null || lu.isEmpty() || lu.isBlank()) throw new ModelException("El lu no puede ser nulo ni vacío");

        if (!estudianteRepository.existsByLu(lu)) return null;

        return estudianteRepository.findByLu(lu);
    }
}
