package pe.edu.sajv.sajv_qos.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.edu.sajv.sajv_qos.repository.AudienciaRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActualizacionSituacionAudienciaService {

    private static final ZoneId ZONA_LIMA =
        ZoneId.of("America/Lima");

    private final AudienciaRepository audienciaRepository;
    private final AuditoriaService auditoriaService;

    @Transactional
    public void actualizarSituaciones() {

        LocalDate fechaActual = LocalDate.now(ZONA_LIMA);
        LocalTime horaActual = LocalTime.now(ZONA_LIMA);

        // Primero finalizar las audiencias cuyo horario terminó.
        int finalizadas = audienciaRepository
            .actualizarAudienciasFinalizadas(
                fechaActual,
                horaActual
            );

        // Después actualizar las audiencias que están en curso.
        int enCurso = audienciaRepository
            .actualizarAudienciasEnCurso(
                fechaActual,
                horaActual
            );

        if (finalizadas > 0 || enCurso > 0) {

            auditoriaService.registrarSistema("AUDIENCIAS", "ACTUALIZACION_AUTOMATICA",
                "El sistema actualizó situaciones: " + enCurso
                    + " audiencias en curso y " + finalizadas
                    + " audiencias finalizadas.");

            log.info(
                "Actualización de audiencias: {} en curso, {} finalizadas. Fecha: {} Hora: {}",
                enCurso,
                finalizadas,
                fechaActual,
                horaActual
            );
        }
    }
}