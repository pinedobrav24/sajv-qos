package pe.edu.sajv.sajv_qos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@Table (name="sede")
public class Sede {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name="sedId")
    private Integer id;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Column(name = "sedDescripcion", nullable = false, length = 100)
    private String descripcion;
    
    @Column(name = "sedDireccion", length = 200)
    private String direccion;
    
    @Column(name = "sedEstado")
    private Integer estado;
}


