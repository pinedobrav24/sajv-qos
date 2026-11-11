package pe.edu.sajv.sajv_qos.dto;

public record EquipoRedDTO(
    Integer id,
    Integer sedeId,
    String sede,
    String nombre,
    String hostname,
    String ip,
    Integer puerto,
    String usuario,
    String interfazQos,
    String fabricante,
    String modelo,
    Integer estado
) {}
