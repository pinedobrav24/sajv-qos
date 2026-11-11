package pe.edu.sajv.sajv_qos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RouterConexionRequestDTO(

    @JsonProperty("equipo_id")
    Integer equipoId,

    String hostname,
    String ip,
    Integer puerto,
    String usuario,
    String contrasena,
    String fabricante,
    String modelo

) {}