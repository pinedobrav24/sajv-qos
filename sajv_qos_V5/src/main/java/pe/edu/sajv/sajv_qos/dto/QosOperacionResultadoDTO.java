package pe.edu.sajv.sajv_qos.dto;

import java.time.LocalDateTime;

public record QosOperacionResultadoDTO(
    Integer id,
    Integer audienciaId,
    Integer equipoId,
    Integer politicaId,
    Integer accion,
    Integer origen,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    Integer resultado,
    String mensaje,
    String salida
) {}