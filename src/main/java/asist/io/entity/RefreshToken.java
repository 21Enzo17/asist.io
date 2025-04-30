package asist.io.entity;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad que representa un token de refresco (refresh token) en la base de datos.
 * Se utiliza para generar nuevos tokens de acceso cuando el token original expira,
 * sin necesidad de que el usuario tenga que autenticarse nuevamente.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    /**
     * Identificador único del token de refresco.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Valor del token de refresco.
     */
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    /**
     * Usuario al que pertenece este token de refresco.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Usuario usuario;

    /**
     * Fecha y hora de caducidad del token de refresco.
     */
    @Column(name = "fecha_expiracion", nullable = false)
    private Instant fechaExpiracion;

    /**
     * Indica si el token ha sido revocado. Un token revocado ya no es válido
     * aunque no haya expirado aún.
     */
    @Column(name = "revocado", nullable = false)
    private boolean revocado = false;
    
    /**
     * Comprueba si el token ha expirado.
     * 
     * @return true si el token ha expirado, false en caso contrario.
     */
    public boolean esExpirado() {
        return fechaExpiracion.isBefore(Instant.now());
    }
    
    /**
     * Comprueba si el token es válido.
     * 
     * @return true si el token no ha expirado y no ha sido revocado, false en caso contrario.
     */
    public boolean esValido() {
        return !esExpirado() && !revocado;
    }
}