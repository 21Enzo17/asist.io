package asist.io;

import asist.io.entity.Curso;
import asist.io.exception.ModelException;
import asist.io.service.ICursoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CursoServiceTest {
    @Autowired
    private ICursoService cursoService;
    Curso curso;

    @BeforeEach
    public void setup() {
        curso = new Curso();
        curso.setNombre("Curso de prueba");
        curso.setDescripcion("Curso de prueba");
        curso.setCarrera("Ingeniería en Sistemas");
    }

    @AfterEach
    public void tearDown() {
        curso = null;
    }

    @Test()
    @DisplayName("Registrar curso")
    public void registrarCursoTest() throws ModelException {
        Curso cursoRegistrado = cursoService.registrarCurso(curso);
        assertNotNull(cursoRegistrado);

        assertThrows(ModelException.class, () -> {
            cursoService.registrarCurso(cursoRegistrado);
        });

        cursoService.eliminarCurso(cursoRegistrado.getId());
    }

    @Test()
    @DisplayName("Eliminar curso")
    public void eliminarCursoTest() throws ModelException {
        Curso cursoRegistrado = cursoService.registrarCurso(curso);
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
        Curso cursoRegistrado = cursoService.registrarCurso(curso);
        String nuevoNombre = "Curso de prueba actualizado";
        cursoRegistrado.setNombre(nuevoNombre);
        Curso cursoActualizado = cursoService.actualizarCurso(cursoRegistrado);

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
            cursoService.actualizarCurso(new Curso());
        });

        assertThrows(ModelException.class, () -> {
            cursoService.actualizarCurso(curso);
        });
    }

    @Test()
    @DisplayName("Obtener curso por id")
    public void obtenerCursoPorId() throws ModelException {
        Curso expected = cursoService.registrarCurso(curso);
        Curso result = cursoService.obtenerCursoPorId(expected.getId());

        assertEquals(expected.getId(), result.getId());

        cursoService.eliminarCurso(expected.getId());
    }



    @Test()
    @DisplayName("Obtener curso por código de asistencia - Argumento inválido")
    public void obtenerCursoPorCodigoAsistenciaArgumentoInvalido() throws ModelException {
        assertThrows(ModelException.class, () -> {
            cursoService.ObtenerCursoPorCodigoAsistencia("");
        });

        assertThrows(ModelException.class, () -> {
            cursoService.ObtenerCursoPorCodigoAsistencia(null);
        });

        assertThrows(ModelException.class, () -> {
            cursoService.ObtenerCursoPorCodigoAsistencia("    ");
        });
    }

    @Test()
    @DisplayName("Obtener curso por código de asistencia - Argumento válido")
    public void obtenerCursoPorCodigoAsistenciaArgumentoValido() throws ModelException {
        curso.setCodigoAsistencia("123ABC");
        Curso expected = cursoService.registrarCurso(curso);
        Curso result = cursoService.ObtenerCursoPorCodigoAsistencia(expected.getCodigoAsistencia());

        assertEquals(expected.getId(), result.getId());

        cursoService.eliminarCurso(expected.getId());
        curso.setCodigoAsistencia(null);
    }

    @Test()
    @DisplayName("Comprobar unicidad de código de asistencia")
    public void comprobarUnicidadCodigoAsistencia() throws ModelException {
        curso.setCodigoAsistencia("123ABC");
        Curso cursoRegistrado = cursoService.registrarCurso(curso);
        assertThrows(ModelException.class, () -> {
            cursoService.registrarCurso(curso);
        });

        cursoService.eliminarCurso(cursoRegistrado.getId());
        curso.setCodigoAsistencia(null);
    }
}
