package pe.edu.sajv.sajv_qos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QosApiResponseDTO(

    boolean exitoso,
    String modo,
    String accion,

    @JsonProperty("audiencia_id")
    Integer audienciaId,

    @JsonProperty("equipo_id")
    Integer equipoId,

    String hostname,
    String interfaz,

    @JsonProperty("class_map")
    String classMap,

    @JsonProperty("policy_map")
    String policyMap,

    String mensaje,

    @JsonProperty("cambio_realizado")
    boolean cambioRealizado,

    String salida
) {}