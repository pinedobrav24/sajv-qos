package pe.edu.sajv.sajv_qos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;
import pe.edu.sajv.sajv_qos.service.UsuarioService;

@Controller
@RequiredArgsConstructor
public class ContrasenaController {
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/mi-contrasena")
    public String formulario(Authentication auth, Model model) {
        boolean obligatorio = usuarioRepository.findByUsuario(auth.getName())
                .map(u -> Integer.valueOf(1).equals(u.getDebeCambiarContrasena())).orElse(false);
        model.addAttribute("cambioObligatorio", obligatorio);
        return "mi-contrasena";
    }

    @PostMapping("/mi-contrasena")
    public String cambiar(Authentication auth,
            @RequestParam String contrasenaActual,
            @RequestParam String nuevaContrasena,
            @RequestParam String confirmarContrasena,
            RedirectAttributes redirect) {
        try {
            usuarioService.cambiarMiContrasena(auth.getName(), contrasenaActual,
                    nuevaContrasena, confirmarContrasena);
            // La sesión conserva la identidad y los roles; la contraseña no se guarda en la sesión.
            redirect.addFlashAttribute("success", "Contraseña actualizada correctamente.");
            return "redirect:/dashboard";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/mi-contrasena";
        }
    }
}
