package asist.io.service;

import asist.io.entity.Estudiante;

public interface IEstudianteService {
    public Estudiante registrarEstudiante(Estudiante estudiante);
    public boolean eliminarEstudiante(String id);
    public Estudiante obtenerEstudiantePorLu(String lu);
}
