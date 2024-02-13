package asist.io.service.impl;

import asist.io.entity.Inscripcion;
import asist.io.exception.ModelException;
import asist.io.repository.InscripcionRepository;
import asist.io.service.IInscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscripcionServiceImpl implements IInscripcionService {
    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Override
    public Inscripcion registrarInscripcion(Inscripcion inscripcion) throws ModelException {
        if (inscripcion == null) throw new ModelException("La inscripción no puede ser nula");
        if (inscripcion.getEstudiante() == null) throw new ModelException("El estudiante de la inscripción no puede ser nulo");
        if (inscripcion.getCurso() == null) throw new ModelException("El curso de la inscripción no puede ser nulo");

        return inscripcionRepository.save(inscripcion);
    }

    @Override
    public Inscripcion obtenerInscripcionPorId(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) throw new ModelException("El id del curso no puede ser nulo, vacío o en blanco");

        return inscripcionRepository.findById(id).orElse(null);
    }

    @Override
    public boolean eliminarInscripcionPorId(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) throw new ModelException("El id del curso no puede ser nulo, vacío o en blanco");

        if (!inscripcionRepository.existsById(id)) return false;

        inscripcionRepository.deleteById(id);
        return true;
    }
}
