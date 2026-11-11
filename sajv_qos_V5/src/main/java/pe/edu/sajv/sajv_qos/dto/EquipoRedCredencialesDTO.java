package pe.edu.sajv.sajv_qos.dto;

import jakarta.validation.constraints.NotBlank;

public record EquipoRedCredencialesDTO(

    @NotBlank
    String usuario,

    @NotBlank
    String contrasena

) {}