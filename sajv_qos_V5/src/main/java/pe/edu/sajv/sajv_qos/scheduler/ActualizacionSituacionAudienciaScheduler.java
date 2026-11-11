package pe.edu.sajv.sajv_qos.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.service.ActualizacionSituacionAudienciaService;

@Component
@RequiredArgsConstructor
public class ActualizacionSituacionAudienciaScheduler {

    private final ActualizacionSituacionAudienciaService
        actualizacionService;

    @Scheduled(
        fixedDelayString = "${sajv.audiencias.scheduler.interval:30000}",
        initialDelayString = "${sajv.audiencias.scheduler.initial-delay:10000}"
    )
    public void actualizarSituaciones() {

        actualizacionService.actualizarSituaciones();
    }
}