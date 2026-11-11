
package pe.edu.sajv.sajv_qos.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalController {

    private final UsuarioRepository usuarioRepository;

    @ModelAttribute
    public void agregarDatosUsuario(
            Model model,
            Authentication authentication) {

        if (authentication == null ||
            !authentication.isAuthenticated() ||
            authentication.getName().equals("anonymousUser")) {
            return;
        }

        usuarioRepository.findByUsuario(authentication.getName())
            .ifPresent(usuario -> {

                String nombreCompleto =
                    (usuario.getNombre() != null
                        ? usuario.getNombre() : "")
                    + " "
                    + (usuario.getApellidos() != null
                        ? usuario.getApellidos() : "");

                model.addAttribute(
                    "usuarioNombre",
                    nombreCompleto.trim()
                );

                model.addAttribute(
                    "usuarioCorreo",
                    usuario.getCorreo()
                );
            });

        boolean esAdministrador =
            tieneRol(authentication, "ROLE_ADMINISTRADOR");

        boolean esOperador =
            tieneRol(authentication, "ROLE_OPERADOR");

        boolean esConsulta =
            tieneRol(authentication, "ROLE_CONSULTA");

        String nombreRol = esAdministrador ? "Administrador"
                : esOperador ? "Operador"
                : esConsulta ? "Consulta"
                : "Sin rol";

        model.addAttribute("usuarioRol", nombreRol);
        model.addAttribute("esAdministrador", esAdministrador);
        model.addAttribute("esOperador", esOperador);
        model.addAttribute("esConsulta", esConsulta);
    }

    private boolean tieneRol(
            Authentication authentication,
            String rol) {

        return authentication.getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(autoridad -> autoridad.equals(rol));
    }
}
