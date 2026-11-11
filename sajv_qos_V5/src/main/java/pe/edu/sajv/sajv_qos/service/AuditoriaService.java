package pe.edu.sajv.sajv_qos.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import pe.edu.sajv.sajv_qos.model.Auditoria;
import pe.edu.sajv.sajv_qos.model.Usuario;
import pe.edu.sajv.sajv_qos.repository.AuditoriaRepository;
import pe.edu.sajv.sajv_qos.repository.UsuarioRepository;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private static final ZoneId ZONA_PERU = ZoneId.of("America/Lima");

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void registrar(String modulo, String accion, String descripcion) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String nombreUsuario = auth != null && auth.isAuthenticated()
                ? auth.getName() : "SISTEMA";
        registrarComo(nombreUsuario, modulo, accion, descripcion);
    }

    @Transactional
    public void registrarComo(String nombreUsuario, String modulo, String accion, String descripcion) {
        Auditoria registro = new Auditoria();
        registro.setUsuario(limitar(nombreUsuario == null ? "SISTEMA" : nombreUsuario, 50));
        registro.setUsuarioId(usuarioRepository.findByUsuario(registro.getUsuario())
                .map(Usuario::getId).orElse(null));
        registro.setModulo(limitar(modulo, 50));
        registro.setAccion(limitar(accion, 50));
        registro.setDescripcion(limitar(descripcion, 500));
        registro.setIp(obtenerIpCliente());
        registro.setFecha(LocalDateTime.now(ZONA_PERU));
        auditoriaRepository.save(registro);
    }

    @Transactional
    public void registrarSistema(String modulo, String accion, String descripcion) {
        registrarComo("SISTEMA", modulo, accion, descripcion);
    }

    @Transactional(readOnly = true)
    public Page<Auditoria> buscar(String usuario, String modulo, String accion,
            LocalDate desde, LocalDate hasta, Pageable pageable) {
        String filtroUsuario = normalizar(usuario);
        String filtroModulo = normalizar(modulo);
        String filtroAccion = normalizar(accion);
        LocalDateTime fechaDesde = desde == null ? null : desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta == null ? null : hasta.atTime(LocalTime.MAX);
        return auditoriaRepository.buscarConFiltros(
                filtroUsuario, filtroModulo, filtroAccion,
                fechaDesde, fechaHasta, pageable);
    }

    private String obtenerIpCliente() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs)) {
            return null;
        }
        HttpServletRequest request = attrs.getRequest();
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = forwarded != null && !forwarded.isBlank()
                ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
        return limitar(ip, 45);
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private String limitar(String valor, int maximo) {
        if (valor == null) return null;
        String limpio = valor.trim();
        return limpio.length() <= maximo ? limpio : limpio.substring(0, maximo);
    }
}
