package pe.edu.sajv.sajv_qos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QosOperacionRequestDTO(

    @JsonProperty("audiencia_id")
    Integer audienciaId,

    @JsonProperty("equipo_id")
    Integer equipoId,

    String hostname,

    String ip,

    Integer puerto,

    String usuario,

    String contrasena,

    @JsonProperty("contrasena_enable")
    String contrasenaEnable,

    String interfaz,

    @JsonProperty("class_map")
    String classMap,

    @JsonProperty("policy_map")
    String policyMap,

    String direccion

) {}