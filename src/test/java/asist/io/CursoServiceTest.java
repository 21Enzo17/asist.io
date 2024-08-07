package asist.io;

import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPatchDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.exception.filters.HttpException;
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
        usuarioService.eliminarUsuario(usuario.getCorreo(), "dadas12345678.1");
        usuario = null;

    }

    /**
     * Test para registrar un curso
     * @throws HttpException
     */
    @Test()
    @DisplayName("Registrar curso")
    public void registrarCursoTest() throws ModelException {
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);
        assertNotNull(cursoRegistrado);

        assertThrows(HttpException.class, () -> {
            cursoPostDTO = null;
            cursoService.registrarCurso(cursoPostDTO);
        });

        cursoService.eliminarCurso(cursoRegistrado.getId());
    }

    /**
     * Test para eliminar un curso
     * @throws HttpException
     */
    @Test()
    @DisplayName("Eliminar curso")
    public void eliminarCursoTest() throws HttpException {
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);
        boolean result = cursoService.eliminarCurso(cursoRegistrado.getId());
        assertTrue(result);

        assertThrows(HttpException.class, () -> {
            cursoService.eliminarCurso("");
        });

        assertThrows(HttpException.class, () -> {
            cursoService.eliminarCurso("        ");
        });
    }

    /**
     * Test para actualizar un curso
     * @throws HttpException
     */
    @Test()
    @DisplayName("Actualizar curso")
    public void actualizarCursoTest() throws HttpException {
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
        assertThrows(HttpException.class, () -> {
            cursoService.actualizarCurso(null);
        });

        assertThrows(HttpException.class, () -> {
            cursoService.actualizarCurso(new CursoPatchDTO());
        });

        assertThrows(HttpException.class, () -> {
            cursoPatchDTO.setId("");
            cursoService.actualizarCurso(cursoPatchDTO);
        });
    }

    /**
     * Test para actualizar un curso - Código asistencia
     * @throws HttpException
     */
    @Test
    @DisplayName("Actualizar curso - Código asistencia")
    public void actualizarCursoCodigoAsistencia() throws HttpException {
        cursoPostDTO.setCodigoAsistencia("123ABC");
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);

        assertDoesNotThrow(() -> {
            cursoService.actualizarCurso(CursoMapper.toPatchDTO(cursoRegistrado));
        });

        assertDoesNotThrow(() -> {
            cursoRegistrado.setCodigoAsistencia("124ABC");
            cursoService.actualizarCurso(CursoMapper.toPatchDTO(cursoRegistrado));
        });

        assertThrows(HttpException.class, () -> {
            cursoPostDTO.setCodigoAsistencia("124ABC");
            cursoService.registrarCurso(cursoPostDTO);
        });

        cursoService.eliminarCurso(cursoRegistrado.getId());
    }

    /**
     * Test para obtener un curso por id
     * @throws HttpException
     */
    @Test()
    @DisplayName("Obtener curso por id")
    public void obtenerCursoPorId() throws HttpException {
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
        assertThrows(HttpException.class, () -> {
            cursoService.obtenerCursoPorCodigoAsistencia("");
        });

        assertThrows(HttpException.class, () -> {
            cursoService.obtenerCursoPorCodigoAsistencia(null);
        });

        assertThrows(HttpException.class, () -> {
            cursoService.obtenerCursoPorCodigoAsistencia("    ");
        });
    }

    /**
     * Test para obtener un curso por código de asistencia
     * @throws HttpException
     */
    @Test()
    @DisplayName("Obtener curso por código de asistencia - Argumento válido")
    public void obtenerCursoPorCodigoAsistenciaArgumentoValido() throws HttpException {
        cursoPostDTO.setCodigoAsistencia("123ABC");
        CursoGetDTO expected = cursoService.registrarCurso(cursoPostDTO);
        CursoGetDTO result = cursoService.obtenerCursoPorCodigoAsistencia(expected.getCodigoAsistencia());

        assertEquals(expected.getId(), result.getId());

        cursoService.eliminarCurso(expected.getId());
        cursoPostDTO.setCodigoAsistencia(null);
    }

    /**
     * Test para comparar la unicidad de código de asistencia
     * @throws HttpException
     */
    @Test()
    @DisplayName("Comprobar unicidad de código de asistencia")
    public void comprobarUnicidadCodigoAsistencia() throws HttpException {
        cursoPostDTO.setCodigoAsistencia("123ABC");
        CursoGetDTO cursoRegistrado = cursoService.registrarCurso(cursoPostDTO);
        assertThrows(HttpException.class, () -> {
            cursoService.registrarCurso(cursoPostDTO);
        });

        cursoService.eliminarCurso(cursoRegistrado.getId());
        cursoPostDTO.setCodigoAsistencia(null);
    }

    /**
     * Test para obtener un curso por término de búsqueda
     * @throws HttpException
     */
    /* 
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
*/
    @Test()
    @DisplayName("Obtener cursos por id de usuario")
    @Disabled
    public void obtenerCursosPorIdUsuario() throws ModelException {
        throw new ModelException("Not tested");
    }

    @Test()
    @DisplayName("Generar código de asistencia de curso")
    public void generarCodigoAsistencia() {
        String codigo = cursoService.generarCodigoAsistencia();
        assertNotNull(codigo);
        assertEquals(6, codigo.length());
        assertThrows(HttpException.class, () -> {
            cursoService.obtenerCursoPorCodigoAsistencia(codigo);
        });
    }
}
