package pe.edu.sajv.sajv_qos.dto;

import java.time.LocalDateTime;
import java.util.List;

public record MonitoreoResumenDTO(
        boolean modoSimulacion,
        boolean monitoreoTcpHabilitado,
        LocalDateTime consultadoEn,
        long equiposActivos,
        long equiposConectados,
        long equiposSinRespuesta,
        long politicasQosActivas,
        List<EquipoMonitoreoDTO> equipos) {

    public record EquipoMonitoreoDTO(
            Integer id,
            String nombre,
            String ip,
            Integer puerto,
            String sede,
            String estadoEquipo,
            Long latenciaMs,
            String estadoQos,
            String ultimaAccion,
            LocalDateTime ultimaOperacion,
            String detalle) {
    }
}
