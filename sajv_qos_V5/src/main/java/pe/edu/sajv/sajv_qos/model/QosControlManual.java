package pe.edu.sajv.sajv_qos.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
    name = "qos_control_manual",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_qcm_equipo_politica",
            columnNames = {"qcmEquId", "qcmPolId"}
        )
    }
)
public class QosControlManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qcmId")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "qcmEquId", referencedColumnName = "equId")
    private EquipoRed equipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "qcmPolId", referencedColumnName = "polId")
    private PoliticaQos politica;

    @Enumerated(EnumType.STRING)
    @Column(name = "qcmModo", nullable = false, length = 25)
    private QosModoControl modo = QosModoControl.AUTOMATICO;

    @Column(name = "qcmMotivo", length = 500)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qcmUsuId", referencedColumnName = "usuId")
    private Usuario usuario;

    @Column(name = "qcmFechaInicio")
    private LocalDateTime fechaInicio;

    @Column(name = "qcmFechaFin")
    private LocalDateTime fechaFin;

    @Column(name = "qcmActivo", nullable = false)
private Integer activo = 0;

    @Column(name = "qcmFechaActualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    @PreUpdate
    public void actualizarFecha() {
        fechaActualizacion = LocalDateTime.now();
    }
}