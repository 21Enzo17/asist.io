package asist.io;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import asist.io.dto.HorarioDTO.HorarioGetDTO;
import asist.io.dto.HorarioDTO.HorarioPatchDTO;
import asist.io.dto.HorarioDTO.HorarioPostDTO;
import asist.io.dto.cursoDTO.CursoGetDTO;
import asist.io.dto.cursoDTO.CursoPostDTO;
import asist.io.dto.usuarioDTO.UsuarioPostDTO;
import asist.io.exception.ModelException;
import asist.io.service.ICursoService;
import asist.io.service.IHorarioService;
import asist.io.service.IUsuarioService;
import asist.io.util.Constantes;
import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class HorarioTest {

    @Autowired
    private IHorarioService target;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private ICursoService cursoService;

    static UsuarioPostDTO usuarioPostDTO;

    static CursoPostDTO cursoPostDTO;
    static CursoGetDTO cursoGetDTO;

    static HorarioPostDTO horarioPostDTO1;
    static HorarioGetDTO horarioGetDTO1;

    static HorarioPostDTO horarioPostDTO2;
    static HorarioGetDTO horarioGetDTO2;

    static HorarioPatchDTO horarioPatchDTO;

    @BeforeEach
    public void setUp() {
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

        horarioPostDTO1 = new HorarioPostDTO();
        horarioPostDTO1.setDia(Constantes.DIAS_DE_LA_SEMANA_INVERSO.get(LocalDate.now().getDayOfWeek()));
        horarioPostDTO1.setEntrada(LocalTime.now());
        horarioPostDTO1.setSalida(LocalTime.now().plusHours(2));
        horarioPostDTO1.setCursoId(cursoGetDTO.getId());

        horarioPatchDTO = new HorarioPatchDTO();
        horarioPatchDTO.setEntrada(horarioPostDTO1.getEntrada().plusHours(1));
        horarioPatchDTO.setSalida(horarioPostDTO1.getSalida().plusHours(2));
        horarioPatchDTO.setDia(horarioPostDTO1.getDia());
        
        horarioPostDTO2 = new HorarioPostDTO();
        horarioPostDTO2.setDia(Constantes.DIAS_DE_LA_SEMANA_INVERSO.get(LocalDate.now().getDayOfWeek().plus(1)));
        horarioPostDTO2.setEntrada(LocalTime.now());
        horarioPostDTO2.setSalida(LocalTime.now().plusHours(2));
        horarioPostDTO2.setCursoId(cursoGetDTO.getId());

    }

    @AfterEach
    public void tearDown() {
        
        usuarioService.eliminarUsuario(usuarioPostDTO.getCorreo(),"contraseña.1");
        usuarioPostDTO = null;
        cursoPostDTO = null;
        cursoGetDTO = null;
        horarioPostDTO1 = null;


    }

    @Test
    @DisplayName("Registrar Horario")
    public void registrarHorario() {
        // Intentamos registrar un horario con un curso no existente
        horarioPostDTO1.setCursoId("15964484894899818");
        assertThrows(ModelException.class, () -> target.registrarHorario(horarioPostDTO1));
        // Intentamos registrar un horario con un curso existente, pero con horarios no validos
        horarioPostDTO1.setCursoId(cursoGetDTO.getId());
        horarioPostDTO1.setEntrada(LocalTime.now().plusHours(2));
        horarioPostDTO1.setSalida(LocalTime.now());
        assertThrows(ModelException.class, () -> target.registrarHorario(horarioPostDTO1));
        // Intentamos registrar un horario con un curso existente
        horarioPostDTO1.setEntrada(LocalTime.now());
        horarioPostDTO1.setSalida(LocalTime.now().plusHours(2));
        assertNotNull(target.registrarHorario(horarioPostDTO1));
        // Intentamos registrar un horario con un horario similar
        horarioPostDTO1.setEntrada(LocalTime.now().plusHours(1));
        horarioPostDTO1.setSalida(LocalTime.now().plusHours(3));
        assertThrows(ModelException.class, () -> target.registrarHorario(horarioPostDTO1));
    }

    @Test
    @DisplayName("Obtener Horario por Id")
    public void obtenerHorarioPorId() {
        horarioGetDTO1 = target.registrarHorario(horarioPostDTO1);
        assertNotNull(target.obtenerHorarioPorId(horarioGetDTO1.getHorarioId()));
    }

    @Test
    @DisplayName("Obtener Horarios por Curso")
    public void obtenerHorariosPorCurso() {
        target.registrarHorario(horarioPostDTO1);
        target.registrarHorario(horarioPostDTO2);
        assertEquals(2, target.obtenerHorariosPorCurso(cursoGetDTO.getId()).size());
    }

    @Test
    @DisplayName("Obtener Horario por LocalDateTime")
    public void obtenerHorarioPorLocalDateTime() {
        assertNotNull(target.registrarHorario(horarioPostDTO1));
        assertNotNull(target.obtenerHorarioPorLocalDateTime(cursoGetDTO.getCodigoAsistencia(), LocalDateTime.now()));
        assertThrows(ModelException.class, () -> target.obtenerHorarioPorLocalDateTime(cursoGetDTO.getId(), LocalDateTime.now().plusDays(2)));
    }
    
    @Test
    @DisplayName("Eliminar Horario")
    public void eliminarHorario() {
        horarioGetDTO1 = target.registrarHorario(horarioPostDTO1);
        assertNotNull(target.obtenerHorarioPorId(horarioGetDTO1.getHorarioId()));
        target.eliminarHorario(horarioGetDTO1.getHorarioId());
        assertThrows(ModelException.class, () -> target.obtenerHorarioPorId(horarioGetDTO1.getHorarioId()));
    }
    
    @Test
    @DisplayName("Actualizar Horario")
    public void actualizarHorario() {
        HorarioGetDTO horarioGetDTO = target.registrarHorario(horarioPostDTO1);
        target.registrarHorario(horarioPostDTO2);
        // Le asignamos valores no validos
        horarioPatchDTO.setHorarioId(horarioGetDTO.getHorarioId());
        horarioPatchDTO.setEntrada(horarioPostDTO2.getEntrada());
        horarioPatchDTO.setSalida(horarioPostDTO2.getSalida());
        horarioPatchDTO.setDia(horarioPostDTO2.getDia());
        assertThrows(ModelException.class, () -> target.actualizarHorario(horarioPatchDTO));
        // Le asignamos valores validos
        horarioPatchDTO.setEntrada(horarioPostDTO1.getEntrada().plusHours(1));
        horarioPatchDTO.setSalida(horarioPostDTO1.getSalida().plusHours(1));
        horarioPatchDTO.setDia(horarioPostDTO2.getDia().plus(1));
        assertNotNull(target.actualizarHorario(horarioPatchDTO));
    }

    

}
