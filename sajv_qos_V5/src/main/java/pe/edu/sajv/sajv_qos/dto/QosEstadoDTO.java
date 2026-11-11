
package pe.edu.sajv.sajv_qos.dto;

public record QosEstadoDTO(
        Integer audienciaId,
        Integer equipoId,
        Integer politicaId,
        String estado,
        Integer ultimaOperacionId,
        String modo
) {
}
