
package pe.edu.sajv.sajv_qos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "sala")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salId")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "salSedId",
        referencedColumnName = "sedId",
        nullable = false
    )
    private Sede sede;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 100, message = "Máximo 100 caracteres")
    @Column(name = "salDescripcion", nullable = false, length = 100)
    private String descripcion;

    @Column(name = "salEstado")
    private Integer estado;
}
