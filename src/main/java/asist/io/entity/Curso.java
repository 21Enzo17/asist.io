package asist.io.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "cursos")
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    @Column(name = "descripcion", nullable = false)
    private String descripcion;
    @Column(name = "carrera", nullable = false)
    private String carrera;
    @Column(name = "codigo_asistencia", nullable = true, unique = true)
    private String codigoAsistencia;
    @ManyToOne()
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Estudiante> estudiantes;


    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy = "curso")
    private List<Horario> horario;
}