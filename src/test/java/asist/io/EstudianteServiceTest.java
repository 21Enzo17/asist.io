package asist.io;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.exception.filters.HttpException;
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
        usuario = new UsuarioPostDTO();
        usuario.setNombre("Usuario de prueba");
        usuario.setCorreo("usuario@prueba.com");
        usuario.setContrasena("dadas12345678.1");

        usuarioService.guardarUsuario(usuario);
        String idUsuario = usuarioService.buscarUsuarioDto(usuario.getCorreo()).getId();

        curso = new CursoPostDTO();
        curso.setCarrera("Test");
        curso.setDescripcion("Test");
        curso.setNombre("TestEstudiantes");
        curso.setIdUsuario(idUsuario);

        cursoRegistrado = cursoService.registrarCurso(curso);

        estudiante = new EstudiantePostDTO();
        estudiante.setLu("ING123");
        estudiante.setNombre("Juan Perez");
        estudiante.setCursoId(cursoRegistrado.getId());
    }


    @AfterEach
    public void tearDown() {
        usuarioService.eliminarUsuario(usuario.getCorreo(), "dadas12345678.1");
        estudiante = null;
        curso = null;
        usuario = null;
    }

    
    /**
     * Test para registrar un estudiante
     */
    @Test
    @DisplayName("Registrar estudiante")
    public void registrarEstudiante() throws HttpException {
        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

        assertNotNull(estudianteRegistrado);
        assertThrows(HttpException.class, () -> estudianteService.registrarEstudiante(estudiante));

        estudianteService.eliminarEstudiante(estudianteRegistrado.getId());
    }

    /**
     * Test para eliminar un estudiante - argumento válido
     */
    @Test
    @DisplayName("Eliminar estudiante")
    public void eliminarEstudiante() throws HttpException {
        EstudianteGetDTO estudianteRegistrado = estudianteService.registrarEstudiante(estudiante);

        assertTrue(estudianteService.eliminarEstudiante(estudianteRegistrado.getId()));
        assertThrows(HttpException.class, () -> estudianteService.eliminarEstudiante(estudianteRegistrado.getId()));
    }

    /**
     * Test para eliminar un estudiante - argumento inválido
     */
    @Test
    @DisplayName("Eliminar estudiante - argumento inválido")
    public void eliminarEstudianteArgumentoInvalido() {
        assertThrows(HttpException.class, () -> estudianteService.eliminarEstudiante(null));
        assertThrows(HttpException.class, () -> estudianteService.eliminarEstudiante(""));
        assertThrows(HttpException.class, () -> estudianteService.eliminarEstudiante("    "));
    }

    /**
     * Test para obtener un estudiante por su lu
     * @throws HttpException
     */
    @Test
    @DisplayName("Obtener estudiante por lu")
    public void obtenerEstudiantePorLu() throws HttpException {
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
        assertThrows(HttpException.class, () -> estudianteService.obtenerEstudiantePorLuYCursoId(null,null));
        assertThrows(HttpException.class, () -> estudianteService.obtenerEstudiantePorLuYCursoId("",""));
        assertThrows(HttpException.class, () -> estudianteService.obtenerEstudiantePorLuYCursoId("    ", ""));
    }

    /**
     * Test para obtener estudiantes por id de curso
     * @throws HttpException
     */
    @Test
    @DisplayName("Obtener estudiantes por id de curso")
    public void obtenerEstudiantesPorIdCurso() throws HttpException {
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

        estudianteService.eliminarEstudiante(estudianteRegistrado.getId());
        cursoService.eliminarCurso(cursoRegistrado.getId());
    }

    @Test
    @DisplayName("Registrar una lista de estudiantes")
    public void registrarEstudiantes() throws HttpException {
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
    public void eliminarEstudiantes() throws HttpException {
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

        assertThrows(HttpException.class, () -> estudianteService.eliminarEstudiantes(null));
        assertThrows(HttpException.class, () -> estudianteService.eliminarEstudiantes(List.of()));
    }
}
