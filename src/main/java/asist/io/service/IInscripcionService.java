package asist.io.service;

import asist.io.dto.inscripcionDTO.InscripcionGetDTO;
import asist.io.dto.inscripcionDTO.InscripcionPostDTO;
import asist.io.exception.ModelException;

public interface IInscripcionService {
    /**
     * Registra una inscripción en la base de datos
     * @param inscripcion Inscripción a registrar
     * @return Inscripción registrada
     * @throws ModelException Si la inscripción es nula o si el id del curso o del estudiante no existen
     */
    public InscripcionGetDTO registrarInscripcion(InscripcionPostDTO inscripcion) throws ModelException;

    /**
     * Obtiene una inscripción por su id
     * @param id Id de la inscripción a obtener
     * @return Inscripción si existe, null si no existe
     * @throws ModelException Si el id es nulo o vacío
     */
    public InscripcionGetDTO obtenerInscripcionPorId(String id) throws ModelException;

    /**
     * Elimina una inscripción por su id
     * @param id Id de la inscripción a eliminar
     * @return true si se eliminó la inscripción, false si no existe la inscripción
     * @throws ModelException
     */
    public boolean eliminarInscripcionPorId(String id) throws ModelException;

    /**
     * Determina si una inscripcion existe por codigo de asistencia y lu
     * @param codigoAsistencia Codigo de asistencia
     * @param lu Lu del estudiante
     * No hace nada en caso de existir, en caso de no hacerlo lanza una excepcion ModelException
     */
    public void existePorCodigoAsistenciaYLu(String codigoAsistencia, String lu);
}
