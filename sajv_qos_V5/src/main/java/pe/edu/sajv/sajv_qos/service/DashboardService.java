
package pe.edu.sajv.sajv_qos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.dto.DashboardResumenDTO;
import pe.edu.sajv.sajv_qos.dto.QosOperacionDTO;
import pe.edu.sajv.sajv_qos.model.Audiencia;
import pe.edu.sajv.sajv_qos.model.QosOperacion;
import pe.edu.sajv.sajv_qos.repository.AudienciaRepository;
import pe.edu.sajv.sajv_qos.repository.EquipoRedRepository;
import pe.edu.sajv.sajv_qos.repository.PoliticaQosRepository;
import pe.edu.sajv.sajv_qos.repository.QosOperacionRepository;
import pe.edu.sajv.sajv_qos.repository.SalaRepository;
import pe.edu.sajv.sajv_qos.repository.SedeRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final ZoneId ZONA_LIMA =
        ZoneId.of("America/Lima");

    private static final int ACTIVO = 1;
    private static final int EN_CURSO = 2;
    private static final int PROGRAMADA = 1;
    private static final int EXITOSO = 1;

    private final AudienciaRepository audienciaRepository;
    private final SedeRepository sedeRepository;
    private final SalaRepository salaRepository;
    private final EquipoRedRepository equipoRepository;
    private final PoliticaQosRepository politicaRepository;
    private final QosOperacionRepository operacionRepository;


    // =====================================================
    // RESUMEN PRINCIPAL DEL DASHBOARD
    // =====================================================

    public ResumenDashboard obtenerResumen() {

        LocalDate hoy = LocalDate.now(ZONA_LIMA);
        LocalTime horaActual = LocalTime.now(ZONA_LIMA);

        LocalDateTime inicioDia = hoy.atStartOfDay();

        // Próximas audiencias: únicamente las que todavía
        // no han comenzado.
        List<Audiencia> proximas = audienciaRepository
            .buscarProximasAudiencias(
                ACTIVO,
                hoy,
                horaActual
            )
            .stream()
            .limit(6)
            .toList();

        // Últimas operaciones QoS.
        List<QosOperacionDTO> operaciones = operacionRepository
            .findTop100ByOrderByFechaInicioDesc()
            .stream()
            .limit(8)
            .map(this::convertirOperacion)
            .toList();

        // Contadores generales.
        long audienciasHoy =
            audienciaRepository.countByEstadoAndFecha(
                ACTIVO,
                hoy
            );

        long audienciasEnCurso =
            audienciaRepository.countByEstadoAndSituacionAndFecha(
                ACTIVO,
                EN_CURSO,
                hoy
            );

        long audienciasProgramadas =
            audienciaRepository.contarAudienciasProgramadas(
                ACTIVO,
                hoy,
                horaActual
            );

        long sedesActivas =
            sedeRepository.countByEstado(ACTIVO);

        long salasActivas =
            salaRepository.countByEstado(ACTIVO);

        long equiposActivos =
            equipoRepository.countByEstado(ACTIVO);

        long politicasActivas =
            politicaRepository.countByEstado(ACTIVO);

        long operacionesExitosasHoy =
            operacionRepository.countByResultadoAndFechaInicioGreaterThanEqual(
                EXITOSO,
                inicioDia
            );

        return new ResumenDashboard(
            audienciasHoy,
            audienciasEnCurso,
            audienciasProgramadas,
            sedesActivas,
            salasActivas,
            equiposActivos,
            politicasActivas,
            operacionesExitosasHoy,
            proximas,
            operaciones,
            hoy,
            horaActual
        );
    }


    // =====================================================
    // RESUMEN PARA ACTUALIZACIÓN AUTOMÁTICA (REST API)
    // =====================================================

    public DashboardResumenDTO obtenerResumenApi() {

        ResumenDashboard resumen = obtenerResumen();

        // Convertir entidades Audiencia a DTO.
        List<DashboardResumenDTO.AudienciaProximaDTO> proximas =
            resumen.proximasAudiencias()
                .stream()
                .map(a -> new DashboardResumenDTO.AudienciaProximaDTO(
                    a.getId(),
                    a.getFecha(),
                    a.getHoraInicio() == null
                        ? null
                        : a.getHoraInicio().toString(),
                    a.getHoraFin() == null
                        ? null
                        : a.getHoraFin().toString(),
                    a.getSala().getDescripcion(),
                    a.getSala().getSede().getDescripcion(),
                    a.getResponsable(),
                    a.getSituacion()
                ))
                .toList();

        return new DashboardResumenDTO(
            resumen.audienciasHoy(),
            resumen.audienciasEnCurso(),
            resumen.audienciasProgramadas(),
            resumen.sedesActivas(),
            resumen.salasActivas(),
            resumen.equiposActivos(),
            resumen.politicasActivas(),
            resumen.operacionesExitosasHoy(),
            resumen.fechaActual(),
            proximas,
            resumen.operacionesRecientes()
        );
    }


    // =====================================================
    // CONVERSIÓN DE OPERACIONES QOS
    // =====================================================

    private QosOperacionDTO convertirOperacion(QosOperacion o) {

        return new QosOperacionDTO(
            o.getId(),
            o.getAudiencia() == null
                ? null
                : o.getAudiencia().getId(),
            o.getEquipo().getId(),
            o.getEquipo().getNombre(),
            o.getPolitica().getId(),
            o.getPolitica().getNombre(),
            o.getAccion(),
            o.getOrigen(),
            o.getFechaInicio(),
            o.getFechaFin(),
            o.getResultado(),
            o.getMensaje(),
            null
        );
    }


    // =====================================================
    // RECORD DEL DASHBOARD
    // =====================================================

    public record ResumenDashboard(

        long audienciasHoy,

        long audienciasEnCurso,

        long audienciasProgramadas,

        long sedesActivas,

        long salasActivas,

        long equiposActivos,

        long politicasActivas,

        long operacionesExitosasHoy,

        List<Audiencia> proximasAudiencias,

        List<QosOperacionDTO> operacionesRecientes,

        LocalDate fechaActual,

        LocalTime horaActual

    ) {}

}
