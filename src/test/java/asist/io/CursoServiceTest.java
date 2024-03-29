package asist.io;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPatchDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.mapper.CursoMapper;
import asist.io.service.ICursoService;
import asist.io.service.IUsuarioService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CursoServiceTest {
    @Autowired
    private ICursoService cursoService;
    @Autowired
    private IUsuarioService usuarioService;
    CursoPostDTO cursoPostDTO;
    CursoPatchDTO cursoPatchDTO;
    CursoGetDTO cursoGetDTO;

    UsuarioPostDTO usuario;

    @BeforeEach
    public void setup() {
        usuario = new UsuarioPostDTO();
        usuario.setNombre("Usuario de prueba");
        usuario.setCorreo("usuario@prueba.com");
        usuario.setContrasena("dadas12345678.1");

        usuarioService.guardarUsuario(usuario);
        String idUsuario = usuarioService.buscarUsuarioDto(usuario.getCorreo()).getId();

        cursoPostDTO = new CursoPostDTO();
        cursoPostDTO.setNombre("Curso de prueba");
        cursoPostDTO.setDescripcion("Curso de prueba");
        cursoPostDTO.setCarrera("Ingeniería en Sistemas");
        cursoPostDTO.setIdUsuario(idUsuario);

        cursoPatchDTO = new CursoPatchDTO();

        cursoGetDTO = new CursoGetDTO();
    }

    @AfterEach
    public void tearDown() {
        cursoPostDTO = null;
        cursoPatchDTO = null;
        cursoGetDTO = null;
        usuarioService.eliminarUsuario(usuario.getCorreo(), usuario.getContrasena());
        usuario = null;

    }

    /**
     * Test para registrar un curso
     * @throws ModelException
     */
    @Test()
    @DisplayName("Registrar curso")
    public void registrarCursoTest() throws ModelException {
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);
        assertNotNull(cursoRegistrado);

        assertThrows(ModelException.class, () -> {
            cursoPostDTO = null;
            cursoService.registrarCurso(cursoPostDTO);
        });

        cursoService.eliminarCurso(cursoRegistrado.getId());
    }

    /**
     * Test para eliminar un curso
     * @throws ModelException
     */
    @Test()
    @DisplayName("Eliminar curso")
    public void eliminarCursoTest() throws ModelException {
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);
        boolean result = cursoService.eliminarCurso(cursoRegistrado.getId());
        assertTrue(result);

        assertThrows(ModelException.class, () -> {
            cursoService.eliminarCurso("");
        });

        assertThrows(ModelException.class, () -> {
            cursoService.eliminarCurso("        ");
        });
    }

    /**
     * Test para actualizar un curso
     * @throws ModelException
     */
    @Test()
    @DisplayName("Actualizar curso")
    public void actualizarCursoTest() throws ModelException {
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);
        String nuevoNombre = "Curso de prueba actualizado";
        cursoRegistrado.setNombre(nuevoNombre);
        CursoGetDTO cursoActualizado = cursoService.actualizarCurso(CursoMapper.toPatchDTO(cursoRegistrado));

        assertEquals(cursoActualizado.getNombre(), nuevoNombre);
        assertEquals(cursoActualizado.getId(), cursoRegistrado.getId());

        cursoService.eliminarCurso(cursoRegistrado.getId());
    }

    /**
     * Test para actualizar un curso - Argumento inválido
     */
    @Test
    @DisplayName("Actualizar curso - Argumento inválido")
    public void actualizarCursoArgumentoInvalido() {
        assertThrows(ModelException.class, () -> {
            cursoService.actualizarCurso(null);
        });

        assertThrows(ModelException.class, () -> {
            cursoService.actualizarCurso(new CursoPatchDTO());
        });

        assertThrows(ModelException.class, () -> {
            cursoPatchDTO.setId("");
            cursoService.actualizarCurso(cursoPatchDTO);
        });
    }

    /**
     * Test para actualizar un curso - Código asistencia
     * @throws ModelException
     */
    @Test
    @DisplayName("Actualizar curso - Código asistencia")
    public void actualizarCursoCodigoAsistencia() throws ModelException {
        cursoPostDTO.setCodigoAsistencia("123ABC");
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);

        assertDoesNotThrow(() -> {
            cursoService.actualizarCurso(CursoMapper.toPatchDTO(cursoRegistrado));
        });

        assertDoesNotThrow(() -> {
            cursoRegistrado.setCodigoAsistencia("124ABC");
            cursoService.actualizarCurso(CursoMapper.toPatchDTO(cursoRegistrado));
        });

        assertThrows(ModelException.class, () -> {
            cursoPostDTO.setCodigoAsistencia("124ABC");
            CursoGetDTO cursoRegistrado2 = cursoService.registrarCurso(cursoPostDTO);
        });

        cursoService.eliminarCurso(cursoRegistrado.getId());
    }

    /**
     * Test para obtener un curso por id
     * @throws ModelException
     */
    @Test()
    @DisplayName("Obtener curso por id")
    public void obtenerCursoPorId() throws ModelException {
        CursoGetDTO expected = cursoService.registrarCurso(cursoPostDTO);
        CursoGetDTO result = cursoService.obtenerCursoPorId(expected.getId());

        assertEquals(expected.getId(), result.getId());

        cursoService.eliminarCurso(expected.getId());
    }


    /**
     * Test para obtener un curso por código de asistencia - Argumento inválido
     */
    @Test()
    @DisplayName("Obtener curso por código de asistencia - Argumento inválido")
    public void obtenerCursoPorCodigoAsistenciaArgumentoInvalido() {
        assertThrows(ModelException.class, () -> {
            cursoService.obtenerCursoPorCodigoAsistencia("");
        });

        assertThrows(ModelException.class, () -> {
            cursoService.obtenerCursoPorCodigoAsistencia(null);
        });

        assertThrows(ModelException.class, () -> {
            cursoService.obtenerCursoPorCodigoAsistencia("    ");
        });
    }

    /**
     * Test para obtener un curso por código de asistencia
     * @throws ModelException
     */
    @Test()
    @DisplayName("Obtener curso por código de asistencia - Argumento válido")
    public void obtenerCursoPorCodigoAsistenciaArgumentoValido() throws ModelException {
        cursoPostDTO.setCodigoAsistencia("123ABC");
        CursoGetDTO expected = cursoService.registrarCurso(cursoPostDTO);
        CursoGetDTO result = cursoService.obtenerCursoPorCodigoAsistencia(expected.getCodigoAsistencia());

        assertEquals(expected.getId(), result.getId());

        cursoService.eliminarCurso(expected.getId());
        cursoPostDTO.setCodigoAsistencia(null);
    }

    /**
     * Test para comparar la unicidad de código de asistencia
     * @throws ModelException
     */
    @Test()
    @DisplayName("Comprobar unicidad de código de asistencia")
    public void comprobarUnicidadCodigoAsistencia() throws ModelException {
        cursoPostDTO.setCodigoAsistencia("123ABC");
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);
        assertThrows(ModelException.class, () -> {
            cursoService.registrarCurso(cursoPostDTO);
        });

        cursoService.eliminarCurso(cursoRegistrado.getId());
        cursoPostDTO.setCodigoAsistencia(null);
    }

    /**
     * Test para obtener un curso por término de búsqueda
     * @throws ModelException
     */
    @Test()
    @DisplayName("Obtener curso por término de búsqueda")
    public void obtenerCursosPorTermino() throws ModelException {
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);
        assertTrue(!cursoService.obtenerCursosPorTermino("Curso").isEmpty());
        assertTrue(!cursoService.obtenerCursosPorTermino("prueba").isEmpty());
        assertThrows( ModelException.class,() -> {
            System.out.println(cursoService.obtenerCursosPorTermino("fisica"));
            cursoService.obtenerCursosPorTermino("halve");
        });
        cursoService.eliminarCurso(cursoRegistrado.getId());
    }

    @Test()
    @DisplayName("Obtener cursos por id de usuario")
    public void obtenerCursosPorIdUsuario() throws ModelException {
        throw new ModelException("Not tested");
    }

    @Test()
    @DisplayName("Generar código de asistencia de curso")
    public void generarCodigoAsistencia() {
        String codigo = cursoService.generarCodigoAsistencia();
        assertNotNull(codigo);
        assertEquals(6, codigo.length());
        assertThrows(ModelException.class, () -> {
            cursoService.obtenerCursoPorCodigoAsistencia(codigo);
        });
    }
}
