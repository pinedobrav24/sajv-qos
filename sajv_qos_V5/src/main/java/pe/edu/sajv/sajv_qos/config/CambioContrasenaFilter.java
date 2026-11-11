
package pe.edu.sajv.sajv_qos.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;

@Component
@RequiredArgsConstructor
public class CambioContrasenaFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI()
                .substring(request.getContextPath().length());

        return path.equals("/login")
                || path.equals("/logout")
                || path.equals("/mi-contrasena")
                || path.equals("/error")
                || path.equals("/favicon.ico")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/vendor/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {

            boolean pendiente = usuarioRepository.findByUsuario(auth.getName())
                    .map(u -> Integer.valueOf(1)
                            .equals(u.getDebeCambiarContrasena()))
                    .orElse(false);

            if (pendiente) {
                response.sendRedirect(
                        request.getContextPath() + "/mi-contrasena");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
