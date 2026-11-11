
package pe.edu.sajv.sajv_qos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.dto.DashboardResumenDTO;
import pe.edu.sajv.sajv_qos.service.DashboardService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    // Vista principal del Dashboard
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {

        model.addAttribute(
            "resumen",
            dashboardService.obtenerResumen()
        );

        return "dashboard/index";
    }

    // Endpoint para actualización automática
    @GetMapping("/api/dashboard/resumen")
    @ResponseBody
    public DashboardResumenDTO obtenerResumen() {

        return dashboardService.obtenerResumenApi();
    }
}
