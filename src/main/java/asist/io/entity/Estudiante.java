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
@Table(name = "estudiantes")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(name = "nombre", nullable = false)
    private String nombre;
    @Column(name = "lu", nullable = false)
    private String lu;


    @ManyToOne
    private Curso curso;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.REMOVE)
    private List<Asistencia> asistencias;
    
}