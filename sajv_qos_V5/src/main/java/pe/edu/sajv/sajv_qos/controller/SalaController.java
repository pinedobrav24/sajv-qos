
package pe.edu.sajv.sajv_qos.controller;

import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.sajv.sajv_qos.dto.SalaOpcionDTO;
import pe.edu.sajv.sajv_qos.model.Sala;
import pe.edu.sajv.sajv_qos.service.SalaService;

@Controller
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;


    // =====================================================
    // LISTAR SALAS
    // =====================================================

    @GetMapping
    public String listar(
            Model model,
            Authentication authentication) {

        cargarDatos(model, authentication.getName());

        return "salas";
    }


    // =====================================================
    // REGISTRAR SALA
    // =====================================================

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("sala") Sala sala,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            cargarDatos(model, authentication.getName());

            model.addAttribute("abrirModal", "registro");

            return "salas";
        }

        try {

            salaService.registrar(
                sala,
                authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                "success",
                "La sala se registró correctamente."
            );

            return "redirect:/salas";

        } catch (IllegalArgumentException e) {

            result.rejectValue(
                "descripcion",
                "error.sala",
                e.getMessage()
            );

            cargarDatos(model, authentication.getName());

            model.addAttribute("abrirModal", "registro");

            return "salas";
        }
    }


    // =====================================================
    // EDITAR SALA
    // =====================================================

    @PostMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @Valid @ModelAttribute("sala") Sala sala,
            BindingResult result,
            Model model,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {

            cargarDatos(model, authentication.getName());

            model.addAttribute("abrirModal", "edicion");
            model.addAttribute("idEdicion", id);

            return "salas";
        }

        try {

            salaService.editar(
                id,
                sala,
                authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                "success",
                "La sala se actualizó correctamente."
            );

            return "redirect:/salas";

        } catch (IllegalArgumentException e) {

            result.rejectValue(
                "descripcion",
                "error.sala",
                e.getMessage()
            );

            cargarDatos(model, authentication.getName());

            model.addAttribute("abrirModal", "edicion");
            model.addAttribute("idEdicion", id);

            return "salas";
        }
    }


    // =====================================================
    // DESACTIVAR SALA
    // =====================================================

    @PostMapping("/desactivar/{id}")
    public String desactivar(
            @PathVariable Integer id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            salaService.desactivar(
                id,
                authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                "success",
                "La sala se desactivó correctamente."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                "error",
                e.getMessage()
            );
        }

        return "redirect:/salas";
    }


    // =====================================================
    // ACTIVAR SALA
    // =====================================================

    @PostMapping("/activar/{id}")
    public String activar(
            @PathVariable Integer id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {

            salaService.activar(
                id,
                authentication.getName()
            );

            redirectAttributes.addFlashAttribute(
                "success",
                "La sala se activó correctamente."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                "error",
                e.getMessage()
            );
        }

        return "redirect:/salas";
    }


    // =====================================================
    // CONSULTAR SALAS POR SEDE
    // UTILIZADO POR EL MÓDULO DE AUDIENCIAS
    // =====================================================

    @GetMapping("/por-sede/{sedeId}")
    @ResponseBody
    public List<SalaOpcionDTO> listarPorSede(
            @PathVariable Integer sedeId,
            Authentication authentication) {

        return salaService.listarPorSede(
                sedeId,
                authentication.getName()
            )
            .stream()
            .map(sala -> new SalaOpcionDTO(
                sala.getId(),
                sala.getDescripcion()
            ))
            .toList();
    }


    // =====================================================
    // CARGAR INFORMACIÓN DE LA VISTA
    // =====================================================

    private void cargarDatos(
            Model model,
            String nombreUsuario) {

        model.addAttribute(
            "salas",
            salaService.listarParaGestion(nombreUsuario)
        );

        model.addAttribute(
            "sedes",
            salaService.listarSedesDisponibles(nombreUsuario)
        );

        if (!model.containsAttribute("sala")) {
            model.addAttribute("sala", new Sala());
        }
    }

}
