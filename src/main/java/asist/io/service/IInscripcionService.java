package asist.io.service;

import asist.io.dto.InscripcionGetDTO;
import asist.io.dto.InscripcionPostDTO;
import asist.io.exception.ModelException;

public interface IInscripcionService {
    public InscripcionGetDTO registrarInscripcion(InscripcionPostDTO inscripcion) throws ModelException;
    public InscripcionGetDTO obtenerInscripcionPorId(String id) throws ModelException;
    public boolean eliminarInscripcionPorId(String id) throws ModelException;
}
