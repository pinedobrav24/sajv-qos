
package pe.edu.sajv.sajv_qos.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import pe.edu.sajv.sajv_qos.model.Audiencia;
import pe.edu.sajv.sajv_qos.model.EquipoRed;
import pe.edu.sajv.sajv_qos.model.PoliticaQos;
import pe.edu.sajv.sajv_qos.model.QosOperacion;

import pe.edu.sajv.sajv_qos.repository.AudienciaRepository;
import pe.edu.sajv.sajv_qos.repository.EquipoRedRepository;
import pe.edu.sajv.sajv_qos.repository.PoliticaQosRepository;

@Service
@RequiredArgsConstructor
public class QosAutomatizacionService {

    private static final Logger log =
            LoggerFactory.getLogger(QosAutomatizacionService.class);

    // =====================================================
    // CONSTANTES
    // =====================================================

    private static final int ESTADO_ACTIVO = 1;

    private static final int SITUACION_PROGRAMADA = 1;
    private static final int SITUACION_EN_CURSO = 2;

    private static final int ACCION_ACTIVAR = 1;
    private static final int ACCION_DESACTIVAR = 2;

    private static final int RESULTADO_EXITOSO = 1;

    // Origen 1: automático.
    private static final int ORIGEN_AUTOMATICO = 1;

    private static final ZoneId ZONA_HORARIA =
            ZoneId.of("America/Lima");

    // =====================================================
    // DEPENDENCIAS
    // =====================================================

    private final AudienciaRepository audienciaRepository;
    private final EquipoRedRepository equipoRedRepository;
    private final PoliticaQosRepository politicaQosRepository;
    private final QosOperacionService qosOperacionService;

    // =====================================================
    // PROCESAR AUTOMATIZACIÓN POR SEDE
    // =====================================================

    @Transactional
    public void procesarAudiencias() {

        LocalDate fechaActual = LocalDate.now(ZONA_HORARIA);
        LocalTime horaActual = LocalTime.now(ZONA_HORARIA);

        log.info(
                "Iniciando evaluación automática QoS. Fecha: {}, Hora: {}",
                fechaActual,
                horaActual
        );

        // =================================================
        // 1. RECUPERAR POLÍTICA GLOBAL ACTIVA
        // =================================================

        List<PoliticaQos> politicas =
                politicaQosRepository.findByEstadoOrderByNombreAsc(
                        ESTADO_ACTIVO
                );

        if (politicas.size() != 1) {

            log.error(
                    "Automatización QoS detenida. "
                    + "Se esperaba exactamente una política activa, "
                    + "pero se encontraron: {}",
                    politicas.size()
            );

            return;
        }

        PoliticaQos politica = politicas.get(0);

        log.info(
                "Política global seleccionada: {} - {}",
                politica.getId(),
                politica.getCodigo()
        );

        // =================================================
        // 2. RECUPERAR AUDIENCIAS ACTIVAS
        // =================================================

        List<Audiencia> audiencias =
                audienciaRepository.findByEstadoOrderByFechaAscHoraInicioAsc(
                        ESTADO_ACTIVO
                );

        // Filtrar audiencias cuyo horario incluye el momento actual.
        List<Audiencia> audienciasEnCurso = audiencias.stream()
                .filter(this::esAudienciaVigente)
                .filter(a -> a.getFecha().equals(fechaActual))
                .filter(a -> !horaActual.isBefore(a.getHoraInicio()))
                .filter(a -> horaActual.isBefore(a.getHoraFin()))
                .toList();

        log.info(
                "Audiencias que requieren QoS en este momento: {}",
                audienciasEnCurso.size()
        );

        // =================================================
        // 3. AGRUPAR AUDIENCIAS POR SEDE
        // =================================================

        Map<Integer, List<Audiencia>> audienciasPorSede =
                audienciasEnCurso.stream()
                        .collect(Collectors.groupingBy(
                                a -> a.getSala().getSede().getId()
                        ));

        // =================================================
        // 4. RECUPERAR ROUTERS ACTIVOS
        // =================================================

        List<EquipoRed> equipos =
                equipoRedRepository.findByEstadoOrderByNombreAsc(
                        ESTADO_ACTIVO
                );

        Map<Integer, List<EquipoRed>> equiposPorSede =
                equipos.stream()
                        .collect(Collectors.groupingBy(
                                e -> e.getSede().getId()
                        ));

        /*
         * Evaluamos todas las sedes que tienen routers activos
         * o audiencias vigentes.
         *
         * Esto permite también detectar routers que deben
         * desactivarse cuando ya no tienen audiencias.
         */

        Set<Integer> sedesAProcesar =
                new java.util.HashSet<>();

        sedesAProcesar.addAll(equiposPorSede.keySet());
        sedesAProcesar.addAll(audienciasPorSede.keySet());

        // =================================================
        // 5. PROCESAR CADA SEDE
        // =================================================

        for (Integer sedeId : sedesAProcesar) {

            try {

                procesarSede(
                        sedeId,
                        equiposPorSede,
                        audienciasPorSede,
                        politica
                );

            } catch (Exception e) {

                log.error(
                        "Error al procesar automatización QoS "
                        + "para la sede {}",
                        sedeId,
                        e
                );
            }
        }

        log.info(
                "Finalizó evaluación automática QoS."
        );
    }

    // =====================================================
    // PROCESAR UNA SEDE
    // =====================================================

