package asist.io;

import asist.io.dto.CursoGetDTO;
import asist.io.dto.CursoPatchDTO;
import asist.io.dto.CursoPostDTO;
import asist.io.exception.ModelException;
import asist.io.mapper.CursoMapper;
import asist.io.service.ICursoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CursoServiceTest {
    @Autowired
    private ICursoService cursoService;
    CursoPostDTO cursoPostDTO;
    CursoPatchDTO cursoPatchDTO;
    CursoGetDTO cursoGetDTO;

    @BeforeEach
    public void setup() {
        cursoPostDTO = new CursoPostDTO();
        cursoPostDTO.setNombre("Curso de prueba");
        cursoPostDTO.setDescripcion("Curso de prueba");
        cursoPostDTO.setCarrera("Ingeniería en Sistemas");

        cursoPatchDTO = new CursoPatchDTO();

        cursoGetDTO = new CursoGetDTO();
    }

    @AfterEach
    public void tearDown() {
        cursoPostDTO = null;
        cursoPatchDTO = null;
        cursoGetDTO = null;
    }

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

    @Test()
    @DisplayName("Obtener curso por id")
    public void obtenerCursoPorId() throws ModelException {
        CursoGetDTO expected = cursoService.registrarCurso(cursoPostDTO);
        CursoGetDTO result = cursoService.obtenerCursoPorId(expected.getId());

        assertEquals(expected.getId(), result.getId());

        cursoService.eliminarCurso(expected.getId());
    }



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
}
