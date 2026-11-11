package pe.edu.sajv.sajv_qos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pe.edu.sajv.sajv_qos.dto.MonitoreoResumenDTO;
import pe.edu.sajv.sajv_qos.service.MonitoreoService;

@Controller
@RequiredArgsConstructor
public class MonitoreoController {

    private final MonitoreoService monitoreoService;

    @GetMapping("/monitoreo")
    public String mostrarMonitoreo(Model model) {
        model.addAttribute("monitoreo", monitoreoService.obtenerResumen());
        return "monitoreo/index";
    }

    @GetMapping("/api/monitoreo/resumen")
    @ResponseBody
    public MonitoreoResumenDTO obtenerResumen() {
        return monitoreoService.obtenerResumen();
    }
}
