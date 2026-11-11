package pe.edu.sajv.sajv_qos.dto;

import java.time.LocalDateTime;

public record QosOperacionDTO(
    Integer id,
    Integer audienciaId,
    Integer equipoId,
    String equipoNombre,
    Integer politicaId,
    String politicaNombre,
    Integer accion,
    Integer origen,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    Integer resultado,
    String mensaje,
    String salida
) {}
