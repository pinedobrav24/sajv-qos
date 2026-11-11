package pe.edu.sajv.sajv_qos.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import pe.edu.sajv.sajv_qos.model.EquipoRed;
import pe.edu.sajv.sajv_qos.model.PoliticaQos;
import pe.edu.sajv.sajv_qos.model.Usuario;
import pe.edu.sajv.sajv_qos.repository.EquipoRedRepository;
import pe.edu.sajv.sajv_qos.repository.PoliticaQosRepository;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;
import pe.edu.sajv.sajv_qos.service.QosOperacionService;

@Controller
@RequiredArgsConstructor
public class QosWebController {

    private final EquipoRedRepository equipoRepository;
    private final PoliticaQosRepository politicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final QosOperacionService qosOperacionService;

    @Value("${sajv.qos.manual.duration-minutes:10}")
    private long duracionManualMinutos;

    @GetMapping("/qos")
    public String mostrarControlQos(Authentication authentication, Model model) {
        Usuario usuario = usuarioRepository.findByUsuario(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("No se encontró el usuario autenticado."));

        List<EquipoRed> equipos = usuario.getRol() != null && usuario.getRol() == 2
                ? equipoRepository.findBySede_IdAndEstadoOrderByNombreAsc(
                        usuario.getSede() == null ? -1 : usuario.getSede().getId(), 1)
                : equipoRepository.findByEstadoOrderByNombreAsc(1);

        List<PoliticaQos> politicas = politicaRepository.findByEstadoOrderByNombreAsc(1);
        List<ControlQosFila> filas = new ArrayList<>();

        for (EquipoRed equipo : equipos) {
            for (PoliticaQos politica : politicas) {
                boolean activa = qosOperacionService.estaActivo(equipo.getId(), politica.getId());
                filas.add(new ControlQosFila(
                        equipo.getId(), equipo.getNombre(),
                        equipo.getSede() == null ? "Sin sede" : equipo.getSede().getDescripcion(),
                        politica.getId(), politica.getNombre(), politica.getPolicyMap(),
                        activa));
            }
        }

        model.addAttribute("filasQos", filas);
        model.addAttribute("duracionManualMinutos", duracionManualMinutos);
        return "qos/index";
    }

    public record ControlQosFila(
            Integer equipoId,
            String equipo,
            String sede,
            Integer politicaId,
            String politica,
            String policyMap,
            boolean activa) {}
}
