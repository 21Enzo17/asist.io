package asist.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IAsistenciaService;
import asist.io.service.ICursoService;
import asist.io.service.IEstudianteService;
import asist.io.service.IHorarioService;
import asist.io.service.IUsuarioService;
import asist.io.util.Constantes;
import asist.io.util.DateFormatter;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class AsistenciaTest {

    @Autowired
    private IAsistenciaService target;
    @Autowired
    private ICursoService cursoService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IEstudianteService estudianteService;
    @Autowired
    private IHorarioService horarioService;
    
    static UsuarioPostDTO usuarioPostDTO;
    static UsuarioGetDTO usuarioGetDTO;
    static CursoPostDTO cursoPostDTO;
    static CursoGetDTO cursoGetDTO;
    static AsistenciaPostDTO asistenciaPostDTO;
    static AsistenciaGetDTO asistenciaGetDTO;
    static EstudiantePostDTO estudiante1;
    static EstudiantePostDTO estudiante2;
    static EstudianteGetDTO estudianteGetDTO1;
    static EstudianteGetDTO estudianteGetDTO2;
    static HorarioPostDTO horarioPostDTO;

    static String fechaInicio;
    static String fechaFin;



    @BeforeEach
    public void setUp(){
        usuarioPostDTO = new UsuarioPostDTO();
        usuarioPostDTO.setNombre("Usuario de prueba");
        usuarioPostDTO.setCorreo("fenix.meneghini@hotmail.com");
        usuarioPostDTO.setContrasena("contraseña.1");
        usuarioService.guardarUsuario(usuarioPostDTO);

        cursoPostDTO = new CursoPostDTO();
        cursoPostDTO.setCodigoAsistencia("1234");
        cursoPostDTO.setNombre("Curso de prueba");
        cursoPostDTO.setDescripcion("Curso de prueba");
        cursoPostDTO.setCarrera("Ingeniería en Sistemas de Información");
        cursoPostDTO.setIdUsuario(usuarioService.buscarUsuario(usuarioPostDTO.getCorreo()).getId());
        cursoGetDTO = cursoService.registrarCurso(cursoPostDTO);

        estudiante1 = new EstudiantePostDTO();
        estudiante1.setLu("1234");
        estudiante1.setNombre("Estudiante de prueba 1");
        estudiante1.setCursoId(cursoGetDTO.getId());

        estudiante2 = new EstudiantePostDTO();
        estudiante2.setLu("5678");
        estudiante2.setNombre("Estudiante de prueba 2");
        estudiante2.setCursoId(cursoGetDTO.getId());

        estudianteGetDTO1 = estudianteService.registrarEstudiante(estudiante1);
        estudianteGetDTO2 = estudianteService.registrarEstudiante(estudiante2);


        horarioPostDTO = new HorarioPostDTO();
        horarioPostDTO.setCursoId(cursoGetDTO.getId());
        horarioPostDTO.setDia(Constantes.DIAS_DE_LA_SEMANA_INVERSO.get(LocalDate.now().getDayOfWeek()));
        horarioPostDTO.setEntrada(LocalTime.now());
        horarioPostDTO.setSalida(LocalTime.now().plusHours(2));
        horarioService.registrarHorario(horarioPostDTO);

        asistenciaPostDTO = new AsistenciaPostDTO();
        asistenciaPostDTO.setCodigoAsistencia("1234");
        asistenciaPostDTO.setLu("1234");

        fechaInicio = DateFormatter.localDateToString(LocalDate.now());
        fechaFin = DateFormatter.localDateToString(LocalDate.now().plusDays(1));

        
    }

    @AfterEach
    public void tearDown(){
        usuarioService.eliminarUsuario(usuarioPostDTO.getCorreo(), "contraseña.1");

        usuarioPostDTO = null;
        usuarioGetDTO = null;
        cursoPostDTO = null;
        cursoGetDTO = null;
        asistenciaPostDTO = null;
        asistenciaGetDTO = null;
        estudiante1 = null;
        estudiante2 = null;
    }

    @Test
    @DisplayName("Test de registro de asistencia")
    public void registrarAsistenciaTest(){
        asistenciaPostDTO = new AsistenciaPostDTO();
        asistenciaPostDTO.setCodigoAsistencia("151511155");
        asistenciaPostDTO.setLu("123122312312312");

        // Se comprueba que no se permite registrar una asistencia si el curso no existe
        assertThrows( ModelException.class, () -> target.registrarAsistencia(asistenciaPostDTO));
        asistenciaPostDTO.setCodigoAsistencia(cursoPostDTO.getCodigoAsistencia());
        // Se comprueba que no se permite registrar una asistencia si el estudiante no existe
        assertThrows( ModelException.class, () -> target.registrarAsistencia(asistenciaPostDTO));
        asistenciaPostDTO.setLu(estudiante1.getLu());
        // Se comprueba que se permite registrar una asistencia si el curso y el estudiante existen y el estudiante esta inscripto al curso 
        assertNotNull(target.registrarAsistencia(asistenciaPostDTO));
    }

    @Test
    @DisplayName("Test de obtener asistencia por curso")
    public void obtenerAsistenciaPorCursoTest(){
        target.registrarAsistencia(asistenciaPostDTO);
        asistenciaPostDTO.setLu(estudiante2.getLu());
        target.registrarAsistencia(asistenciaPostDTO);
        // Se comprueba que no se permite obtener asistencias si el curso no existe
        assertThrows( ModelException.class, () -> target.obtenerAsistenciaPorCursoYPeriodo("123123131", fechaInicio, fechaFin));
        // Se comprueba que se permite obtener asistencias si el curso existe
        List<List<Object>> asistencias = target.obtenerAsistenciaPorCursoYPeriodo(cursoGetDTO.getId(), fechaInicio, fechaFin);
        Long cantidad = asistencias.stream()
        .flatMap(fila -> fila.subList(1, fila.size()).stream())  // Convierte la lista de listas en un stream de objetos
        .filter(o -> o instanceof Boolean && (Boolean) o)  // Filtra los valores que son `true`
        .count();  // Cuenta los valores `true`
        assertEquals(2, cantidad);
}

    @Test
    @DisplayName("Test de obtener asistencia por estudiante")
    public void obtenerAsistenciaPorEstudianteTest(){
        target.registrarAsistencia(asistenciaPostDTO);
        asistenciaPostDTO.setLu(estudiante2.getLu());
        target.registrarAsistencia(asistenciaPostDTO);
        // Se comprueba que no se permite obtener asistencias si el estudiante no existe
        assertThrows( ModelException.class, () -> target.obtenerAsistenciaPorLuCursoYPeriodo("123123131", cursoGetDTO.getId(), fechaInicio, fechaFin));
        // Se comprueba que no se permita obtener asistencias si el curso no existe
        assertThrows( ModelException.class, () -> target.obtenerAsistenciaPorLuCursoYPeriodo(estudiante1.getLu(), "123123131", fechaInicio, fechaFin));
        // Se comprueba que se permite obtener asistencias si el estudiante existe
        Long cantidad = target.obtenerAsistenciaPorLuCursoYPeriodo(estudiante1.getLu(), cursoGetDTO.getId(),fechaInicio, fechaFin).stream()
        .flatMap(fila -> fila.subList(1, fila.size()).stream())  // Convierte la lista de listas en un stream de objetos
        .filter(o -> o instanceof Boolean && (Boolean) o)  // Filtra los valores que son `true`
        .count();  // C
        assertEquals(1, cantidad);
    }



}