    private void procesarSede(
            Integer sedeId,
            Map<Integer, List<EquipoRed>> equiposPorSede,
            Map<Integer, List<Audiencia>> audienciasPorSede,
            PoliticaQos politica) {

        log.info(
                "Evaluando automatización QoS para sede: {}",
                sedeId
        );

        // =================================================
        // 1. OBTENER AUDIENCIAS DE LA SEDE
        // =================================================

        List<Audiencia> audiencias =
                audienciasPorSede.getOrDefault(
                        sedeId,
                        List.of()
                );

        // =================================================
        // 2. OBTENER ROUTER DE LA SEDE
        // =================================================

        List<EquipoRed> equipos =
                equiposPorSede.getOrDefault(
                        sedeId,
                        List.of()
                );

        if (equipos.size() != 1) {

            log.error(
                    "No se puede automatizar la sede {}. "
                    + "Se esperaba exactamente un router activo "
                    + "y se encontraron: {}",
                    sedeId,
                    equipos.size()
            );

            return;
        }

        EquipoRed equipo = equipos.get(0);

        log.info(
                "Router seleccionado. Sede: {}, Equipo: {}, IP: {}",
                sedeId,
                equipo.getNombre(),
                equipo.getIp()
        );

        // =================================================
        // 3. DETERMINAR SI LA SEDE NECESITA QoS
        // =================================================

        boolean necesitaQos = !audiencias.isEmpty();

        // =================================================
        // CONTROL DE INTERVENCIÓN MANUAL
        // =================================================

        if (qosOperacionService.tieneIntervencionManualVigente(
                equipo.getId(),
                politica.getId())) {

        log.info(
                "Automatización omitida por intervención manual vigente. Sede: {}, Equipo: {}",
                sedeId,
                equipo.getId()
        );

        return;
        }
        // =================================================
        // 4. CONSULTAR ESTADO ACTUAL DEL ROUTER
        // =================================================

        boolean qosActivo =
                qosOperacionService.estaActivo(
                        equipo.getId(),
                        politica.getId()
                );

        log.info(
                "Estado QoS. Sede: {}, Equipo: {}, Activo: {}, "
                + "Audiencias vigentes: {}",
                sedeId,
                equipo.getId(),
                qosActivo,
                audiencias.size()
        );

        // =================================================
        // 5. ACTIVAR QoS
        // =================================================

        if (necesitaQos && !qosActivo) {

            // Se utiliza una audiencia vigente como referencia
            // para registrar la activación.
            Audiencia audienciaReferencia = audiencias.get(0);

            log.info(
                    "Se requiere ACTIVAR QoS. Sede: {}, Audiencia: {}",
                    sedeId,
                    audienciaReferencia.getId()
            );

            QosOperacion resultado =
                    qosOperacionService.ejecutar(
                            audienciaReferencia.getId(),
                            equipo.getId(),
                            politica.getId(),
                            ACCION_ACTIVAR,
                            ORIGEN_AUTOMATICO
                    );

            registrarResultado(
                    resultado,
                    ACCION_ACTIVAR,
                    sedeId
            );

            return;
        }

        // =================================================
        // 6. DESACTIVAR QoS
        // =================================================

        if (!necesitaQos && qosActivo) {

            log.info(
                    "Se requiere DESACTIVAR QoS. Sede: {}, Equipo: {}",
                    sedeId,
                    equipo.getId()
            );

            /*
             * Se utiliza audienciaId = null porque la sede
             * ya no tiene audiencias que requieran QoS.
             *
             * La operación de desactivación queda registrada
             * a nivel del router y de la política.
             */

            QosOperacion resultado =
                    qosOperacionService.ejecutar(
                            null,
                            equipo.getId(),
                            politica.getId(),
                            ACCION_DESACTIVAR,
                            ORIGEN_AUTOMATICO
                    );

            registrarResultado(
                    resultado,
                    ACCION_DESACTIVAR,
                    sedeId
            );

            return;
        }

        // =================================================
        // 7. NO SE REQUIERE NINGÚN CAMBIO
        // =================================================

        log.info(
                "No se requiere modificación QoS. "
                + "Sede: {}, Equipo: {}, Estado: {}",
                sedeId,
                equipo.getId(),
                qosActivo ? "ACTIVO" : "INACTIVO"
        );
    }

    // =====================================================
    // VALIDAR AUDIENCIA VIGENTE
    // =====================================================

    private boolean esAudienciaVigente(Audiencia audiencia) {

        if (audiencia == null) {
            return false;
        }

        if (audiencia.getFecha() == null
                || audiencia.getHoraInicio() == null
                || audiencia.getHoraFin() == null
                || audiencia.getSala() == null
                || audiencia.getSala().getSede() == null) {

            return false;
        }

        Integer situacion = audiencia.getSituacion();

        return situacion != null
                && (situacion == SITUACION_PROGRAMADA
                || situacion == SITUACION_EN_CURSO);
    }

    // =====================================================
    // REGISTRAR RESULTADO EN LOG
    // =====================================================

    private void registrarResultado(
            QosOperacion resultado,
            Integer accion,
            Integer sedeId) {

        if (resultado == null) {

            log.error(
                    "La operación QoS no devolvió resultado. Sede: {}",
                    sedeId
            );

            return;
        }

        String nombreAccion =
                accion == ACCION_ACTIVAR
                        ? "ACTIVACIÓN"
                        : "DESACTIVACIÓN";

        if (resultado.getResultado() != null
                && resultado.getResultado() == RESULTADO_EXITOSO) {

            log.info(
                    "{} QoS exitosa. Sede: {}, Operación: {}, Mensaje: {}",
                    nombreAccion,
                    sedeId,
                    resultado.getId(),
                    resultado.getMensaje()
            );

        } else {

            log.error(
                    "{} QoS fallida. Sede: {}, Operación: {}, Mensaje: {}",
                    nombreAccion,
                    sedeId,
                    resultado.getId(),
                    resultado.getMensaje()
            );
        }
    }
}
