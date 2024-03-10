package asist.io.service;

import java.util.List;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;


public interface IAsistenciaService {
    public AsistenciaGetDTO registrarAsistencia(AsistenciaPostDTO asistenciaPostDTO);

    public List<AsistenciaGetDTO> obtenerAsistenciaPorCurso(String cursoId);

    public List<AsistenciaGetDTO> obtenerAsistenciaPorPeriodoYCurso(String fechaInicio, String fechaFin, String cursoId);

    public List<AsistenciaGetDTO> obtenerAsistenciaPorLuYCurso(String lu, String cursoId);
}
