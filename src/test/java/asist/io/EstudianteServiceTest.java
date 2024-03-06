package asist.io;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.dto.inscripcionDTO.InscripcionGetDTO;
import asist.io.dto.inscripcionDTO.InscripcionPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.ICursoService;
import asist.io.service.IEstudianteService;
import asist.io.service.IInscripcionService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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

    EstudiantePostDTO estudiante;
    CursoPostDTO curso;
    InscripcionPostDTO inscripcion;

    @BeforeEach
    public void setup() {
        estudiante = new EstudiantePostDTO();
        estudiante.setLu("ING123");
        estudiante.setNombre("Juan Perez");
    }


    @AfterEach
    public void tearDown() {
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
        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

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
        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

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
        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

        assertNotNull(estudianteService.obtenerEstudiantePorLu(estudiante.getLu()));
        assertEquals(estudianteRegistrado.getId(), estudianteService.obtenerEstudiantePorLu(estudiante.getLu()).getId());

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

        curso = new CursoPostDTO();
        curso.setNombre("Algoritmos");
        curso.setDescripcion("Curso de algoritmos");
        curso.setCarrera("Ingeniería en Sistemas");

        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(curso);
        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

        inscripcion = new InscripcionPostDTO(estudianteRegistrado.getId(), cursoRegistrado.getId());
        InscripcionGetDTO inscripcionRegistrada = inscripcionService.registrarInscripcion(inscripcion);

        List<EstudianteGetDTO> estudiantesObtenidos = estudianteService.obtenerEstudiantesPorIdCurso(cursoRegistrado.getId());

        assertNotNull(estudiantesObtenidos);
        assertEquals(estudiantesObtenidos.get(0).getId(), estudianteRegistrado.getId());

        inscripcionService.eliminarInscripcionPorId(inscripcionRegistrada.getId());
        cursoService.eliminarCurso(cursoRegistrado.getId());
        estudianteService.eliminarEstudiante(estudianteRegistrado.getId());
    }

    @Test
    @DisplayName("Registrar una lista de estudiantes")
    public void registrarEstudiantes() throws ModelException {
        EstudiantePostDTO estudiante2 = new EstudiantePostDTO();
        estudiante2.setLu("ING124");
        estudiante2.setNombre("Juan Perez");

        EstudiantePostDTO estudiante3 = new EstudiantePostDTO();
        estudiante3.setLu("ING125");
        estudiante3.setNombre("Juan Perez");

        List<EstudiantePostDTO> estudiantes = List.of(estudiante, estudiante2, estudiante3);
        List<EstudianteGetDTO> estudiantesRegistrados = estudianteService.registrarEstudiantes(estudiantes);

        assertNotNull(estudiantesRegistrados);
        assertEquals(estudiantesRegistrados.size(), 3);

        estudianteService.eliminarEstudiante(estudiantesRegistrados.get(0).getId());
        estudianteService.eliminarEstudiante(estudiantesRegistrados.get(1).getId());
        estudianteService.eliminarEstudiante(estudiantesRegistrados.get(2).getId());
    }

    @Test
    @DisplayName("Eliminar una lista de estudiantes")
    public void eliminarEstudiantes() throws ModelException {
        EstudiantePostDTO estudiante2 = new EstudiantePostDTO();
        estudiante2.setLu("ING124");
        estudiante2.setNombre("Juan Perez");

        EstudiantePostDTO estudiante3 = new EstudiantePostDTO();
        estudiante3.setLu("ING125");
        estudiante3.setNombre("Juan Perez");

        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);
        EstudianteGetDTO estudianteRegistrado2 = estudianteService.registrarEstudiante(estudiante2);
        EstudianteGetDTO estudianteRegistrado3 = estudianteService.registrarEstudiante(estudiante3);

        List<String> ids = List.of(estudianteRegistrado.getId(), estudianteRegistrado2.getId(), estudianteRegistrado3.getId());
        assertTrue(estudianteService.eliminarEstudiantes(ids));

        assertThrows(ModelException.class, () -> estudianteService.eliminarEstudiantes(null));
        assertThrows(ModelException.class, () -> estudianteService.eliminarEstudiantes(List.of()));
    }
}
