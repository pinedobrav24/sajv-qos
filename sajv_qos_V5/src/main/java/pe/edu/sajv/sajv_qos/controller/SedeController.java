
package pe.edu.sajv.sajv_qos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import pe.edu.sajv.sajv_qos.model.Sede;
import pe.edu.sajv.sajv_qos.service.SedeService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sedes")
public class SedeController {

    private final SedeService sedeService;


    // =====================================================
    // LISTAR SEDES
    // =====================================================

    @GetMapping
    public String listar(Model model) {

        model.addAttribute("sedes", sedeService.SedeSel());

        if (!model.containsAttribute("sede")) {
            model.addAttribute("sede", new Sede());
        }

        return "sedes";
    }


    // =====================================================
    // REGISTRAR SEDE
    // =====================================================

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("sede") Sede sede,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones de campos de la entidad
        if (result.hasErrors()) {

            cargarListado(model);

            model.addAttribute("abrirModal", "registro");

            return "sedes";
        }

        try {

            sedeService.registrar(sede);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "La sede se registró correctamente."
            );

            return "redirect:/sedes";

        } catch (IllegalArgumentException e) {

            result.rejectValue(
                    "descripcion",
                    "error.sede",
                    e.getMessage()
            );

            cargarListado(model);

            model.addAttribute("abrirModal", "registro");

            return "sedes";
        }
    }


    // =====================================================
    // EDITAR SEDE
    // =====================================================

    @PostMapping("/editar/{id}")
    public String editar(
            @PathVariable Integer id,
            @Valid @ModelAttribute("sede") Sede sede,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validaciones de campos de la entidad
        if (result.hasErrors()) {

            cargarListado(model);

            model.addAttribute("abrirModal", "edicion");
            model.addAttribute("idEdicion", id);

            return "sedes";
        }

        try {

            sedeService.editar(id, sede);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "La sede se actualizó correctamente."
            );

            return "redirect:/sedes";

        } catch (IllegalArgumentException e) {

            result.rejectValue(
                    "descripcion",
                    "error.sede",
                    e.getMessage()
            );

            cargarListado(model);

            model.addAttribute("abrirModal", "edicion");
            model.addAttribute("idEdicion", id);

            return "sedes";
        }
    }


    // =====================================================
    // DESACTIVAR SEDE
    // =====================================================

    @PostMapping("/desactivar/{id}")
    public String desactivar(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {

            sedeService.desactivar(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "La sede se desactivó correctamente."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/sedes";
    }


    // =====================================================
    // ACTIVAR SEDE
    // =====================================================

    @PostMapping("/activar/{id}")
    public String activar(
            @PathVariable Integer id,
            RedirectAttributes redirectAttributes) {

        try {

            sedeService.activar(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "La sede se activó correctamente."
            );

        } catch (IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/sedes";
    }


    // =====================================================
    // MÉTODO AUXILIAR: RECARGAR LISTADO
    // =====================================================

    private void cargarListado(Model model) {

        model.addAttribute("sedes", sedeService.SedeSel());

    }

}
