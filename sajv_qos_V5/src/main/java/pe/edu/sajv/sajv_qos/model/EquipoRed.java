package pe.edu.sajv.sajv_qos.model;

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
@Table(name = "equipo_red")
public class EquipoRed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equId")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equSedId", referencedColumnName = "sedId", nullable = false)
    private Sede sede;

    @Column(name = "equNombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "equHostname", nullable = false, length = 100)
    private String hostname;

    @Column(name = "equIP", nullable = false, length = 45)
    private String ip;

    @Column(name = "equPuerto", nullable = false)
    private Integer puerto;

    @Column(name = "equUsuario", nullable = false, length = 100)
    private String usuario;

    // Se almacena cifrada (AES-GCM); nunca guardar la contraseña en texto plano.
    @Column(name = "equContrasena", length = 512)
    private String contrasena;

    @Column(name = "equInterfazQos", nullable = false, length = 50)
    private String interfazQos;

    @Column(name = "equFabricante", nullable = false, length = 50)
    private String fabricante;

    @Column(name = "equModelo", nullable = false, length = 50)
    private String modelo;

    @Column(name = "equEstado", nullable = false)
    private Integer estado;
}
