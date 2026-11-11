
package pe.edu.sajv.sajv_qos.dto;

import java.time.LocalDateTime;

public record AudienciaCalendarioDTO(
    Integer id,
    String title,
    LocalDateTime start,
    LocalDateTime end,
    String expediente,
    String sala,
    String sede,
    String responsable,
    Integer estado,
    Integer situacion,
    Integer salaId,
    Integer sedeId,
    String coHost,
    Integer tipo,
    String comentarios
) {}
