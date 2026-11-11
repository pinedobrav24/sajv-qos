package pe.edu.sajv.sajv_qos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioForm {
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 80, message = "Máximo 80 caracteres")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Máximo 100 caracteres")
    private String apellidos;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 4, max = 20, message = "El usuario debe tener entre 4 y 20 caracteres")
    private String usuario;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo válido")
    @Size(max = 50, message = "Máximo 50 caracteres")
    private String correo;

    // Obligatoria al crear; opcional al editar.
    @Size(max = 100, message = "La contraseña no puede superar 100 caracteres")
    private String contrasena;

    @NotNull(message = "Seleccione un perfil")
    private Integer rol;

    private Integer sedeId;
}
