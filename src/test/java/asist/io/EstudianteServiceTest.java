package asist.io;

import asist.io.entity.Curso;
import asist.io.entity.Estudiante;
import asist.io.entity.Inscripcion;
import asist.io.exception.ModelException;
import asist.io.service.ICursoService;
import asist.io.service.IEstudianteService;
import asist.io.service.IInscripcionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EstudianteServiceTest {
    @Autowired
    private IEstudianteService estudianteService;
    @Autowired
    private ICursoService cursoService;
    @Autowired
    private IInscripcionService inscripcionService;

     Estudiante estudiante;
    Curso curso;
    Inscripcion inscripcion;

    @BeforeEach
    public void setup() throws ModelException {
        estudiante = new Estudiante();
        estudiante.setLu("ING123");
        estudiante.setNombre("Juan Perez");

        curso = new Curso();
        curso.setNombre("Algoritmos");
        curso.setDescripcion("Curso de algoritmos");
        curso.setCarrera("Ingeniería en Sistemas");

        inscripcion = new Inscripcion();
        inscripcion.setCurso(curso);
        inscripcion.setEstudiante(estudiante);
    }


    @AfterEach
    public void tearDown() throws ModelException {
        estudiante = null;

        curso = null;
        inscripcion = null;
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

    /**
     * Test para obtener estudiantes por id de curso
     * @throws ModelException
     */
    @Test
    @DisplayName("Obtener estudiantes por id de curso")
    public void obtenerEstudiantesPorIdCurso() throws ModelException {
        curso = cursoService.registrarCurso(curso);
        estudiante = estudianteService.registrarEstudiante(estudiante);
        inscripcion = inscripcionService.registrarInscripcion(inscripcion);

        List<Estudiante> estudiantesObtenidos = estudianteService.obtenerEstudiantesPorIdCurso(curso.getId());

        assertNotNull(estudiantesObtenidos);
        assertEquals(estudiantesObtenidos.get(0).getId(), estudiante.getId());

        inscripcionService.eliminarInscripcionPorId(inscripcion.getId());
        cursoService.eliminarCurso(curso.getId());
        estudianteService.eliminarEstudiante(estudiante.getId());
    }
}
