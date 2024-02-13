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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class InscripcionServiceTest {
    @Autowired
    private IInscripcionService inscripcionService;
    @Autowired
    private ICursoService cursoService;
    @Autowired
    private IEstudianteService estudianteService;

    Inscripcion inscJuanInformatica;
    Inscripcion inscRocioInformatica;
    Inscripcion inscSantiagoMatematicas;
    Inscripcion inscSantiagoInformatica;

    Estudiante juan;
    Estudiante rocio;
    Estudiante santiagoMat;
    Estudiante santiagoInf;


    Curso informatica;
    Curso matematicas;

    @BeforeEach
    public void setUp() throws ModelException {
        /* --- Cursos --- */
        informatica = new Curso();
        informatica.setNombre("Informática");
        informatica.setDescripcion("Curso sobre Informática");
        informatica.setCarrera("Ingeniería Informática");
        informatica = cursoService.registrarCurso(informatica);

        matematicas = new Curso();
        matematicas.setNombre("Matemáticas");
        matematicas.setDescripcion("Curso sobre Matemáticas");
        matematicas.setCarrera("Licenciatura Superior en Matemáticas");
        matematicas = cursoService.registrarCurso(matematicas);

        /* --- Estudiantes --- */

        juan = new Estudiante();
        juan.setNombre("Juan");
        juan.setLu("INF123");
        juan = estudianteService.registrarEstudiante(juan);

        rocio = new Estudiante();
        rocio.setNombre("Rocío");
        rocio.setLu("INF124");
        rocio = estudianteService.registrarEstudiante(rocio);

        santiagoMat = new Estudiante();
        santiagoMat.setNombre("Santiago");
        santiagoMat.setLu("MAT123");
        santiagoMat = estudianteService.registrarEstudiante(santiagoMat);

        santiagoInf = new Estudiante();
        santiagoInf.setNombre("Santiago");
        santiagoInf.setLu("INF125");
        santiagoInf = estudianteService.registrarEstudiante(santiagoInf);

        /* --- Inscripciones --- */

        inscJuanInformatica = new Inscripcion();
        inscJuanInformatica.setEstudiante(juan);
        inscJuanInformatica.setCurso(informatica);

        inscRocioInformatica = new Inscripcion();
        inscRocioInformatica.setEstudiante(rocio);
        inscRocioInformatica.setCurso(informatica);

        inscSantiagoMatematicas = new Inscripcion();
        inscSantiagoMatematicas.setEstudiante(santiagoMat);
        inscSantiagoMatematicas.setCurso(matematicas);

        inscSantiagoInformatica = new Inscripcion();
        inscSantiagoInformatica.setEstudiante(santiagoInf);
        inscSantiagoInformatica.setCurso(informatica);
    }

    @AfterEach
    public void tearDown() throws ModelException {
        cursoService.eliminarCurso(informatica.getId());
        cursoService.eliminarCurso(matematicas.getId());

        estudianteService.eliminarEstudiante(juan.getId());
        estudianteService.eliminarEstudiante(rocio.getId());
        estudianteService.eliminarEstudiante(santiagoMat.getId());
        estudianteService.eliminarEstudiante(santiagoInf.getId());

        juan = null;
        rocio = null;
        santiagoMat = null;
        santiagoInf = null;

        informatica = null;
        matematicas = null;

        inscJuanInformatica = null;
        inscRocioInformatica = null;
        inscSantiagoMatematicas = null;
        inscSantiagoInformatica = null;
    }

    /**
     * Test para registrar inscripción con un argumento válido
     * @throws ModelException
     */
    @Test
    @DisplayName("Registrar inscripción")
    public void registrarInscripcion() throws ModelException {
        Inscripcion inscripcionGuardada = inscripcionService.registrarInscripcion(inscJuanInformatica);

        assertNotNull(inscripcionGuardada);
        assertEquals(inscJuanInformatica.getEstudiante().getLu(), inscripcionGuardada.getEstudiante().getLu());

        inscripcionService.eliminarInscripcionPorId(inscripcionGuardada.getId());
    }

    /**
     * Test para registrar inscripción con un argumento inválido
     */
    @Test
    @DisplayName("Registrar inscripción - argumento inválido")
    public void registrarInscripcionArgumentoInvalido() {
        assertThrows(ModelException.class, () -> inscripcionService.registrarInscripcion(null));
    }

    /**
     * Test para eliminar inscripción por id
     * @throws ModelException
     */
    @Test
    @DisplayName("Eliminar inscripción por id")
    public void eliminarInscripcionPorId() throws ModelException {
        Inscripcion inscripcionGuardada = inscripcionService.registrarInscripcion(inscJuanInformatica);

        assertTrue(inscripcionService.eliminarInscripcionPorId(inscripcionGuardada.getId()));
    }

    /**
     * Test para obtener inscripción por id
     * @throws ModelException
     */
    @Test
    @DisplayName("Obtener inscripción por id")
    public void obtenerInscripcionPorId() throws ModelException {
        Inscripcion inscripcionRegistrada = inscripcionService.registrarInscripcion(inscJuanInformatica);

        assertNotNull(inscripcionService.obtenerInscripcionPorId(inscripcionRegistrada.getId()));

        inscripcionService.eliminarInscripcionPorId(inscripcionRegistrada.getId());
    }
}
