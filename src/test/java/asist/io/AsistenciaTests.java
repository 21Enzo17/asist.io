package asist.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import asist.io.dto.asistenciaDTO.AsistenciaGetDTO;
import asist.io.dto.asistenciaDTO.AsistenciaPostDTO;
import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.estudianteDTO.EstudianteGetDTO;
import asist.io.dto.estudianteDTO.EstudiantePostDTO;
import asist.io.dto.inscripcionDTO.InscripcionPostDTO;
import asist.io.dto.passwordDTO.PasswordDTO;
import asist.io.dto.usuarioDTO.UsuarioGetDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.IAsistenciaService;
import asist.io.service.ICursoService;
import asist.io.service.IEstudianteService;
import asist.io.service.IInscripcionService;
import asist.io.service.IUsuarioService;
import asist.io.util.DateFormatter;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class AsistenciaTests {

    @Autowired
    private IAsistenciaService target;
    @Autowired
    private ICursoService cursoService;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IEstudianteService estudianteService;
    @Autowired
    private IInscripcionService inscripcionService;
    
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
    static InscripcionPostDTO inscripcionPostDTO1;
    static InscripcionPostDTO inscripcionPostDTO2;


    @BeforeEach
    public void setUp(){
        usuarioPostDTO = new UsuarioPostDTO();
        usuarioPostDTO.setNombre("Usuario de prueba");
        usuarioPostDTO.setCorreo("enzo.meneghini@hotmail.com");
        usuarioPostDTO.setContrasena(new PasswordDTO("contraseña.1"));
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

        estudiante2 = new EstudiantePostDTO();
        estudiante2.setLu("5678");
        estudiante2.setNombre("Estudiante de prueba 2");

        estudianteGetDTO1 = estudianteService.registrarEstudiante(estudiante1);
        estudianteGetDTO2 = estudianteService.registrarEstudiante(estudiante2);

        inscripcionPostDTO1 = new InscripcionPostDTO();
        inscripcionPostDTO1.setIdCurso(cursoGetDTO.getId());
        inscripcionPostDTO1.setIdEstudiante(estudianteGetDTO1.getId());

        inscripcionPostDTO2 = new InscripcionPostDTO();
        inscripcionPostDTO2.setIdCurso(cursoGetDTO.getId());
        inscripcionPostDTO2.setIdEstudiante(estudianteGetDTO2.getId());

        asistenciaPostDTO = new AsistenciaPostDTO();
        asistenciaPostDTO.setCodigoAsistencia("1234");
        asistenciaPostDTO.setLu("1234");

        
    }

    @AfterEach
    public void tearDown(){
        usuarioService.eliminarUsuario(usuarioPostDTO.getCorreo());
        cursoService.eliminarCurso(cursoGetDTO.getId());
        estudianteService.eliminarEstudiante(estudiante1.getLu());
        estudianteService.eliminarEstudiante(estudiante2.getLu());
        

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
        // Se comprueba que no se permite registrar una asisencia si el estudiante no esta inscripto al curso
        assertThrows( ModelException.class, () -> target.registrarAsistencia(asistenciaPostDTO));
        inscripcionService.registrarInscripcion(inscripcionPostDTO1);
        // Se comprueba que se permite registrar una asistencia si el curso y el estudiante existen y el estudiante esta inscripto al curso 
        assertNotNull(target.registrarAsistencia(asistenciaPostDTO));
    }

    @Test
    @DisplayName("Test de obtener asistencia por curso")
    public void obtenerAsistenciaPorCursoTest(){
        inscripcionService.registrarInscripcion(inscripcionPostDTO1);
        inscripcionService.registrarInscripcion(inscripcionPostDTO2);
        target.registrarAsistencia(asistenciaPostDTO);
        asistenciaPostDTO.setLu(estudiante2.getLu());
        target.registrarAsistencia(asistenciaPostDTO);
        // Se comprueba que no se permite obtener asistencias si el curso no existe
        assertThrows( ModelException.class, () -> target.obtenerAsistenciaPorCurso("123123131"));
        // Se comprueba que se permite obtener asistencias si el curso existe
        assertEquals(2, target.obtenerAsistenciaPorCurso(cursoGetDTO.getId()).size());
    }

    @Test
    @DisplayName("Test de obtener asistencia por estudiante")
    public void obtenerAsistenciaPorEstudianteTest(){
        inscripcionService.registrarInscripcion(inscripcionPostDTO1);
        inscripcionService.registrarInscripcion(inscripcionPostDTO2);
        target.registrarAsistencia(asistenciaPostDTO);
        asistenciaPostDTO.setLu(estudiante2.getLu());
        target.registrarAsistencia(asistenciaPostDTO);
        // Se comprueba que no se permite obtener asistencias si el estudiante no existe
        assertThrows( ModelException.class, () -> target.obtenerAsistenciaPorLuYCurso("123123131", cursoGetDTO.getId()));
        // Se comprueba que no se permita obtener asistencias si el curso no existe
        assertThrows( ModelException.class, () -> target.obtenerAsistenciaPorLuYCurso(estudiante1.getLu(), "123123131"));
        // Se comprueba que se permite obtener asistencias si el estudiante existe
        assertEquals(1, target.obtenerAsistenciaPorLuYCurso(estudiante1.getLu(), cursoGetDTO.getId()).size());
    }

    @Test
    @DisplayName("Test de obtener asistencia por periodo y curso")
    public void obtenerAsistenciaPorPeriodoYCursoTest(){
        inscripcionService.registrarInscripcion(inscripcionPostDTO1);
        inscripcionService.registrarInscripcion(inscripcionPostDTO2);
        target.registrarAsistencia(asistenciaPostDTO);
        asistenciaPostDTO.setLu(estudiante2.getLu());
        target.registrarAsistencia(asistenciaPostDTO);
        // Se comprueba que no se permite obtener asistencias si el curso no existe
        assertThrows( ModelException.class, () -> target.obtenerAsistenciaPorPeriodoYCurso("01/01/2021", "01/01/2021", "123123131"));
        // Se comprueba que no se permita obtener asistencias si las fecha de inicio es posterior a la fecha de fin
        assertThrows( ModelException.class, () -> target.obtenerAsistenciaPorPeriodoYCurso("02/01/2021", "01/01/2021", cursoGetDTO.getId()));
        // Se comprueba un periodo sin asistencias
        assertEquals(0, target.obtenerAsistenciaPorPeriodoYCurso("01/01/2021", "02/01/2021", cursoGetDTO.getId()).size());
        // Se comprueba un periodo con asistencias
        assertEquals(2, target.obtenerAsistenciaPorPeriodoYCurso(DateFormatter.localDateToString(LocalDate.now()), DateFormatter.localDateToString(LocalDate.now().plusDays(1)), cursoGetDTO.getId()).size());
    }

}
