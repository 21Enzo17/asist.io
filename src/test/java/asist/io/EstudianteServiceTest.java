package asist.io;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.ICursoService;
import asist.io.service.IEstudianteService;
import asist.io.service.IUsuarioService;
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
    private IUsuarioService usuarioService;

    EstudiantePostDTO estudiante;
    CursoPostDTO curso;
    CursoGetDTO cursoRegistrado;
    UsuarioPostDTO usuario;

    @BeforeEach
    public void setup() {
        curso = new CursoPostDTO();
        curso.setCarrera("Test");
        curso.setDescripcion("Test");
        curso.setNombre("TestEstudiantes");
        curso.setIdUsuario("5ee995c0-e978-4bdf-b5bd-a83d36bc1603");

        cursoRegistrado = cursoService.registrarCurso(curso);

        estudiante = new EstudiantePostDTO();
        estudiante.setLu("ING123");
        estudiante.setNombre("Juan Perez");
        estudiante.setCursoId(cursoRegistrado.getId());
    }


    @AfterEach
    public void tearDown() {
        estudiante = null;

        curso = null;
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

        assertNotNull(estudianteService.obtenerEstudiantePorLuYCursoId(estudiante.getLu(),estudiante.getCursoId()));
        assertEquals(estudianteRegistrado.getId(), estudianteService.obtenerEstudiantePorLuYCursoId(estudiante.getLu(), estudiante.getCursoId()).getId());

        estudianteService.eliminarEstudiante(estudianteRegistrado.getId());
    }

    /**
     * Test para obtener un estudiante por su lu - argumento inválido
     */
    @Test
    @DisplayName("Obtener estudiante por lu - argumento inválido")
    public void obtenerEstudiantePorLuArgumentoInvalido() {
        assertThrows(ModelException.class, () -> estudianteService.obtenerEstudiantePorLuYCursoId(null,null));
        assertThrows(ModelException.class, () -> estudianteService.obtenerEstudiantePorLuYCursoId("",""));
        assertThrows(ModelException.class, () -> estudianteService.obtenerEstudiantePorLuYCursoId("    ", ""));
    }

    /**
     * Test para obtener estudiantes por id de curso
     * @throws ModelException
     */
    @Test
    @DisplayName("Obtener estudiantes por id de curso")
    public void obtenerEstudiantesPorIdCurso() throws ModelException {

        usuario = new UsuarioPostDTO();
        usuario.setNombre("Juan Perez");
        usuario.setContrasena("1234");
        usuario.setCorreo("prueba@prueba.com");
        usuarioService.guardarUsuario(usuario);

        curso = new CursoPostDTO();
        curso.setNombre("Algoritmos");
        curso.setDescripcion("Curso de algoritmos");
        curso.setCarrera("Ingeniería en Sistemas");
        curso.setIdUsuario(usuarioService.buscarUsuarioDto(usuario.getCorreo()).getId());

        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(curso);
        estudiante.setCursoId(cursoRegistrado.getId());
        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);


        List<EstudianteGetDTO> estudiantesObtenidos = estudianteService.obtenerEstudiantesPorIdCurso(cursoRegistrado.getId());

        assertNotNull(estudiantesObtenidos);
        assertEquals(estudiantesObtenidos.get(0).getId(), estudianteRegistrado.getId());

        cursoService.eliminarCurso(cursoRegistrado.getId());
        estudianteService.eliminarEstudiante(estudianteRegistrado.getId());
        usuarioService.eliminarUsuario(usuario.getCorreo(),"1234");
    }

    @Test
    @DisplayName("Registrar una lista de estudiantes")
    public void registrarEstudiantes() throws ModelException {
        EstudiantePostDTO estudiante2 = new EstudiantePostDTO();
        estudiante2.setLu("ING124");
        estudiante2.setNombre("Juan Perez");
        estudiante2.setCursoId(cursoRegistrado.getId());

        EstudiantePostDTO estudiante3 = new EstudiantePostDTO();
        estudiante3.setLu("ING125");
        estudiante3.setNombre("Juan Perez");
        estudiante3.setCursoId(cursoRegistrado.getId());

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
        estudiante2.setCursoId(cursoRegistrado.getId());

        EstudiantePostDTO estudiante3 = new EstudiantePostDTO();
        estudiante3.setLu("ING125");
        estudiante3.setNombre("Juan Perez");
        estudiante3.setCursoId(cursoRegistrado.getId());

        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);
        EstudianteGetDTO estudianteRegistrado2 = estudianteService.registrarEstudiante(estudiante2);
        EstudianteGetDTO estudianteRegistrado3 = estudianteService.registrarEstudiante(estudiante3);

        List<String> ids = List.of(estudianteRegistrado.getId(), estudianteRegistrado2.getId(), estudianteRegistrado3.getId());
        assertTrue(estudianteService.eliminarEstudiantes(ids));

        assertThrows(ModelException.class, () -> estudianteService.eliminarEstudiantes(null));
        assertThrows(ModelException.class, () -> estudianteService.eliminarEstudiantes(List.of()));
    }
}
