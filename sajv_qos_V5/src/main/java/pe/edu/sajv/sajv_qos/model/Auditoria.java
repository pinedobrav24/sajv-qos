package pe.edu.sajv.sajv_qos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audId")
    private Integer id;

    @Column(name = "audUsuarioId")
    private Integer usuarioId;

    @Column(name = "audUsuario", nullable = false, length = 50)
    private String usuario;

    @Column(name = "audModulo", nullable = false, length = 50)
    private String modulo;

    @Column(name = "audAccion", nullable = false, length = 50)
    private String accion;

    @Column(name = "audDescripcion", length = 500)
    private String descripcion;

    @Column(name = "audIp", length = 45)
    private String ip;

    @Column(name = "audFecha", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void antesDeGuardar() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }
}
