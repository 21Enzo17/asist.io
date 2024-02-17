package asist.io;

import asist.io.dto.*;
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

    InscripcionPostDTO inscJuanInformatica;
    InscripcionPostDTO inscRocioInformatica;
    InscripcionPostDTO inscSantiagoMatematicas;
    InscripcionPostDTO inscSantiagoInformatica;

    EstudiantePostDTO juan;
    EstudiantePostDTO rocio;
    EstudiantePostDTO santiagoMat;
    EstudiantePostDTO santiagoInf;


    CursoPostDTO informatica;
    CursoPostDTO matematicas;


    CursoGetDTO informaticaRegistrada;
    CursoGetDTO matematicasRegistrada;
    EstudianteGetDTO juanRegistrado;
    EstudianteGetDTO rocioRegistrada;
    EstudianteGetDTO santiagoMatRegistrado;
    EstudianteGetDTO santiagoInfRegistrado;

    @BeforeEach
    public void setUp() throws ModelException {
        /* --- Cursos --- */
        informatica = new CursoPostDTO();
        informatica.setNombre("Informática");
        informatica.setDescripcion("Curso sobre Informática");
        informatica.setCarrera("Ingeniería Informática");
        informaticaRegistrada = cursoService.registrarCurso(informatica);

        matematicas = new CursoPostDTO();
        matematicas.setNombre("Matemáticas");
        matematicas.setDescripcion("Curso sobre Matemáticas");
        matematicas.setCarrera("Licenciatura Superior en Matemáticas");
        matematicasRegistrada = cursoService.registrarCurso(matematicas);

        /* --- Estudiantes --- */

        juan = new EstudiantePostDTO();
        juan.setNombre("Juan");
        juan.setLu("INF123");
        juanRegistrado = estudianteService.registrarEstudiante(juan);

        rocio = new EstudiantePostDTO();
        rocio.setNombre("Rocío");
        rocio.setLu("INF124");
        rocioRegistrada = estudianteService.registrarEstudiante(rocio);

        santiagoMat = new EstudiantePostDTO();
        santiagoMat.setNombre("Santiago");
        santiagoMat.setLu("MAT123");
        santiagoMatRegistrado = estudianteService.registrarEstudiante(santiagoMat);

        santiagoInf = new EstudiantePostDTO();
        santiagoInf.setNombre("Santiago");
        santiagoInf.setLu("INF125");
        santiagoInfRegistrado = estudianteService.registrarEstudiante(santiagoInf);

        /* --- Inscripciones --- */

        inscJuanInformatica = new InscripcionPostDTO();
        inscJuanInformatica.setIdEstudiante(juanRegistrado.getId());
        inscJuanInformatica.setIdCurso(informaticaRegistrada.getId());

        inscRocioInformatica = new InscripcionPostDTO();
        inscRocioInformatica.setIdEstudiante(rocioRegistrada.getId());
        inscRocioInformatica.setIdCurso(informaticaRegistrada.getId());

        inscSantiagoMatematicas = new InscripcionPostDTO();
        inscSantiagoMatematicas.setIdEstudiante(santiagoMatRegistrado.getId());
        inscSantiagoMatematicas.setIdCurso(matematicasRegistrada.getId());

        inscSantiagoInformatica = new InscripcionPostDTO();
        inscSantiagoInformatica.setIdEstudiante(santiagoInfRegistrado.getId());
        inscSantiagoInformatica.setIdCurso(informaticaRegistrada.getId());
    }

    @AfterEach
    public void tearDown() throws ModelException {
        cursoService.eliminarCurso(informaticaRegistrada.getId());
        cursoService.eliminarCurso(matematicasRegistrada.getId());

        estudianteService.eliminarEstudiante(juanRegistrado.getId());
        estudianteService.eliminarEstudiante(rocioRegistrada.getId());
        estudianteService.eliminarEstudiante(santiagoMatRegistrado.getId());
        estudianteService.eliminarEstudiante(santiagoInfRegistrado.getId());

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

        informaticaRegistrada = null;
        matematicasRegistrada = null;
        juanRegistrado = null;
        rocioRegistrada = null;
        santiagoMatRegistrado = null;
        santiagoInfRegistrado = null;
    }

    /**
     * Test para registrar inscripción con un argumento válido
     * @throws ModelException
     */
    @Test
    @DisplayName("Registrar inscripción")
    public void registrarInscripcion() throws ModelException {
        InscripcionGetDTO inscripcionGuardada = inscripcionService.registrarInscripcion(inscJuanInformatica);

        assertNotNull(inscripcionGuardada);
        assertEquals(inscJuanInformatica.getIdEstudiante(), inscripcionGuardada.getEstudiante().getId());

        inscripcionService.eliminarInscripcionPorId(inscripcionGuardada.getId());
    }

    /**
     * Test para registrar inscripción con un argumento inválido
     */
    @Test
    @DisplayName("Registrar inscripción - argumento inválido")
    @Disabled
    public void registrarInscripcionArgumentoInvalido() {
        assertThrows(ModelException.class, () -> inscripcionService.registrarInscripcion(null));
    }

    /**
     * Test para eliminar inscripción por id
     * @throws ModelException
     */
    @Test
    @DisplayName("Eliminar inscripción por id")
    @Disabled
    public void eliminarInscripcionPorId() throws ModelException {
        InscripcionGetDTO inscripcionGuardada = inscripcionService.registrarInscripcion(inscJuanInformatica);

        assertTrue(inscripcionService.eliminarInscripcionPorId(inscripcionGuardada.getId()));
    }

    /**
     * Test para obtener inscripción por id
     * @throws ModelException
     */
    @Test
    @DisplayName("Obtener inscripción por id")
    public void obtenerInscripcionPorId() throws ModelException {
        InscripcionGetDTO inscripcionRegistrada = inscripcionService.registrarInscripcion(inscJuanInformatica);

        assertNotNull(inscripcionService.obtenerInscripcionPorId(inscripcionRegistrada.getId()));

        inscripcionService.eliminarInscripcionPorId(inscripcionRegistrada.getId());
    }
}
