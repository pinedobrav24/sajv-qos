package pe.edu.sajv.sajv_qos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record QosOperacionManualDTO(

    Integer audienciaId,

    @NotNull
    @Positive
    Integer equipoId,

    @NotNull
    @Positive
    Integer politicaId,

    @NotBlank
    @Size(max = 500)
    String motivo

) {}