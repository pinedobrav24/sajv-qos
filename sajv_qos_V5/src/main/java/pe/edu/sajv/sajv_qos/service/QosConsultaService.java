package pe.edu.sajv.sajv_qos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.sajv.sajv_qos.dto.EquipoRedDTO;
import pe.edu.sajv.sajv_qos.dto.PoliticaQosDTO;
import pe.edu.sajv.sajv_qos.dto.QosOperacionDTO;
import pe.edu.sajv.sajv_qos.repository.EquipoRedRepository;
import pe.edu.sajv.sajv_qos.repository.PoliticaQosRepository;
import pe.edu.sajv.sajv_qos.repository.QosOperacionRepository;

@Service
@Transactional(readOnly = true)
public class QosConsultaService {

    private final EquipoRedRepository equipoRepository;
    private final PoliticaQosRepository politicaRepository;
    private final QosOperacionRepository operacionRepository;

    public QosConsultaService(
            EquipoRedRepository equipoRepository,
            PoliticaQosRepository politicaRepository,
            QosOperacionRepository operacionRepository) {
        this.equipoRepository = equipoRepository;
        this.politicaRepository = politicaRepository;
        this.operacionRepository = operacionRepository;
    }

    public List<EquipoRedDTO> listarEquipos() {
        return equipoRepository.findAll().stream()
            .map(e -> new EquipoRedDTO(
                e.getId(), e.getSede().getId(), e.getSede().getDescripcion(),
                e.getNombre(), e.getHostname(), e.getIp(), e.getPuerto(),
                e.getUsuario(), e.getInterfazQos(), e.getFabricante(),
                e.getModelo(), e.getEstado()))
            .toList();
    }

    public List<PoliticaQosDTO> listarPoliticas() {
        return politicaRepository.findAll().stream()
            .map(p -> new PoliticaQosDTO(
                p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion(),
                p.getDscp(), p.getAnchoBanda(), p.getEstado()))
            .toList();
    }

    public List<QosOperacionDTO> listarOperaciones() {
        return operacionRepository.findTop100ByOrderByFechaInicioDesc().stream()
            .map(o -> new QosOperacionDTO(
                o.getId(),
                o.getAudiencia() == null ? null : o.getAudiencia().getId(),
                o.getEquipo().getId(), o.getEquipo().getNombre(),
                o.getPolitica().getId(), o.getPolitica().getNombre(),
                o.getAccion(), o.getOrigen(), o.getFechaInicio(), o.getFechaFin(),
                o.getResultado(), o.getMensaje(), o.getSalida()))
            .toList();
    }
}
