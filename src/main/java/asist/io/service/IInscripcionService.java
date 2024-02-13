package asist.io.service;

import asist.io.entity.Inscripcion;
import asist.io.exception.ModelException;

public interface IInscripcionService {
    public Inscripcion registrarInscripcion(Inscripcion inscripcion) throws ModelException;
    public Inscripcion obtenerInscripcionPorId(String id) throws ModelException;
    public boolean eliminarInscripcionPorId(String id) throws ModelException;
}
