package pe.edu.sajv.sajv_qos.service;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.dto.MonitoreoResumenDTO;
import pe.edu.sajv.sajv_qos.model.EquipoRed;
import pe.edu.sajv.sajv_qos.model.QosCodigos;
import pe.edu.sajv.sajv_qos.model.QosOperacion;
import pe.edu.sajv.sajv_qos.repository.EquipoRedRepository;
import pe.edu.sajv.sajv_qos.repository.QosOperacionRepository;

@Service
@RequiredArgsConstructor
public class MonitoreoService {

    private static final ZoneId ZONA_LIMA = ZoneId.of("America/Lima");
    private static final int ACTIVO = 1;
    private static final int TIMEOUT_MS = 1800;

    private final EquipoRedRepository equipoRepository;
    private final QosOperacionRepository operacionRepository;

    @Value("${sajv.qos.simulation:true}")
    private boolean modoSimulacion;

    // Independiente del modo de ejecución QoS: permite probar TCP sin activar cambios reales de configuración.
    @Value("${sajv.monitoreo.tcp.enabled:false}")
    private boolean monitoreoTcpHabilitado;

    @Transactional(readOnly = true)
    public MonitoreoResumenDTO obtenerResumen() {
        List<EquipoRed> equipos = equipoRepository.findByEstadoOrderByNombreAsc(ACTIVO);
        List<QosOperacion> operaciones = operacionRepository.findTop100ByOrderByFechaInicioDesc();

        Map<String, QosOperacion> ultimaOperacionPorEquipoPolitica = new HashMap<>();
        for (QosOperacion op : operaciones) {
            if (op.getEquipo() == null || op.getPolitica() == null) {
                continue;
            }
            String clave = op.getEquipo().getId() + ":" + op.getPolitica().getId();
            ultimaOperacionPorEquipoPolitica.putIfAbsent(clave, op);
        }

        List<MonitoreoResumenDTO.EquipoMonitoreoDTO> detalle = new ArrayList<>();
        long conectados = 0;
        long sinRespuesta = 0;
        long qosActivas = 0;

        for (EquipoRed equipo : equipos) {
            EstadoConexion conexion = comprobarConexion(equipo);
            if ("CONECTADO".equals(conexion.estado())) conectados++;
            if ("SIN_RESPUESTA".equals(conexion.estado())) sinRespuesta++;

            QosOperacion ultima = ultimaOperacionPorEquipoPolitica.values().stream()
                    .filter(op -> op.getEquipo().getId().equals(equipo.getId()))
                    .max((a, b) -> a.getFechaInicio().compareTo(b.getFechaInicio()))
                    .orElse(null);

            String estadoQos = "SIN_REGISTRO";
            String ultimaAccion = "Sin operaciones";
            LocalDateTime fechaOperacion = null;

            if (ultima != null) {
                fechaOperacion = ultima.getFechaInicio();
                if (Integer.valueOf(QosCodigos.RESULTADO_EXITOSO).equals(ultima.getResultado())) {
                    if (Integer.valueOf(QosCodigos.ACCION_ACTIVAR).equals(ultima.getAccion())) {
                        estadoQos = "ACTIVA";
                        ultimaAccion = "Activación";
                        qosActivas++;
                    } else if (Integer.valueOf(QosCodigos.ACCION_DESACTIVAR).equals(ultima.getAccion())) {
                        estadoQos = "INACTIVA";
                        ultimaAccion = "Desactivación";
                    }
                } else {
                    estadoQos = "OPERACION_NO_EXITOSA";
                    ultimaAccion = "Última operación no exitosa";
                }
            }

            detalle.add(new MonitoreoResumenDTO.EquipoMonitoreoDTO(
                    equipo.getId(),
                    equipo.getNombre(),
                    equipo.getIp(),
                    equipo.getPuerto(),
                    equipo.getSede() == null ? "Sin sede" : equipo.getSede().getDescripcion(),
                    conexion.estado(),
                    conexion.latenciaMs(),
                    estadoQos,
                    ultimaAccion,
                    fechaOperacion,
                    conexion.detalle()));
        }

        return new MonitoreoResumenDTO(
                modoSimulacion,
                monitoreoTcpHabilitado,
                LocalDateTime.now(ZONA_LIMA),
                equipos.size(),
                conectados,
                sinRespuesta,
                qosActivas,
                detalle);
    }

    private EstadoConexion comprobarConexion(EquipoRed equipo) {
        if (!monitoreoTcpHabilitado) {
            return new EstadoConexion(
                    "SIMULADO",
                    null,
                    "Comprobación TCP omitida: monitoreo de conectividad real deshabilitado.");
        }

        long inicio = System.nanoTime();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(equipo.getIp(), equipo.getPuerto()), TIMEOUT_MS);
            long latencia = (System.nanoTime() - inicio) / 1_000_000;
            return new EstadoConexion("CONECTADO", latencia, "Conexión TCP establecida.");
        } catch (Exception ex) {
            return new EstadoConexion(
                    "SIN_RESPUESTA",
                    null,
                    "No se pudo establecer conexión TCP en " + TIMEOUT_MS + " ms.");
        }
    }

    private record EstadoConexion(String estado, Long latenciaMs, String detalle) {
    }
}
