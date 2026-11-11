package pe.edu.sajv.sajv_qos.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.sajv.sajv_qos.dto.EquipoRedCredencialesDTO;
import pe.edu.sajv.sajv_qos.model.EquipoRed;
import pe.edu.sajv.sajv_qos.repository.EquipoRedRepository;
import pe.edu.sajv.sajv_qos.util.CryptoService;

@Service
public class EquipoRedCredencialesService {

    private final EquipoRedRepository equipoRepository;
    private final CryptoService cryptoService;

    public EquipoRedCredencialesService(
            EquipoRedRepository equipoRepository,
            CryptoService cryptoService) {

        this.equipoRepository = equipoRepository;
        this.cryptoService = cryptoService;
    }

    @Transactional
    public void actualizarCredenciales(
            Integer equipoId,
            EquipoRedCredencialesDTO request) {

        EquipoRed equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "No se encontró el equipo de red."
                    )
                );

        String contrasenaCifrada =
                cryptoService.encrypt(request.contrasena());

        equipo.setUsuario(request.usuario());
        equipo.setContrasena(contrasenaCifrada);

        equipoRepository.save(equipo);
    }
}