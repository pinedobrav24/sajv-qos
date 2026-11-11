package pe.edu.sajv.sajv_qos.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.sajv.sajv_qos.dto.UsuarioForm;
import pe.edu.sajv.sajv_qos.repository.SedeRepository;
import pe.edu.sajv.sajv_qos.service.UsuarioService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SedeRepository sedeRepository;

    @GetMapping
    public String listar(Model model) {
        cargarDatos(model);
        if (!model.containsAttribute("usuarioForm")) {
            model.addAttribute("usuarioForm", new UsuarioForm());
        }
        return "usuarios";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                          BindingResult result, Model model,
                          RedirectAttributes redirect) {
        if (result.hasErrors()) {
            cargarDatos(model);
            model.addAttribute("modoModal", "registro");
            return "usuarios";
        }
        try {
            usuarioService.crear(form);
            redirect.addFlashAttribute("success", "El usuario se registró correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            result.reject("usuarioForm", ex.getMessage());
            cargarDatos(model);
            model.addAttribute("modoModal", "registro");
            return "usuarios";
        }
    }

    @PostMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         @Valid @ModelAttribute("usuarioForm") UsuarioForm form,
                         BindingResult result, Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            cargarDatos(model);
            model.addAttribute("modoModal", "edicion");
            model.addAttribute("idEdicion", id);
            return "usuarios";
        }
        try {
            usuarioService.actualizar(id, form);
            redirect.addFlashAttribute("success", "El usuario se actualizó correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException ex) {
            result.reject("usuarioForm", ex.getMessage());
            cargarDatos(model);
            model.addAttribute("modoModal", "edicion");
            model.addAttribute("idEdicion", id);
            return "usuarios";
        }
    }

    @PostMapping("/{id}/desactivar")
    public String desactivar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            usuarioService.cambiarEstado(id, false);
            redirect.addFlashAttribute("success", "El usuario se desactivó correctamente.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/activar")
    public String activar(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            usuarioService.cambiarEstado(id, true);
            redirect.addFlashAttribute("success", "El usuario se activó correctamente.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/restablecer-contrasena")
    public String restablecerContrasena(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            String claveTemporal = usuarioService.restablecerContrasena(id);
            redirect.addFlashAttribute("claveTemporal", claveTemporal);
            redirect.addFlashAttribute("usuarioRestablecido", usuarioService.buscarPorId(id).getUsuario());
            redirect.addFlashAttribute("success", "La contraseña fue restablecida. Entregue al usuario la clave temporal mostrada; deberá cambiarla al iniciar sesión.");
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios";
    }

    private void cargarDatos(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        model.addAttribute("sedes", sedeRepository.findAll().stream()
                .filter(s -> Integer.valueOf(1).equals(s.getEstado())).toList());
    }
}
