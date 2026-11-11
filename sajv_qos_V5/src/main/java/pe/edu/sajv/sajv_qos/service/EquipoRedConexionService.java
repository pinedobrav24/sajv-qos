package pe.edu.sajv.sajv_qos.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import pe.edu.sajv.sajv_qos.dto.RouterConexionRequestDTO;
import pe.edu.sajv.sajv_qos.model.EquipoRed;
import pe.edu.sajv.sajv_qos.repository.EquipoRedRepository;
import pe.edu.sajv.sajv_qos.util.CryptoService;

@Service
@RequiredArgsConstructor
public class EquipoRedConexionService {

    private final EquipoRedRepository equipoRepository;
    private final CryptoService cryptoService;
    private final QosApiClient qosApiClient;

    @Transactional(readOnly = true)
    public Map<String, Object> probarConexion(Integer equipoId) {

        EquipoRed equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontró el equipo de red."
                    )
                );

        if (equipo.getEstado() == null || equipo.getEstado() != 1) {
            throw new IllegalStateException(
                "El equipo de red se encuentra inactivo."
            );
        }

        if (equipo.getContrasena() == null ||
                equipo.getContrasena().isBlank()) {
            throw new IllegalStateException(
                "El equipo no tiene credenciales SSH registradas."
            );
        }

        String contrasena =
                cryptoService.decrypt(equipo.getContrasena());

        RouterConexionRequestDTO request =
                new RouterConexionRequestDTO(
                    equipo.getId(),
                    equipo.getHostname(),
                    equipo.getIp(),
                    equipo.getPuerto(),
                    equipo.getUsuario(),
                    contrasena,
                    equipo.getFabricante(),
                    equipo.getModelo()
                );

        return qosApiClient.probarConexion(request);
    }
}