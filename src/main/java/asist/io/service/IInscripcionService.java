package asist.io.service;

import asist.io.entity.Estudiante;
import asist.io.entity.Inscripcion;

import java.util.List;

public interface IInscripcionService {
    public List<Estudiante> obtenerEstudiantesPorIdCurso(String id);
    public Inscripcion obtenerInscripcionPorId(String id);
}
