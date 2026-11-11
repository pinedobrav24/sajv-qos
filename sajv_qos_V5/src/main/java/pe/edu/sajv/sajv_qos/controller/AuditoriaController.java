package pe.edu.sajv.sajv_qos.controller;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.model.Auditoria;
import pe.edu.sajv.sajv_qos.service.AuditoriaService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public String listar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String accion,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {

        if (pagina < 0) pagina = 0;
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            model.addAttribute("error", "La fecha inicial no puede ser posterior a la fecha final.");
            desde = null;
            hasta = null;
        }

        Page<Auditoria> registros = auditoriaService.buscar(
                usuario, modulo, accion, desde, hasta,
                PageRequest.of(pagina, 20, Sort.by(Sort.Direction.DESC, "fecha")));

        model.addAttribute("registros", registros);
        model.addAttribute("usuarioFiltro", usuario);
        model.addAttribute("moduloFiltro", modulo);
        model.addAttribute("accionFiltro", accion);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("modulos", new String[] {
                "SEGURIDAD", "USUARIOS", "AUDIENCIAS", "SEDES", "SALAS", "QoS", "MONITOREO", "SISTEMA"
        });
        return "auditoria/auditoria";
    }
}
