package pe.edu.sajv.sajv_qos.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import pe.edu.sajv.sajv_qos.model.Audiencia;

@Entity
@Getter
@Setter
@Table(name = "qos_operacion")
public class QosOperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qopId")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qopAudId", referencedColumnName = "audId")
    private Audiencia audiencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "qopEquId", referencedColumnName = "equId", nullable = false)
    private EquipoRed equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "qopPolId", referencedColumnName = "polId", nullable = false)
    private PoliticaQos politica;

    @Column(name = "qopAccion", nullable = false)
    private Integer accion;

    @Column(name = "qopOrigen", nullable = false)
    private Integer origen;

    @Column(name = "qopFecInicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "qopFecFin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "qopResultado", nullable = false)
    private Integer resultado;

    @Column(name = "qopMensaje", length = 500)
    private String mensaje;

    @Column(name = "qopSalida", columnDefinition = "TEXT")
    private String salida;

    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "qopUsuId", referencedColumnName = "usuId")
private Usuario usuario;

@Column(name = "qopMotivo", length = 500)
private String motivo;
}
