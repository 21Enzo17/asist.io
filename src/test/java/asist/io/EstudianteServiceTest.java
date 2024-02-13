package asist.io;

import asist.io.entity.Estudiante;
import asist.io.exception.ModelException;
import asist.io.service.IEstudianteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EstudianteServiceTest {
    @Autowired
    private IEstudianteService estudianteService;
    Estudiante estudiante;

    @BeforeEach
    public void setup() {
        estudiante = new Estudiante();
        estudiante.setLu("ING123");
        estudiante.setNombre("Juan Perez");
    }


    /**
     * Test para registrar un estudiante
     */
    @Test
    @DisplayName("Registrar estudiante")
    public void registrarEstudiante() throws ModelException {
        Estudiante estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

        assertNotNull(estudianteRegistrado);
        assertThrows(ModelException.class, () -> estudianteService.registrarEstudiante(estudiante));

        estudianteService.eliminarEstudiante(estudianteRegistrado.getId());
    }

    /**
     * Test para eliminar un estudiante - argumento válido
     */
    @Test
    @DisplayName("Eliminar estudiante")
    public void eliminarEstudiante() throws ModelException {
        Estudiante estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

        assertTrue(estudianteService.eliminarEstudiante(estudianteRegistrado.getId()));
        assertFalse(estudianteService.eliminarEstudiante(estudianteRegistrado.getId()));
    }

    /**
     * Test para eliminar un estudiante - argumento inválido
     */
    @Test
    @DisplayName("Eliminar estudiante - argumento inválido")
    public void eliminarEstudianteArgumentoInvalido() {
        assertThrows(ModelException.class, () -> estudianteService.eliminarEstudiante(null));
        assertThrows(ModelException.class, () -> estudianteService.eliminarEstudiante(""));
        assertThrows(ModelException.class, () -> estudianteService.eliminarEstudiante("    "));
    }

    /**
     * Test para obtener un estudiante por su lu
     * @throws ModelException
     */
    @Test
    @DisplayName("Obtener estudiante por lu")
    public void obtenerEstudiantePorLu() throws ModelException {
        Estudiante estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

        assertNotNull(estudianteService.obtenerEstudiantePorLu(estudiante.getLu()));
        assertEquals(estudianteRegistrado.getId(), estudianteService.obtenerEstudiantePorLu(estudiante.getLu()).getId());
        assertNull(estudianteService.obtenerEstudiantePorLu("SYS124"));

        estudianteService.eliminarEstudiante(estudianteRegistrado.getId());
    }

    /**
     * Test para obtener un estudiante por su lu - argumento inválido
     */
    @Test
    @DisplayName("Obtener estudiante por lu - argumento inválido")
    public void obtenerEstudiantePorLuArgumentoInvalido() {
        assertThrows(ModelException.class, () -> estudianteService.obtenerEstudiantePorLu(null));
        assertThrows(ModelException.class, () -> estudianteService.obtenerEstudiantePorLu(""));
        assertThrows(ModelException.class, () -> estudianteService.obtenerEstudiantePorLu("    "));
    }
}
