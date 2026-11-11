
package pe.edu.sajv.sajv_qos.dto;

import java.time.LocalDate;
import java.util.List;

public record DashboardResumenDTO(
    long audienciasHoy,
    long audienciasEnCurso,
    long audienciasProgramadas,
    long sedesActivas,
    long salasActivas,
    long equiposActivos,
    long politicasActivas,
    long operacionesExitosasHoy,
    LocalDate fechaActual,
    List<AudienciaProximaDTO> proximasAudiencias,
    List<QosOperacionDTO> operacionesRecientes
) {

    public record AudienciaProximaDTO(
        Integer id,
        LocalDate fecha,
        String horaInicio,
        String horaFin,
        String salaNombre,
        String sedeNombre,
        String responsable,
        Integer situacion
    ) {}

}
