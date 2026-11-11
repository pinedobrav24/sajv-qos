package pe.edu.sajv.sajv_qos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "politica_qos")
public class PoliticaQos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "polId")
    private Integer id;

    @Column(name = "polCodigo", nullable = false, length = 50)
    private String codigo;

    @Column(name = "polNombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "polDescripcion", nullable = false, length = 200)
    private String descripcion;

    // Valor DSCP configurado en el catálogo. No implica que el router marque DSCP.
    @Column(name = "polDscp", nullable = false)
    private Integer dscp;

    @Column(name = "polAnchoBanda", nullable = false)
    private Integer anchoBanda;

    @Column(name = "polEstado", nullable = false)
    private Integer estado;

    @Column(name = "polClassMap", length = 100)
    private String classMap;

    @Column(name = "polPolicyMap", length = 100)
    private String policyMap;

    @Column(name = "polDireccion", nullable = false, length = 10)
    private String direccion;
    
}
