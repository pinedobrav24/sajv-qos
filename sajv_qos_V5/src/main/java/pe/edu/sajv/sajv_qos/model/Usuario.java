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
@Table(name="usuario")
public class Usuario {
 @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuId")
    private Integer id;

    @Column(name = "usuNombre", length = 80)
    private String nombre;

    @Column(name = "usuApellidos", length = 100)
    private String apellidos;

    @Column(name = "usuUsuario", length = 20)
    private String usuario;

    @Column(name = "usuCorreo", length = 50)
    private String correo;

    @Column(name = "usuContrasena", length = 200)
    private String contrasena;

    @Column(name = "usuRol")
    private Integer rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuSedId")
    private Sede sede;

    @Column(name = "usuDebeCambiarContrasena", nullable = false)
    private Integer debeCambiarContrasena = 0;

    @Column(name = "usuEstado")
    private Integer estado;

}
