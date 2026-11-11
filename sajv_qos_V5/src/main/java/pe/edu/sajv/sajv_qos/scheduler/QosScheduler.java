
package pe.edu.sajv.sajv_qos.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import pe.edu.sajv.sajv_qos.service.QosAutomatizacionService;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "sajv.qos.scheduler",
        name = "enabled",
        havingValue = "true"
)
public class QosScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(QosScheduler.class);

    private final QosAutomatizacionService automatizacionService;

    // =====================================================
    // EJECUCIÓN AUTOMÁTICA
    // =====================================================

    @Scheduled(
            fixedDelayString = "${sajv.qos.scheduler.interval:30000}",
            initialDelayString = "${sajv.qos.scheduler.initial-delay:15000}"
    )
    public void ejecutarAutomatizacion() {

        log.info("==============================================");
        log.info("INICIO DEL SCHEDULER SAJV-QoS");
        log.info("==============================================");

        try {

            automatizacionService.procesarAudiencias();

        } catch (Exception e) {

            log.error(
                    "Error durante la ejecución del scheduler SAJV-QoS.",
                    e
            );

        } finally {

            log.info("==============================================");
            log.info("FIN DEL SCHEDULER SAJV-QoS");
            log.info("==============================================");
        }
    }
}
