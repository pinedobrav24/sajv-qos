package pe.edu.sajv.sajv_qos.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.sajv.sajv_qos.dto.EquipoRedDTO;
import pe.edu.sajv.sajv_qos.dto.PoliticaQosDTO;
import pe.edu.sajv.sajv_qos.dto.QosOperacionDTO;
import pe.edu.sajv.sajv_qos.service.QosConsultaService;

@RestController
@RequestMapping("/api/qos")
public class QosConsultaController {

    private final QosConsultaService qosConsultaService;

    public QosConsultaController(QosConsultaService qosConsultaService) {
        this.qosConsultaService = qosConsultaService;
    }

    @GetMapping("/equipos")
    public List<EquipoRedDTO> listarEquipos() {
        return qosConsultaService.listarEquipos();
    }

    @GetMapping("/politicas")
    public List<PoliticaQosDTO> listarPoliticas() {
        return qosConsultaService.listarPoliticas();
    }

    @GetMapping("/operaciones")
    public List<QosOperacionDTO> listarOperaciones() {
        return qosConsultaService.listarOperaciones();
    }
}
