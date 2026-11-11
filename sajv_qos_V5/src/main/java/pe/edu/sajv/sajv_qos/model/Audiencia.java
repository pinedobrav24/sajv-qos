package pe.edu.sajv.sajv_qos.model;

import java.time.LocalDate;
import java.time.LocalTime;

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

@Entity
@Getter 
@Setter     
@Table(name = "audiencavirt")
public class Audiencia {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audId")
    private Integer id;

    @Column(name = "audNroExpediente", length = 20)
    private String nroExpediente;

    @JoinColumn(name = "audSalaId", referencedColumnName = "salId")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Sala sala;

    @Column(name = "audResponsable", length = 80)
    private String responsable;

    @Column(name = "audCoHost", length = 80)
    private String coHost;

    @Column(name = "audFecha")
    private LocalDate fecha;

    @Column(name = "audHrInicio")
    private LocalTime horaInicio;

    @Column(name = "audHrFin")
    private LocalTime horaFin;

    @Column(name = "audTipo")
    private Integer tipo;

    //1-programada / 2-en curso / 3-finalizada / 4-cancelada/ 5-suspendida
    @Column (name = "audSituacion")
    private Integer situacion=1;

    @Column(name = "audComentarios", length = 200)
    private String comentarios;

    @JoinColumn(name = "audUsuId", referencedColumnName = "usuId")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Usuario usuario;

    @Column(name = "audEstado")
    private Integer estado;
}
