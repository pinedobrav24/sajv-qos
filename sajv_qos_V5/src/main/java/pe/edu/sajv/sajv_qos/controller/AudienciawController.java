
package pe.edu.sajv.sajv_qos.controller;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.model.Usuario;
import pe.edu.sajv.sajv_qos.repository.SedeRepository;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;
import pe.edu.sajv.sajv_qos.service.AudienciaService;

@Controller
@RequiredArgsConstructor
public class AudienciawController {

    private final SedeRepository sedeRepository;
    private final UsuarioRepository usuarioRepository;
    private final AudienciaService audienciaService;

    @GetMapping("/audiencias")
    public String mostrarAudiencias(
            Model model,
            Authentication authentication) {

        boolean esAdministrador = authentication.getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority()
                .equals("ROLE_ADMINISTRADOR"));

        boolean esOperador = authentication.getAuthorities()
            .stream()
            .anyMatch(a -> a.getAuthority()
                .equals("ROLE_OPERADOR"));

        model.addAttribute("esAdministrador", esAdministrador);
        model.addAttribute("esOperador", esOperador);

        // Administrador: cargar todas las sedes
        if (esAdministrador) {

            model.addAttribute(
                "sedes",
                sedeRepository.findAll()
            );
        }

        // Operador: obtener su sede asignada
if (esOperador) {

    Usuario usuario = usuarioRepository
        .findByUsuario(authentication.getName())
        .orElseThrow(() ->
            new IllegalStateException(
                "No se encontró el usuario autenticado."
            )
        );

    
    if (usuario.getSede() == null) {
        throw new IllegalStateException(
            "El operador no tiene una sede asignada."
        );
    }

    
    model.addAttribute("sedeUsuario", usuario.getSede());
}
        return "audiencias";
        
    }

    @PostMapping("/audiencias/guardar")
    public String guardarAudiencia(
            Authentication authentication,
            @RequestParam String nroExpediente,
            @RequestParam(required = false) Integer sedeId,
            @RequestParam Integer salaId,
            @RequestParam String responsable,
            @RequestParam(required = false) String coHost,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
                LocalTime horaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
                LocalTime horaFin,
            @RequestParam Integer tipo,
            @RequestParam(required = false) String comentarios,
            RedirectAttributes redirectAttributes) {

        try {

            audienciaService.registrar(
                authentication.getName(),
                nroExpediente,
                sedeId,
                salaId,
                responsable,
                coHost,
                fecha,
                horaInicio,
                horaFin,
                tipo,
                comentarios
            );

            redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "La audiencia fue registrada correctamente."
            );

        } catch (IllegalArgumentException | AccessDeniedException e) {

            redirectAttributes.addFlashAttribute(
                "mensajeError",
                e.getMessage()
            );
        }

        return "redirect:/audiencias";
    }

    
    @PostMapping("/audiencias/editar")
    public String editarAudiencia(
            Authentication authentication,
            @RequestParam Integer audienciaId,
            @RequestParam String nroExpediente,
            @RequestParam(required = false) Integer sedeId,
            @RequestParam Integer salaId,
            @RequestParam String responsable,
            @RequestParam(required = false) String coHost,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
                LocalTime horaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
                LocalTime horaFin,
            @RequestParam Integer tipo,
            @RequestParam(required = false) String comentarios,
            RedirectAttributes redirectAttributes) {

        try {

            audienciaService.editar(
                authentication.getName(),
                audienciaId,
                nroExpediente,
                sedeId,
                salaId,
                responsable,
                coHost,
                fecha,
                horaInicio,
                horaFin,
                tipo,
                comentarios
            );

            redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "La audiencia fue actualizada correctamente."
            );

        } catch (IllegalArgumentException | AccessDeniedException e) {

            redirectAttributes.addFlashAttribute(
                "mensajeError",
                e.getMessage()
            );
        }

        return "redirect:/audiencias";
    }


    @PostMapping("/audiencias/cancelar")
    public String cancelarAudiencia(
            Authentication authentication,
            @RequestParam Integer audienciaId,
            RedirectAttributes redirectAttributes) {

        try {

            audienciaService.cancelar(
                authentication.getName(),
                audienciaId
            );

            redirectAttributes.addFlashAttribute(
                "mensajeExito",
                "La audiencia fue cancelada correctamente."
            );

        } catch (IllegalArgumentException | AccessDeniedException e) {

            redirectAttributes.addFlashAttribute(
                "mensajeError",
                e.getMessage()
            );
        }

        return "redirect:/audiencias";
    }

}
