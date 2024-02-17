package asist.io.service.impl;

import asist.io.dto.InscripcionGetDTO;
import asist.io.dto.InscripcionPostDTO;
import asist.io.entity.Inscripcion;
import asist.io.exception.ModelException;
import asist.io.mapper.InscripcionMapper;
import asist.io.repository.CursoRepository;
import asist.io.repository.EstudianteRepository;
import asist.io.repository.InscripcionRepository;
import asist.io.service.IInscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InscripcionServiceImpl implements IInscripcionService {
    @Autowired
    private InscripcionRepository inscripcionRepository;
    @Autowired
    private CursoRepository cursoRepository;
    @Autowired
    private EstudianteRepository estudianteRepository;

    @Override
    public InscripcionGetDTO registrarInscripcion(InscripcionPostDTO inscripcion) throws ModelException {
        if (inscripcion == null) throw new ModelException("La inscripción no puede ser nula");
        if (inscripcion.getIdEstudiante() == null) throw new ModelException("El estudiante de la inscripción no puede ser nulo");
        if (inscripcion.getIdCurso() == null) throw new ModelException("El curso de la inscripción no puede ser nulo");

        if (cursoRepository.findById(inscripcion.getIdCurso()).orElse(null) == null) throw new ModelException("El curso con el id " + inscripcion.getIdCurso() + " no existe");
        if (estudianteRepository.findById(inscripcion.getIdEstudiante()).orElse(null) == null) throw new ModelException("El estudiante con el id " + inscripcion.getIdEstudiante() + " no existe");


        InscripcionGetDTO inscripcionRegistrada = InscripcionMapper.toGetDTO(inscripcionRepository.save(InscripcionMapper.toEntity(inscripcion, cursoRepository.findById(inscripcion.getIdCurso()).get(), estudianteRepository.findById(inscripcion.getIdEstudiante()).get())));
        return inscripcionRegistrada;
    }

    @Override
    public InscripcionGetDTO obtenerInscripcionPorId(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) throw new ModelException("El id del curso no puede ser nulo, vacío o en blanco");

        Inscripcion inscripcion = inscripcionRepository.findById(id).get();
        InscripcionGetDTO inscripcionEncontrada = InscripcionMapper.toGetDTO(inscripcion);
        return inscripcionEncontrada;
    }

    @Override
    public boolean eliminarInscripcionPorId(String id) throws ModelException {
        if (id == null || id.isEmpty() || id.isBlank()) throw new ModelException("El id del curso no puede ser nulo, vacío o en blanco");

        if (!inscripcionRepository.existsById(id)) return false;

        inscripcionRepository.deleteById(id);
        return true;
    }
}
