package pe.edu.sajv.sajv_qos.dto;

public record PoliticaQosDTO(
    Integer id,
    String codigo,
    String nombre,
    String descripcion,
    Integer dscp,
    Integer anchoBanda,
    Integer estado
) {}
